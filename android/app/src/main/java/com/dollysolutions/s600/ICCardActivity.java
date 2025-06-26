package com.dollysolutions.s600;

import java.io.UnsupportedEncodingException;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import android.util.Log;
import java.util.Arrays;
import android.content.Context;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.OutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import java.util.Map;
import java.util.HashMap;

import com.kp.ktsdkservice.data.APDU_RESP;
import com.kp.ktsdkservice.data.APDU_SEND;

import om.kp.ktsdkservice.iccard.AidlICCard;
import android.app.Activity;

/**
 *
 * 
 * @author Administrator
 * 
 */

public class ICCardActivity extends Activity {
	com.kp.ktsdkservice.data.APDU_RESP resp;

	public static String readField(AidlICCard iccard, byte p1, byte p2, int length) throws RemoteException, UnsupportedEncodingException {
		APDU_SEND send = new APDU_SEND();
		send.Command = new byte[] { (byte) 0x80, (byte) 0xB0, p1, p2 };
		send.Lc = 2;
		send.DataIn[0] = 0x00;
		send.DataIn[1] = (byte) length;
		send.Le = 0;

		APDU_RESP resp = iccard.iccIsoCommand((byte) 0, send);
		if (resp != null && resp.SWA == (byte) 0x90 && resp.SWB == (byte) 0x00) {
			return new String(resp.DataOut, "TIS-620").replace('#', ' ').trim();
		} else {
			return "[Error SW=" + byteToHex(resp.SWA) + byteToHex(resp.SWB) + "]";
		}
	}

	public static byte[] readPhoto(AidlICCard iccard) throws RemoteException, InterruptedException {
		ByteArrayOutputStream photo = new ByteArrayOutputStream();

		int[][] photoOffsets = {
				{ 0x01, 0x7B }, { 0x02, 0x7A }, { 0x03, 0x79 }, { 0x04, 0x78 },
				{ 0x05, 0x77 }, { 0x06, 0x76 }, { 0x07, 0x75 }, { 0x08, 0x74 },
				{ 0x09, 0x73 }, { 0x0A, 0x72 }, { 0x0B, 0x71 }, { 0x0C, 0x70 },
				{ 0x0D, 0x6F }, { 0x0E, 0x6E }, { 0x0F, 0x6D }, { 0x10, 0x6C },
				{ 0x11, 0x6B }, { 0x12, 0x6A }, { 0x13, 0x69 }, { 0x14, 0x68 }
		};

		for (int i = 0; i < photoOffsets.length; i++) {
			int p1 = photoOffsets[i][0];
			int p2 = photoOffsets[i][1];

			// READ BINARY command
			APDU_SEND readSend = new APDU_SEND();
			readSend.Command = new byte[] { (byte) 0x80, (byte) 0xB0, (byte) p1, (byte) p2 };
			readSend.Lc = 2;
			readSend.DataIn[0] = 0x00;
			readSend.DataIn[1] = (byte) 0xFF;
			readSend.Le = 0;
			// Thread.sleep(200);
			APDU_RESP resp = iccard.iccIsoCommand((byte) 0, readSend);
			if (resp == null || resp.SWA != (byte) 0x90) {
				Log.w("Photo", "Block " + (i + 1) + ": read failed (null response)");
				break;
			}
			if (resp.SWA == (byte) 0x90) {
				// StringBuilder sb = new StringBuilder();
				// for (int j = 0; j < 256; j++) {
				// sb.append(String.format("%02X", resp.DataOut[j])).append(" ");
				// }
				// Log.d("PhotoData", sb.toString().trim());
				photo.write(resp.DataOut, 0, resp.DataOut.length - 257);
			}

			else {
				Log.w("Photo", "READ BINARY failed at block " + (i + 1) +
						" SW=" + byteToHex(resp.SWA) + byteToHex(resp.SWB));
				continue;
			}
		}
		byte[] full = photo.toByteArray();
		int end = findEOI(full);
		if (end > 0) {
			return Arrays.copyOfRange(full, 0, end + 2); // include FF D9
		}
		return full;
	}

	private int findEOI(byte[] data) {
		for (int i = data.length - 2; i >= 0; i--) {
			if ((data[i] & 0xFF) == 0xFF && (data[i + 1] & 0xFF) == 0xD9) {
				return i;
			}
		}
		return -1;
	}

	private byte[] trimJpeg(byte[] data) {
		for (int i = data.length - 2; i >= 0; i--) {
			if ((data[i] & 0xFF) == 0xFF && (data[i + 1] & 0xFF) == 0xD9) {
				return Arrays.copyOfRange(data, 0, i + 2); // ครอบคลุมถึง FF D9
			}
		}
		return data; // fallback ถ้าไม่เจอ
	}

	private boolean hasJpegMarkers(byte[] data) {
		for (int i = 0; i < data.length - 1; i++) {
			if ((data[i] & 0xFF) == 0xFF && (data[i + 1] & 0xFF) == 0xD8) {
				for (int j = i + 2; j < data.length - 1; j++) {
					if ((data[j] & 0xFF) == 0xFF && (data[j + 1] & 0xFF) == 0xD9) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void savePhotoToGallery(Context context, byte[] photoData) {
		String fileName = "idcard_photo_" + System.currentTimeMillis() + ".jpg";

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ThaiIDCard");

		ContentResolver resolver = context.getContentResolver();
		Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		try {
			if (imageUri != null) {
				OutputStream outputStream = resolver.openOutputStream(imageUri);
				if (outputStream != null) {
					outputStream.write(photoData);
					outputStream.close();
					Log.d("PhotoSave", "Saved to gallery: " + imageUri.toString());
				}
			}
		} catch (IOException e) {
			Log.e("PhotoSave", "Error saving image", e);
		}
	}

	// 00A404000E315041592E5359532E4444463031
	public static Map<String, String> readThaiIDCard(AidlICCard iccard) {
		try {
			if (iccard != null) {
				byte[] data = iccard.iccInit((byte) 0);
				if (data != null) {
					// showMessage(getResources().getString(R.string.ic_card_open_success) +
					// util.hex2Str(data));
				} else {
					// showMessage(getResources().getString(R.string.ic_card_open_fail));
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String> result = new HashMap<>();
		APDU_SEND send = new APDU_SEND();
		APDU_RESP resp;

		try {
			// 1. SELECT AID
			send.Command = new byte[] { 0x00, (byte) 0xA4, 0x04, 0x00 };
			send.Lc = 8;
			send.Le = 0;
			byte[] aid = { (byte) 0xA0, 0x00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01 };
			System.arraycopy(aid, 0, send.DataIn, 0, 8);
			resp = iccard.iccIsoCommand((byte) 0, send);

			if (resp == null || (resp.SWA != (byte) 0x90 && resp.SWA != (byte) 0x61)) {
				result.put("error", "SELECT AID failed: " + byteToHex(resp.SWA) + byteToHex(resp.SWB));
				return result;
			}

			// (optional) Handle GET RESPONSE
			if (resp.SWA == (byte) 0x61) {
				send.Command = new byte[] { 0x00, (byte) 0xC0, 0x00, 0x00 };
				send.Lc = 0;
				send.Le = resp.SWB;
				resp = iccard.iccIsoCommand((byte) 0, send);
			}

			Thread.sleep(200);

			// Read all fields
			result.put("CID", readField((byte) 0x00, (byte) 0x04, 13));
			result.put("FullnameTH", readField((byte) 0x00, (byte) 0x11, 100));
			result.put("FullnameEN", readField((byte) 0x00, (byte) 0x75, 100));
			result.put("DateOfBirth", readField((byte) 0x00, (byte) 0xD9, 8));
			result.put("Gender", readField((byte) 0x00, (byte) 0xE1, 1));
			result.put("Issuer", readField((byte) 0x00, (byte) 0xF6, 100));
			result.put("IssueDate", readField((byte) 0x01, (byte) 0x67, 8));
			result.put("ExpireDate", readField((byte) 0x01, (byte) 0x6F, 8));
			result.put("Address", readField((byte) 0x15, (byte) 0x79, 100));

			// Optional: load photo as base64
			byte[] fullPhoto = readPhoto();

			byte[] jpegPhoto = trimJpeg(fullPhoto);

			boolean isJpegValid = hasJpegMarkers(jpegPhoto);
			Log.d("JPEG_CHECK", "JPEG valid: " + isJpegValid);

			String base64 = Base64.encodeToString(jpegPhoto, Base64.NO_WRAP);
			// Log.d("PhotoBase64-Corrected", base64);

			Log.d("PhotoData", "Length = " + jpegPhoto.length);
			Log.d("PhotoData", String.format("First4= %02X %02X %02X %02X",
					jpegPhoto[0], jpegPhoto[1], jpegPhoto[2], jpegPhoto[3]));
			Log.d("PhotoData", String.format("Last4= %02X %02X %02X %02X",
					jpegPhoto[jpegPhoto.length - 4],
					jpegPhoto[jpegPhoto.length - 3],
					jpegPhoto[jpegPhoto.length - 2],
					jpegPhoto[jpegPhoto.length - 1]));

			// savePhotoToGallery(this, jpegPhoto);
			result.put("Photo", base64);
			Thread.sleep(200);
			iccard.iccClose((byte) 0);

		} catch (Exception e) {
			result.put("exception", e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	private static String byteToHex(byte b) {
		return String.format("%02X", b & 0xFF);
	}

}
