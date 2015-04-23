// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk;

/**
 * Additional data item that can be attached to a {@link TraceSpanData} instance.
 */
public class TraceSpanLabel {
  private final String key;
  private final String value;

  /**
   * Creates a label with the given key and value.
   */
  public TraceSpanLabel(String key, String value) {
    if (key == null) {
      throw new IllegalArgumentException("Null label keys not allowed");
    }
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return key + '=' + value;
  }
}
