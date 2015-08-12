// Copyright 2015 Google Inc. All rights reserved.
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

package com.google.cloud.trace.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.math.BigInteger;

/**
 * Tests for the {@link TraceContext} class.
 */
public class TraceContextTest {

  @Test
  public void testShouldWrite() {
    TraceContext context = new TraceContext("1", BigInteger.valueOf(2), TraceContext.TRACE_ENABLED);
    assertTrue(context.getShouldWrite());
    assertEquals(TraceContext.TRACE_ENABLED, context.getOptions());
    
    context.setShouldWrite(false);
    assertFalse(context.getShouldWrite());
    assertEquals(0, context.getOptions());

    context = new TraceContext("1", BigInteger.valueOf(2), 0);
    assertFalse(context.getShouldWrite());
  }
}
