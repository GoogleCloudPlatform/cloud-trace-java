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

package com.google.cloud.trace.core;

import com.google.common.base.MoreObjects;
import java.time.Instant;

/**
 * A timestamp represented by a Java {@link Instant}.
 *
 * @see Instant
 * @see JavaTimestampFactory
 * @see Timestamp
 */
public class JavaTimestamp implements Timestamp {
  private final Instant instant;

  /**
   * Creates a Java timestamp.
   *
   * @param instant the instant to be represented by this timestamp.
   */
  public JavaTimestamp(Instant instant) {
    this.instant = instant;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instant", instant)
        .toString();
  }

  @Override
  public long getSeconds() {
    return instant.getEpochSecond();
  }

  @Override
  public int getNanos() {
    return instant.getNano();
  }
}
