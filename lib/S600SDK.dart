import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/widgets.dart';
import 'package:image/image.dart' as img;

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

  static Future<String> printerTest() async {
    try {
      final result = await _channel.invokeMethod('printerTest');
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

  static Future<void> printQrCode(String qrData) async {
    await _channel.invokeMethod('printQrCode', {
      'barCodeData': qrData,
    });
  }

  static Future<void> printBarCode(String qrData) async {
    await _channel.invokeMethod('printBarCode', {
      'barCodeData': qrData,
    });
  }

  static Future<void> printText(List<Map<String, dynamic>> textList) async {
    await _channel.invokeMethod('printText', {
      'items': textList,
    });
  }

  static Future<void> printImage(Uint8List imageBytes) async {
    final base64Image = base64Encode(imageBytes);
    await _channel.invokeMethod('printImage', {
      'imageBase64': base64Image,
    });
  }
}
