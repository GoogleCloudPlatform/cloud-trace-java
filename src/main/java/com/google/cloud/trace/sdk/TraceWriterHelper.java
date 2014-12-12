package com.google.cloud.trace.sdk;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for dealing with {@link TraceWriter} instances.
 */
public class TraceWriterHelper {

  private static final Logger logger = Logger.getLogger(TraceWriterHelper.class.getName());

  /**
   * Reflectively instantiates and initializes a TraceWriter using a Properties file.
   */
  public static TraceWriter createFromProperties(String writerClassName, Properties props) {
    TraceWriter writer = null;
    try {
      writer = (TraceWriter) Class.forName(writerClassName).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      logger.log(Level.WARNING, "Error creating TraceWriter", e);
    }
    if (writer != null && writer instanceof CanInitFromProperties) {
      ((CanInitFromProperties) writer).initFromProperties(props);
    }
    return writer;
  }
}
