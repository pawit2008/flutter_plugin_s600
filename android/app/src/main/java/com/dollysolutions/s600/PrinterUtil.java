package com.dollysolutions.s600;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import java.io.IOException;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.dollysolutions.s600.R;

import com.kp.ktsdkservice.printer.AidlPrinterListener;
import com.kp.ktsdkservice.printer.PrintItemObj;
import com.kp.ktsdkservice.service.AidlDeviceService;
import com.kp.ktsdkservice.printer.AidlPrinter;

import java.io.ByteArrayOutputStream;
import android.os.RemoteException;

public class PrinterUtil {

    public static void printImageFromAssets(Context context, AidlPrinter printer, String assetFileName) {
        try {
            InputStream is = context.getAssets().open(assetFileName);
            Bitmap originalBitmap = BitmapFactory.decodeStream(is);

            if (originalBitmap == null) {
                Log.e("PrinterUtil", "❌ Failed to decode PNG asset");
                return;
            }

            // ✅ แปลงเป็น ARGB_8888 (ปลอดภัยสำหรับการพิมพ์)
            Bitmap safeBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

            // ✅ เติมพื้นหลังขาว เผื่อมี transparency ซ่อนอยู่
            Bitmap finalBitmap = Bitmap.createBitmap(
                    safeBitmap.getWidth(),
                    safeBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(finalBitmap);
            canvas.drawColor(Color.WHITE); // พื้นหลังขาว
            canvas.drawBitmap(safeBitmap, 0, 0, null);

            // ✅ Resize ให้กว้างพอดี printer (384 px)
            int targetWidth = 384;
            int targetHeight = (int) ((float) finalBitmap.getHeight() / finalBitmap.getWidth() * targetWidth);
            Bitmap resized = Bitmap.createScaledBitmap(finalBitmap, targetWidth, targetHeight, true);

            // ✅ พิมพ์
            printer.printBmp(0, resized.getWidth(), resized.getHeight(), resized, new AidlPrinterListener.Stub() {
                @Override
                public void onPrintFinish() throws RemoteException {
                    Log.d("PrinterUtil", "✅ Print finished");
                    printer.prnStart();
                    printer.printClose();
                }

                @Override
                public void onError(int code) throws RemoteException {
                    Log.e("PrinterUtil", "❌ Print error: " + code);
                }
            });

        } catch (IOException | RemoteException e) {
            Log.e("PrinterUtil", "❌ Exception during PNG print", e);
        }
    }

    public static void printReceipt(Context context, AidlPrinter aidlPrinter) {
        Log.d("PrinterUtil", "🖨️ printReceipt called");

        try {
            PrinterUtil.printImageFromAssets(context, aidlPrinter, "dpark-logo-new.png");

            aidlPrinter.printText(new ArrayList<PrintItemObj>() {
                {
                    add(new PrintItemObj("(English) what are you doing now", 24, true));
                    add(new PrintItemObj("รายการรถเข้าออก", 24, true));

                    // add(new PrintItemObj("默认打印数据测试"));
                    // add(new PrintItemObj("打印数据字体放大", 24));
                    // add(new PrintItemObj("打印数据加粗", 8, true));
                    // add(new PrintItemObj("打印数据左对齐测试", 8, false, PrintItemObj.ALIGN.LEFT));
                    // add(new PrintItemObj("打印数据居中对齐测试", 8, false, PrintItemObj.ALIGN.CENTER));
                    // add(new PrintItemObj("打印数据右对齐测试", 8, false, PrintItemObj.ALIGN.RIGHT));
                    // add(new PrintItemObj("打印数据下划线", 8, false, PrintItemObj.ALIGN.LEFT, true));

                    // add(new PrintItemObj("打印数据不换行测试打印数据不换行测试打印数据不换行测试", 8, false,
                    // PrintItemObj.ALIGN.LEFT, false,
                    // false));
                    // add(new PrintItemObj("打印数据不换行测试", 8, false, PrintItemObj.ALIGN.LEFT, false,
                    // false));
                    // add(new PrintItemObj("打印数据行间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false,
                    // true, 40));
                    // add(new PrintItemObj("打印数据行间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false,
                    // true, 83));
                    // add(new PrintItemObj("打印数据行间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false,
                    // true, 40));
                    // add(new PrintItemObj("打印数据字符间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false,
                    // true, 29, 25));
                    // add(new PrintItemObj("打印数据字符间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false,
                    // true, 29, 25));
                    // add(new PrintItemObj("打印数据字符间距测试\r\n\r\n\r\n", 8, false,
                    // PrintItemObj.ALIGN.LEFT, false, true, 29,
                    // 25));

                }
            }, new AidlPrinterListener.Stub() {
                @Override
                public void onPrintFinish() throws RemoteException {
                    aidlPrinter.prnStart();
                    aidlPrinter.printClose();
                    // String endTime = getCurTime();
                    // showMessage(getResources().getString(R.string.print_end_time) + endTime);
                }

                @Override
                public void onError(int arg0) throws RemoteException {
                    // showMessage(getResources().getString(R.string.print_error_code) + arg0);
                }
            });

        } catch (Exception e) {
            Log.e("PrinterUtil", "❌ Exception in printReceipt", e);
        }
    }

}
