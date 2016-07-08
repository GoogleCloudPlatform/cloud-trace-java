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

import java.security.SecureRandom;

public class RandomSpanIdFactory extends AbstractSpanIdFactory {
  private static final int SPAN_ID_BIT_LENGTH = 64;

  private final SecureRandom random;

  public RandomSpanIdFactory() {
    this.random = new SecureRandom();
  }

  public RandomSpanIdFactory(byte[] seed) {
    this.random = new SecureRandom(seed);
  }

  public RandomSpanIdFactory(SecureRandom random) {
    this.random = random;
  }

  @Override
  public SpanId nextId() {
    return new SpanId(random.nextLong());
  }
}
