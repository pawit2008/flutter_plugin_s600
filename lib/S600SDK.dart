import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class S600SDK {
  static const MethodChannel _channel = MethodChannel('thai_id_card_reader');

  static Future<Map<String, dynamic>> readThaiIDCard() async {
    try {
      if (kDebugMode) {
        print('✅ Card Data:');
      }
      final Map<dynamic, dynamic> result =
          await _channel.invokeMethod('readThaiIDCard');
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      throw Exception("L14 Failed to read ID card: ${e.message}");
    }
  }

  static Future<void> printerTest() async {
    try {
      if (kDebugMode) {
        print('✅ Printer testing..');
      }
      await _channel.invokeMethod('printerTest');
    } on PlatformException catch (e) {
      throw Exception("L14 Failed to read ID card: ${e.message}");
    }
  }
}
