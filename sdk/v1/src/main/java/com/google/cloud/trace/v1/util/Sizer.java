package com.google.cloud.trace.v1.util;

public interface Sizer<T> {
  int size(T sizeable);
}
