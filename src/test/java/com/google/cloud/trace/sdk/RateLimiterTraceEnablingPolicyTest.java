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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.util.concurrent.RateLimiter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

/**
 * Tests for the {@link RateLimiterTraceEnablingPolicy} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class RateLimiterTraceEnablingPolicyTest {

  private RateLimiterTraceEnablingPolicy policy;
  @Mock private RateLimiter mockLimiter;
  
  @Before
  public void setUp() {
    policy = new RateLimiterTraceEnablingPolicy();
    policy.setLimiter(mockLimiter);
  }
  
  @Test
  public void testInitFromProperties() {
    Mockito.when(mockLimiter.tryAcquire()).thenReturn(true);
    Properties props = new Properties();
    props.setProperty(RateLimiterTraceEnablingPolicy.class.getName() + ".ratePerSecond", "19");
    policy.initFromProperties(props);
    assertEquals(19, policy.getLimiter().getRate(), 0.001);
  }
  
  @Test
  public void testIsTracingEnabledAlready() {
    Mockito.when(mockLimiter.tryAcquire()).thenReturn(false);
    assertTrue(policy.isTracingEnabled(true));
  }
  
  @Test
  public void testIsTracingEnabledByLimiter() {
    Mockito.when(mockLimiter.tryAcquire()).thenReturn(true);
    assertTrue(policy.isTracingEnabled(false));
  }
}
