import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'S600SDK_method_channel.dart';

abstract class S600SDKPlatform extends PlatformInterface {
  /// Constructs a ThaiIdCardReaderPlatform.
  S600SDKPlatform() : super(token: _token);

  static final Object _token = Object();

  static S600SDKPlatform _instance = MethodChannelS600SDK();

  /// The default instance of [S600SDKPlatform] to use.
  ///
  /// Defaults to [MethodChannelS600SDK].
  static S600SDKPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [S600SDKPlatform] when
  /// they register themselves.
  static set instance(S600SDKPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
