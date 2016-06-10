package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class Label {
  private final String key;
  private final String value;

  public Label(String key, String value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Label)) {
      return false;
    }

    Label that = (Label)obj;
    return Objects.equals(key, that.key)
        && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("key", key)
        .add("value", value)
        .toString();
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }
}
