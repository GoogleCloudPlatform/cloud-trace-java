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

import java.math.BigInteger;

/**
 * An abstract factory for generating trace identifiers. This factory knows about the invalid trace
 * identifier value.
 *
 * @see IdFactory
 * @see RandomTraceIdFactory
 * @see TraceId
 */
public abstract class AbstractTraceIdFactory implements IdFactory<TraceId> {
  @Override
  public abstract TraceId nextId();

  /**
   * Returns the invalid trace identifier value.
   *
   * @return the invalid trace identifier value.
   */
  @Override
  public TraceId invalid() {
    return new TraceId(BigInteger.ZERO);
  }
}
