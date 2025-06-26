package com.dollysolutions.s600;

import android.os.Bundle;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;

import com.dollysolutions.s600.ThaiIdCardReaderPlugin;

public class MainActivity extends FlutterActivity {
    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        // ✅ Manual register plugin
        new ThaiIdCardReaderPlugin(
                getApplicationContext(),
                flutterEngine.getDartExecutor().getBinaryMessenger());
    }
}
