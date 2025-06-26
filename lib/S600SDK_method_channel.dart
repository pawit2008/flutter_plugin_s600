import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'S600SDK_platform_interface.dart';

/// An implementation of [S600SDKPlatform] that uses method channels.
class MethodChannelS600SDK extends S600SDKPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('thai_id_card_reader');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
