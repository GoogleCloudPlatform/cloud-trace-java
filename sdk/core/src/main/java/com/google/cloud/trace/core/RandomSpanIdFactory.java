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

import java.security.SecureRandom;

/**
 * An factory for generating span identifiers. This factory uses a {@link SecureRandom} to produce
 * span identifiers.
 *
 * @see IdFactory
 * @see SecureRandom
 * @see SpanId
 */
public class RandomSpanIdFactory implements IdFactory<SpanId> {
  private final SecureRandom random;

  /**
   * Creates a new span identifier factory. This constructor generates a {@code SecureRandom} using
   * a default seed.
   */
  public RandomSpanIdFactory() {
    this.random = new SecureRandom();
  }

  /**
   * Creates a new span identifier factory. This constructor generates a {@code SecureRandom} using
   * the given seed.
   *
   * @param seed a byte array used as the seed.
   */
  public RandomSpanIdFactory(byte[] seed) {
    this.random = new SecureRandom(seed);
  }

  /**
   * Creates a new span identifier factory.
   *
   * @param random a secure random used to generate span identifiers.
   */
  public RandomSpanIdFactory(SecureRandom random) {
    this.random = random;
  }

  /**
   * Generates a new span identifier.
   *
   * @return the new span identifier.
   */
  @Override
  public SpanId nextId() {
    return new SpanId(random.nextLong());
  }
}
