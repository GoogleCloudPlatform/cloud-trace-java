package com.google.cloud.trace.util;

public interface Timestamp {
  long getSeconds();
  int getNanos();
}
