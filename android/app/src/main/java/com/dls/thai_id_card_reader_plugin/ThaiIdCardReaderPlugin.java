package com.dls.thai_id_card_reader_plugin;

import android.content.Context;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import android.util.Log;
import com.kp.ktsdkservice.printer.AidlPrinterListener;
import com.kp.ktsdkservice.printer.PrintItemObj;
import com.kp.ktsdkservice.service.AidlDeviceService;
import com.kp.ktsdkservice.printer.AidlPrinter;

import com.dollysolutions.s600.ICCardActivity;

import com.dollysolutions.s600.SmartPosApplication;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/** ThaiIdCardReaderPlugin */
public class ThaiIdCardReaderPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {
  private static final String TAG = "ThaiPlugin";

  private Context context;
  private ICCardActivity icCardActivity;
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext();
    icCardActivity = new ICCardActivity();

    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "thai_id_card_reader");
    channel.setMethodCallHandler(this);

    Log.d(TAG, "✅ Plugin attached to engine");
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
      Log.d(TAG, "1️⃣ printerTest method called - entry point");
      
      // Verify channel is working
      if (channel == null) {
        Log.e(TAG, "❌ Channel is null!");
        result.error("CHANNEL_NULL", "Method channel not initialized", null);
        return;
      }
      Log.d(TAG, "2️⃣ Channel verification passed");

      // Check application instance
      SmartPosApplication app = SmartPosApplication.getInstance();
      if (app == null) {
        Log.e(TAG, "❌ SmartPosApplication instance is null!");
        result.error("APP_NULL", "Application instance not available", null);
        return;
      }
      Log.d(TAG, "3️⃣ Application instance verified");

      // Add delay to ensure service is bound
      int retryCount = 0;
      AidlPrinter printer = null;
      
      while (retryCount < 5 && printer == null) {
        printer = SmartPosApplication.getInstance().printer;
        if (printer == null) {
          Log.d(TAG, "Waiting for printer service to bind...");
          Thread.sleep(500);
          retryCount++;
        }
      }

      if (printer == null) {
        Log.e(TAG, "❌ AidlPrinter is NULL after retries");
        result.error("PRINTER_NULL", "Printer service not available after retries", null);
        return;
      }

      Log.d(TAG, "📤 Printer service available - printing test receipt");
      PrinterUtil.printReceipt(context, printer);

      result.success("Printed test receipt successfully");

    } catch (Exception e) {
      Log.e(TAG, "❌ Exception during printerTest", e);
      result.error("PRINT_ERROR", "Failed to print: " + e.getMessage(), null);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    Log.d(TAG, "❎ Plugin detached from engine");
  }
}
