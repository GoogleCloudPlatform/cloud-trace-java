package com.google.cloud.trace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility to get the current SDK version.
 */
class SdkVersion {
  private static final String unknownVersion = "unknown_version";
  private static String version = null;

  /**
   * Gets the current SDK version. Returns unknown_version if the version cannot be determined.
   */
  static String get() {
    if (version == null) {
      version = readFromFile();
      return version;
    } else {
      return version;
    }
  }

  private static String readFromFile() {
    final Properties properties = new Properties();
    try {
      InputStream input = SdkVersion.class
          .getResourceAsStream("/cloud-trace-java.properties");
      if (input == null) {
        return unknownVersion;
      }
      properties.load(input);
      return properties.getProperty("cloud.trace.sdk.version", unknownVersion);
    } catch (IOException e) {
      return unknownVersion;
    }
  }
}
