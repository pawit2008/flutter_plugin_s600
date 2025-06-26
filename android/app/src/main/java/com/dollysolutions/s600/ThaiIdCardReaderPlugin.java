package com.dollysolutions.s600;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dollysolutions.s600.ICCardActivity;
import com.dollysolutions.s600.PrinterUtil;
import com.dollysolutions.s600.SmartPosApplication;
import com.kp.ktsdkservice.printer.AidlPrinter;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.BinaryMessenger;

public class ThaiIdCardReaderPlugin implements MethodChannel.MethodCallHandler {
  private static final String TAG = "ThaiPlugin";
  private final Context context;
  private final MethodChannel channel;
  private final ICCardActivity icCardActivity;

  public ThaiIdCardReaderPlugin(Context context, BinaryMessenger messenger) {
    this.context = context;
    this.channel = new MethodChannel(messenger, "thai_id_card_reader");
    this.channel.setMethodCallHandler(this);
    this.icCardActivity = new ICCardActivity();
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
      Map<String, String> cardData = icCardActivity.readThaiIDCard();
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
