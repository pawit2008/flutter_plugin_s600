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
import 'package:dls_s600/dls_s600.dart';
```

### Example

```dart
// Initialize printer
final printer = DlsS600.printer;

// Print barcode
await printer.printBarCode("1234567890");

// Generate QR code
final qrImage = await DlsS600.generateQRCode("https://example.com");
```

## Platform Setup

### Android

Add these permissions to your AndroidManifest.xml if not already present:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

## License

MIT
