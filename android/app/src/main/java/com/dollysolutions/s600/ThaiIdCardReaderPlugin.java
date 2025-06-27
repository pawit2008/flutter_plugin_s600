package com.dollysolutions.s600;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kp.ktsdkservice.printer.AidlPrinter;
import com.kp.ktsdkservice.printer.PrintItemObj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import om.kp.ktsdkservice.iccard.AidlICCard;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/** ThaiIdCardReaderPlugin */
public class ThaiIdCardReaderPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {
  private static final String TAG = "ThaiPlugin";
  private MethodChannel channel;
  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.context = flutterPluginBinding.getApplicationContext();
    this.channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "thai_id_card_reader");
    this.channel.setMethodCallHandler(this);
    Log.d(TAG, "✅ Plugin registered via FlutterPlugin");
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (channel != null) {
      channel.setMethodCallHandler(null);
    }
    channel = null;
    context = null;
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

  private void handleReadThaiIDCard(MethodChannel.Result result) {
    try {
      SmartPosApplication app = SmartPosApplication.getInstance();
      AidlICCard icCard = null;
      int retry = 0;
      while (retry < 5 && icCard == null) {
        icCard = app.aidlICCard;
        Thread.sleep(500);
        retry++;
      }

      if (icCard == null) {
        result.error("ICCARD_NULL", "ICCard service not available", null);
        return;
      }

      Map<String, String> cardData = ICCardActivity.readThaiIDCard(icCard);
      if (cardData != null && !cardData.isEmpty()) {
        result.success(cardData);
      } else {
        result.error("READ_ERROR", "No data received from ID card", null);
      }

    } catch (Exception e) {
      Log.e(TAG, "Exception reading ID card", e);
      result.error("READ_ERROR", e.getMessage(), null);
    }
  }

  private void handlePrintQrCode(MethodCall call, MethodChannel.Result result) {
    String data = call.argument("barCodeData");
    try {
      AidlPrinter printer = SmartPosApplication.getInstance().printer;
      if (printer == null) {
        result.error("PRINTER_NULL", "Printer not available", null);
        return;
      }
      PrinterUtil.printQrCode(context, printer, data);
      result.success("QrCode printed");
    } catch (Exception e) {
      result.error("EXCEPTION", e.getMessage(), null);
    }
  }

  private void handlePrintBarCode(MethodCall call, MethodChannel.Result result) {
    String data = call.argument("barCodeData");
    try {
      AidlPrinter printer = SmartPosApplication.getInstance().printer;
      if (printer == null) {
        result.error("PRINTER_NULL", "Printer not available", null);
        return;
      }
      PrinterUtil.printBarCode(context, printer, data);
      result.success("BarCode printed");
    } catch (Exception e) {
      result.error("EXCEPTION", e.getMessage(), null);
    }
  }

  private void handlePrintText(MethodCall call, MethodChannel.Result result) {
    List<Map<String, Object>> items = call.argument("items");
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

    try {
      AidlPrinter printer = SmartPosApplication.getInstance().printer;
      if (printer == null) {
        result.error("PRINTER_NULL", "Printer not available", null);
        return;
      }
      PrinterUtil.printText(context, printer, printList);
      result.success("Text printed");
    } catch (Exception e) {
      result.error("PRINT_FAIL", e.getMessage(), null);
    }
  }

  private void handlePrintImage(MethodCall call, MethodChannel.Result result) {
    String base64 = call.argument("imageBase64");
    try {
      byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
      Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
      if (bitmap == null) {
        result.error("DECODE_ERROR", "Image decode failed", null);
        return;
      }

      AidlPrinter printer = SmartPosApplication.getInstance().printer;
      if (printer == null) {
        result.error("PRINTER_NULL", "Printer not available", null);
        return;
      }

      PrinterUtil.printBitmap(context, printer, bitmap);
      result.success("Image printed");
    } catch (Exception e) {
      result.error("PRINT_IMAGE_FAIL", e.getMessage(), null);
    }
  }

  private void handlePrinterTest(MethodChannel.Result result) {
    try {
      AidlPrinter printer = SmartPosApplication.getInstance().printer;
      if (printer == null) {
        result.error("PRINTER_NULL", "Printer not available", null);
        return;
      }
      PrinterUtil.printReceipt(context, printer);
      result.success("Test print completed");
    } catch (Exception e) {
      result.error("PRINT_TEST_FAIL", e.getMessage(), null);
    }
  }
}
