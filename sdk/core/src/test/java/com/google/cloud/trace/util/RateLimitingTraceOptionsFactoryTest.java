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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.trace.util.TraceOptions;
import com.google.cloud.trace.util.TraceOptionsFactory;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

/** Unit tests for {@link RateLimitingTraceOptionsFactory}. */
@RunWith(JUnit4.class)
public class RateLimitingTraceOptionsFactoryTest {

  private RateLimiter ratelimiter = mock(RateLimiter.class);

  @Test
  public void create_forNewContext_permitAvailable() {
    when(ratelimiter.tryAcquire()).thenReturn(true);

    TraceOptionsFactory traceOptionsFactory =
        new RateLimitingTraceOptionsFactory(ratelimiter, true);

    TraceOptions traceOptions = traceOptionsFactory.create();
    assertThat(traceOptions.getTraceEnabled()).isTrue();
    assertThat(traceOptions.getStackTraceEnabled()).isTrue();
  }

  @Test
  public void create_forNewContext_noPermitAvailable() {
    when(ratelimiter.tryAcquire()).thenReturn(false);

    TraceOptionsFactory traceOptionsFactory =
        new RateLimitingTraceOptionsFactory(ratelimiter, true);

    TraceOptions traceOptions = traceOptionsFactory.create();
    assertThat(traceOptions.getTraceEnabled()).isFalse();
    assertThat(traceOptions.getStackTraceEnabled()).isTrue();
  }

  @Test
  public void create_forChildContext() {
    TraceOptionsFactory traceOptionsFactory =
        new RateLimitingTraceOptionsFactory(ratelimiter, true);

    TraceOptions traceOptions = new TraceOptions();
    assertThat(traceOptionsFactory.create(traceOptions)).isEqualTo(traceOptions);
  }
}
