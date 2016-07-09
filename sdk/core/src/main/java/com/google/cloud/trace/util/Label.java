// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * A class that represents a span label. A span label is a key-value pair of strings.
 *
 * @see Labels
 */
public class Label {
  private final String key;
  private final String value;

  /**
   * Creates a span label.
   *
   * @param key   a string that is the label key.
   * @param value a string that is the label value.
   */
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

  /**
   * Returns the label key.
   *
   * @return the key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the label value.
   *
   * @return the value.
   */
  public String getValue() {
    return value;
  }
}
