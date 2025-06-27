import 'package:flutter/material.dart';
import 'package:dls_s600/S600SDK.dart';
import 'package:flutter/services.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'DLS S600 Plugin Demo',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const PluginDemoScreen(),
    );
  }
}

class PluginDemoScreen extends StatefulWidget {
  const PluginDemoScreen({super.key});

  @override
  State<PluginDemoScreen> createState() => _PluginDemoScreenState();
}

class _PluginDemoScreenState extends State<PluginDemoScreen> {
  String _status = 'Ready';
  Map<String, dynamic>? _cardData;

  Future<void> _testPrint() async {
    setState(() => _status = 'Printing...');
    try {
      await S600SDK.printText([
        {"text": "DLS S600 Demo", "fontSize": 24, "align": "CENTER"},
        {"text": "Test Print", "fontSize": 16},
        {"text": "Barcode:", "fontSize": 12},
      ]);
      await S600SDK.printBarCode("123456789");
      await S600SDK.printQrCode("https://dollysolutions.com");
      setState(() => _status = 'Print successful!');
    } on PlatformException catch (e) {
      setState(() => _status = 'Error: ${e.message}');
    }
  }

  Future<void> _readCard() async {
    setState(() => _status = 'Reading card...');
    try {
      final data = await S600SDK.readThaiIDCard();
      setState(() {
        _cardData = data;
        _status = 'Card read successfully!';
      });
    } on PlatformException catch (e) {
      setState(() => _status = 'Error: ${e.message}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('DLS S600 Plugin Demo')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Text(_status, style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 20),
            if (_cardData != null) ...[
              Text('Card Data:', style: Theme.of(context).textTheme.headlineSmall),
              Expanded(
                child: ListView(
                  children: _cardData!.entries.map((e) => 
                    ListTile(
                      title: Text(e.key),
                      subtitle: Text(e.value.toString()),
                    )
                  ).toList(),
                ),
              ),
            ],
            const Spacer(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  onPressed: _testPrint,
                  child: const Text('Test Print'),
                ),
                ElevatedButton(
                  onPressed: _readCard,
                  child: const Text('Read Card'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
