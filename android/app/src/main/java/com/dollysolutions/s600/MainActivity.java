package com.example.thai_id_card_reader_app;

import android.os.Bundle;
import android.util.Log;

import com.dollysolutions.s600.ICCardActivity;
import com.dollysolutions.s600.PrintDevActivity;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "thai_id_card_reader";

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    if (call.method.equals("readThaiIDCard")) {
                        try {
                            Log.d("MainActivity", "readThaiIDCard called");

                            ICCardActivity reader = new ICCardActivity();

                            Map<String, String> cardData = reader.readThaiIDCard(); // เรียกจริงจาก SDK
                            result.success(cardData);
                        } catch (Exception e) {
                            Log.e("MainActivity", "Read failed: " + e.getMessage());
                            result.error("READ_FAIL", "Error reading card", e.getMessage());
                        }
                    } else {
                        result.notImplemented();
                    }
                });
    }
}
