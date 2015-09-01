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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Tests for the {@link PercentageTraceEnablingPolicy} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class PercentageTraceEnablingPolicyTest {

  private PercentageTraceEnablingPolicy policy;
  @Mock private Random mockRandom;
  @Mock private Handler mockHandler;
  @Captor private ArgumentCaptor<LogRecord> logRecordCaptor;
  
  @Before
  public void setUp() {
    policy = new PercentageTraceEnablingPolicy();
    policy.setRandom(mockRandom);
    Logger.getLogger(PercentageTraceEnablingPolicy.class.getName()).addHandler(mockHandler);
  }
  
  @Test
  public void testInitFromPropertiesValid() {
    Properties props = new Properties();
    props.setProperty(PercentageTraceEnablingPolicy.class.getName() + ".percentage", "0.5");
    policy.initFromProperties(props);
    assertEquals(0.5, policy.getPercent(), 0.5);
  }
  
  @Test
  public void testInitFromPropertiesInvalid() {
    Properties props = new Properties();
    props.setProperty(PercentageTraceEnablingPolicy.class.getName() + ".percentage", "50");
    policy.initFromProperties(props);
    Mockito.verify(mockHandler).publish(logRecordCaptor.capture());
    assertEquals("Invalid percentage: 50.0", logRecordCaptor.getValue().getMessage());
  }
  
  @Test
  public void isTracingEnabledYes() {
    Mockito.when(mockRandom.nextDouble()).thenReturn(0.5);
    policy.setPercent(0.75);
    assertTrue(policy.isTracingEnabled(false));
  }
  
  @Test
  public void isTracingEnabledNo() {
    Mockito.when(mockRandom.nextDouble()).thenReturn(0.5);
    policy.setPercent(0.25);
    assertFalse(policy.isTracingEnabled(false));
  }
  
  @Test
  public void testSetPercent() {
    policy.setPercent(0.25);
    assertEquals(0.25, policy.getPercent(), 0.0001);
    try {
      policy.setPercent(-0.25);
    } catch (IllegalArgumentException iae) {
      return;
    }
    fail("Didn't catch expected exception");
  }
}
