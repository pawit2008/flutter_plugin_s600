import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class S600SDK {
  static const MethodChannel _channel = MethodChannel('thai_id_card_reader', JSONMethodCodec());

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

  static Future<String> printerTest() async {
    try {
      print('🔄 Starting printer test...');
      final result = await _channel.invokeMethod('printerTest');
      print('✅ Printer test completed with result: $result');
      return result;
    } on PlatformException catch (e) {
      print('❌ Printer test failed: ${e.message}');
      print('❌ Details: ${e.details}');
      print('❌ Stacktrace: ${e.stacktrace}');
      throw Exception("Failed to test printer: ${e.message}");
    } catch (e) {
      print('❌ Unexpected error in printerTest: $e');
      print('❌ Error type: ${e.runtimeType}');
      if (e is MissingPluginException) {
        print('❌ Missing plugin implementation!');
      }
      rethrow;
    }
  }
}
