# DLS S600 Flutter Plugin

A Flutter plugin for Dolly Solutions S600 device functionality including:

- Barcode printing
- QR code generation
- Smart card operations
- Device service binding

## Installation

Add this to your package's pubspec.yaml file:

```yaml
dependencies:
  dls_s600: ^1.0.0
```

## Usage

Import the package:

```dart
import 'package:s600/S600SDK.dart';
```

### Example

```dart
// Read Thai ID card
final cardData = await S600SDK.readThaiIDCard();

// Print barcode
await S600SDK.printBarCode("1234567890");

// Generate and print QR code
await S600SDK.printQrCode("https://example.com");

// Print text with formatting
await S600SDK.printText([
  {"text": "Header", "fontSize": 24},
  {"text": "Item 1", "align": "LEFT"},
  {"text": "Item 2", "align": "RIGHT"}
]);

// Print image from assets
final ByteData data = await rootBundle.load('assets/image.png');
final Uint8List bytes = data.buffer.asUint8List();
await S600SDK.printImage(bytes);
```

## Platform Setup

### Android

Add these permissions to your AndroidManifest.xml if not already present:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

## License

MIT
