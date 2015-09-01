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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

/**
 * Tests for the {@link ForwardTraceEnablingPolicy} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class ForwardTraceEnablingPolicyTest {

  private ForwardTraceEnablingPolicy policy;
  
  @Before
  public void setUp() {
    policy = new ForwardTraceEnablingPolicy();
  }
  
  @Test
  public void testInitFromPropertiesValid() {
    Properties props = new Properties();
    props.setProperty(ForwardTraceEnablingPolicy.class.getName() + ".enablingPolicy",
        AlwaysTraceEnablingPolicy.class.getName());
    policy.initFromProperties(props);
    assertTrue(policy.getEnablingPolicy() instanceof AlwaysTraceEnablingPolicy);
  }
  
  @Test
  public void testIsTracingEnabledShouldForward() {
    assertTrue(policy.isTracingEnabled(true));
  }
  
  @Test
  public void testIsTracingEnabledUsesInnerPolicy() {
    assertFalse(policy.isTracingEnabled(false));
    policy.setEnablingPolicy(new AlwaysTraceEnablingPolicy());
    assertTrue(policy.isTracingEnabled(false));
  }
}
