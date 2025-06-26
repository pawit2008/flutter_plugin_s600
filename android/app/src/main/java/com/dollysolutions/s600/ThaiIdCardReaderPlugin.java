package com.dollysolutions.s600;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dollysolutions.s600.ICCardActivity;
import com.dollysolutions.s600.PrinterUtil;
import com.dollysolutions.s600.SmartPosApplication;
import com.kp.ktsdkservice.printer.AidlPrinter;
import com.kp.ktsdkservice.printer.PrintItemObj;
import om.kp.ktsdkservice.iccard.AidlICCard;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.BinaryMessenger;

public class ThaiIdCardReaderPlugin implements MethodChannel.MethodCallHandler {
  private static final String TAG = "ThaiPlugin";
  private final Context context;
  private final MethodChannel channel;

  public ThaiIdCardReaderPlugin(Context context, BinaryMessenger messenger) {
    this.context = context;
    this.channel = new MethodChannel(messenger, "thai_id_card_reader");
    this.channel.setMethodCallHandler(this);
    Log.d(TAG, "✅ Plugin manually registered");
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;

      case "readThaiIDCard":
        handleReadThaiIDCard(result);
        break;
      case "printQrCode":
        handlePrintQrCode(call, result);
        break;
      case "printBarCode":
        handlePrintBarCode(call, result);
        break;
      case "printText":
        Log.d(TAG, "handlePrintText Start");
        handlePrintText(call, result);
        break;
      case "printImage":
        handlePrintImage(call, result);
        break;
      case "printerTest":
        handlePrinterTest(result);
        break;

      default:
        result.notImplemented();
        break;
    }
  }

  private void handlePrintQrCode(MethodCall call, MethodChannel.Result result) {
    String data = call.argument("barCodeData");
    if (data == null || data.isEmpty()) {
      result.error("INVALID", " data missing", null);
      return;
    }
    try {

      AidlPrinter printer = SmartPosApplication.getInstance().printer;
      if (printer == null) {
        result.error("PRINTER_NULL", "Printer not available", null);
        return;
      }

      PrinterUtil.printQrCode(context, printer, data); // ใช้ method ที่เราทำไว้
      result.success("QrCode printed");
    } catch (Exception e) {
      result.error("EXCEPTION", "Exception: " + e.getMessage(), null);
    }
  }

  private void handlePrintBarCode(MethodCall call, MethodChannel.Result result) {
    String data = call.argument("barCodeData");
    if (data == null || data.isEmpty()) {
      result.error("INVALID", " data missing", null);
      return;
    }
    try {

      AidlPrinter printer = SmartPosApplication.getInstance().printer;
      if (printer == null) {
        result.error("PRINTER_NULL", "Printer not available", null);
        return;
      }

      PrinterUtil.printBarCode(context, printer, data); // ใช้ method ที่เราทำไว้
      result.success("BarCode printed");
    } catch (Exception e) {
      result.error("EXCEPTION", "Exception: " + e.getMessage(), null);
    }
  }

  private void handlePrintText(MethodCall call, MethodChannel.Result result) {
    List<Map<String, Object>> items = call.argument("items");

    if (items == null || items.isEmpty()) {
      Log.d(TAG, "No items to print");
      result.error("INVALID", "No items to print", null);
      return;
    }
    Log.d(TAG, "Print Prepairing..");
    ArrayList<PrintItemObj> printList = new ArrayList<>();

    for (Map<String, Object> item : items) {
      String text = (String) item.get("text");
      int fontSize = item.get("fontSize") != null ? ((Number) item.get("fontSize")).intValue() : 8;
      boolean bold = item.get("bold") != null && (boolean) item.get("bold");
      boolean underline = item.get("underline") != null && (boolean) item.get("underline");
      String alignStr = (String) item.get("align");
      int lineSpacing = item.get("lineSpacing") != null ? ((Number) item.get("lineSpacing")).intValue() : -1;
      int letterSpacing = item.get("letterSpacing") != null ? ((Number) item.get("letterSpacing")).intValue() : -1;

      PrintItemObj.ALIGN align = PrintItemObj.ALIGN.LEFT;
      if ("CENTER".equalsIgnoreCase(alignStr))
        align = PrintItemObj.ALIGN.CENTER;
      else if ("RIGHT".equalsIgnoreCase(alignStr))
        align = PrintItemObj.ALIGN.RIGHT;

      PrintItemObj obj;

      if (lineSpacing != -1 && letterSpacing != -1) {
        obj = new PrintItemObj(text, fontSize, bold, align, underline, true, lineSpacing, letterSpacing);
      } else if (lineSpacing != -1) {
        obj = new PrintItemObj(text, fontSize, bold, align, underline, true, lineSpacing);
      } else if (alignStr != null || underline) {
        obj = new PrintItemObj(text, fontSize, bold, align, underline);
      } else if (fontSize != 8 || bold) {
        obj = new PrintItemObj(text, fontSize, bold);
      } else {
        obj = new PrintItemObj(text);
      }

      printList.add(obj);
    }
    Log.d(TAG, "Print Starting..");
    AidlPrinter printer = SmartPosApplication.getInstance().printer;
    if (printer == null) {
      result.error("PRINTER_NULL", "Printer is not ready", null);
      return;
    }
    Log.d(TAG, "Print Printing..");
    PrinterUtil.printText(context, printer, printList);
    result.success("Printed text list");
  }

  private void handlePrintImage(MethodCall call, MethodChannel.Result result) {
    String base64 = call.argument("imageBase64");
    if (base64 == null || base64.isEmpty()) {
      result.error("INVALID", "Image data missing", null);
      return;
    }

    try {
      byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
      Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

      if (bitmap == null) {
        result.error("DECODE_ERROR", "Failed to decode base64 image", null);
        return;
      }

      AidlPrinter printer = SmartPosApplication.getInstance().printer;
      if (printer == null) {
        result.error("PRINTER_NULL", "Printer not available", null);
        return;
      }

      PrinterUtil.printBitmap(context, printer, bitmap); // ใช้ method ที่เราทำไว้
      result.success("Image printed");
    } catch (Exception e) {
      result.error("EXCEPTION", "Exception: " + e.getMessage(), null);
    }
  }

  private void handleReadThaiIDCard(MethodChannel.Result result) {
    try {
      SmartPosApplication app = SmartPosApplication.getInstance();
      if (app == null) {
        result.error("APP_NULL", "SmartPosApplication instance not available", null);
        return;
      }

      AidlICCard icCard = null;
      int retry = 0;
      while (retry < 5 && icCard == null) {
        icCard = app.aidlICCard;
        if (icCard == null) {
          Log.d(TAG, "⌛ Waiting for ICCard service...");
          Thread.sleep(500);
          retry++;
        }
      }

      if (icCard == null) {
        result.error("ICCARD_NULL", "ICCard service not available after retries", null);
        return;
      }

      Map<String, String> cardData = ICCardActivity.readThaiIDCard(icCard);

      if (cardData != null && !cardData.isEmpty()) {
        result.success(cardData);
      } else {
        result.error("READ_ERROR", "Failed to read ID card: No data received", null);
      }
    } catch (Exception e) {
      Log.e(TAG, "❌ Exception reading ID card", e);
      result.error("READ_ERROR", "Exception reading ID card: " + e.getMessage(), null);
    }
  }

  private void handlePrinterTest(MethodChannel.Result result) {
    try {
      Log.d(TAG, "🖨️ Starting printer test...");

      SmartPosApplication app = SmartPosApplication.getInstance();
      if (app == null) {
        result.error("APP_NULL", "SmartPosApplication instance not available", null);
        return;
      }

      AidlPrinter printer = null;
      int retry = 0;
      while (retry < 5 && printer == null) {
        printer = app.printer;
        if (printer == null) {
          Log.d(TAG, "⌛ Waiting for printer service...");
          Thread.sleep(500);
          retry++;
        }
      }

      if (printer == null) {
        result.error("PRINTER_NULL", "Printer service not available after retries", null);
        return;
      }

      PrinterUtil.printReceipt(context, printer);
      result.success("Printed test receipt successfully");

    } catch (Exception e) {
      Log.e(TAG, "❌ Exception during printerTest", e);
      result.error("PRINT_ERROR", "Failed to print: " + e.getMessage(), null);
    }
  }
}
