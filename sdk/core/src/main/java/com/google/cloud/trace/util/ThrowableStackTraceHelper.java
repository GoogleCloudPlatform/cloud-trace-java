package com.google.cloud.trace.util;

public class ThrowableStackTraceHelper {
  public static StackTrace.Builder createBuilder(Throwable throwable) {
    return addFrames(StackTrace.builder(), throwable);
  }

  public static StackTrace.Builder addFrames(StackTrace.Builder builder, Throwable throwable) {
    for (StackTraceElement element : throwable.getStackTrace()) {
      Integer lineNumber;
      if (element.getLineNumber() < 0) {
        lineNumber = null;
      } else {
        lineNumber = element.getLineNumber();
      }
      builder.add(
          element.getClassName(), element.getMethodName(), element.getFileName(), lineNumber, null);
    }
    return builder;
  }
}
