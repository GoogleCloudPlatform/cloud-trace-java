package com.google.cloud.trace.sdk;

import java.util.Properties;

/**
 * Implemented by classes that can initialize from standard Java Properties files.
 */
public interface CanInitFromProperties {
  void initFromProperties(Properties props);
}
