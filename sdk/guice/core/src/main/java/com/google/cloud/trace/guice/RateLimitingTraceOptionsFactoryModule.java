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

package com.google.cloud.trace.guice;

import com.google.cloud.trace.guice.StackTraceEnabled;
import com.google.cloud.trace.util.RateLimitingTraceOptionsFactory;
import com.google.cloud.trace.util.TraceOptionsFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Provides a {@link TraceOptionsFactory} which generates new trace options whose trace option
 * values are set based on a rate limiter which limits the number of enabled traces per second.
 *
 * <p>This class is unconditionally thread-safe.
 *
 * @see RateLimitingTraceOptionsFactory
 */
@ThreadSafe
public final class RateLimitingTraceOptionsFactoryModule extends AbstractModule {

  @Override
  protected void configure() {
    requireBinding(Key.get(double.class, SamplingRate.class));
    requireBinding(Key.get(boolean.class, StackTraceEnabled.class));
  }

  @Provides
  @Singleton
  TraceOptionsFactory provideTraceOptionsFactory(
      @SamplingRate double samplingInterval, @StackTraceEnabled boolean stackTraceEnabled) {
    return new RateLimitingTraceOptionsFactory(samplingInterval, stackTraceEnabled);
  }
}
