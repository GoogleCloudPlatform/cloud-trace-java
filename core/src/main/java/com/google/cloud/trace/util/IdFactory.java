package com.google.cloud.trace.util;

public interface IdFactory<T> {
  T nextId();
  T invalid();
}
