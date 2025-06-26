import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'S600SDK.dart'; // Ensure this matches your file structure

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'S600 Flutter Tester',
      home: S600Screen(),
    );
  }
}

class S600Screen extends StatefulWidget {
  const S600Screen({super.key});

  @override
  _S600ScreenState createState() => _S600ScreenState();
}

class _S600ScreenState extends State<S600Screen> {
  Map<String, dynamic>? _cardData;
  String _error = '';

  Future<void> _readCard() async {
    try {
      final data = await S600SDK.readThaiIDCard();
      setState(() {
        _cardData = data;
        _error = '';
      });
    } catch (e) {
      setState(() {
        _cardData = null;
        _error = e.toString();
      });
    }
  }

  Future<void> _printCardData() async {
    try {
      print('Printing card data...');
      final ByteData data = await rootBundle.load('assets/dpark-logo-new.png');
      final Uint8List bytes = data.buffer.asUint8List();
      // await S600SDK.printImage(bytes);
      // await S600SDK.printQrCode('test');

      // await S600SDK.printBarCode('test');

      final List<Map<String, dynamic>> items = [
        {"text": "Dpark"},
        {"text": "ใบรับรถ", "fontSize": 24},
        {"text": "ใบเสร็จ", "fontSize": 8, "bold": true},
        {"text": "เวลาเข้า", "fontSize": 8, "align": "LEFT"},
        {"text": "เวลาจอด", "fontSize": 8, "align": "CENTER"},
        {"text": "จำนวนเงิน", "fontSize": 8, "align": "RIGHT"},
        {"text": "ยอดเงิน", "fontSize": 18, "align": "CENTER"},
        {"text": "เลขทะเบียน", "fontSize": 8, "underline": true},
        {
          "text": "เลขบัตร",
          "fontSize": 8,
          "align": "LEFT",
          "lineSpacing": 40,
        },
        {
          "text": "เลขบัตร",
          "fontSize": 8,
          "align": "LEFT",
          "lineSpacing": 29,
          "letterSpacing": 25,
        },
        // {
        //   "text": "打印数据字符间距测试\r\n\r\n\r\n",
        //   "fontSize": 8,
        //   "align": "LEFT",
        //   "lineSpacing": 29,
        //   "letterSpacing": 25,
        // },
      ];

      await S600SDK.printText(items);
      //final result = await S600SDK.printerTest();
      // ScaffoldMessenger.of(context).showSnackBar(
      //   SnackBar(content: Text(result)),
      // );
    } on PlatformException catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Print error: ${e.message}')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Print failed: $e')),
      );
    }
  }

  Widget _buildCardData() {
    if (_cardData == null) {
      return Text('No card data read yet.');
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: _cardData!.entries.where((e) => e.key != 'Photo').map((entry) {
        return Text('${entry.key}: ${entry.value}');
      }).toList(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('S600 Flutter Tester'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            // ✅ ปุ่มอยู่ในแนวนอน
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton(
                  onPressed: _readCard,
                  child: Text('Read Thai ID Card'),
                ),
                SizedBox(width: 16), // ระยะห่างระหว่างปุ่ม
                ElevatedButton(
                  onPressed: _printCardData,
                  child: Text('Print'),
                ),
              ],
            ),
            SizedBox(height: 20),

            if (_error.isNotEmpty)
              Text(
                'Error: $_error',
                style: TextStyle(color: Colors.red),
              ),

            if (_cardData != null && _cardData!['Photo'] != null)
              Padding(
                padding: const EdgeInsets.only(bottom: 16.0),
                child: Image.memory(
                  base64Decode(_cardData!['Photo']),
                  width: 200,
                  height: 240,
                  fit: BoxFit.cover,
                ),
              ),

            Expanded(
              child: SingleChildScrollView(
                child: _buildCardData(),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
