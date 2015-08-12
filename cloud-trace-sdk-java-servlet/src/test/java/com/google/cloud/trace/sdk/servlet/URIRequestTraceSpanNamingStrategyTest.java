// Copyright 2015 Google Inc. All rights reserved.
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

package com.google.cloud.trace.sdk.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

/**
 * Tests for the {@link URIRequestTraceSpanNamingStrategy} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class URIRequestTraceSpanNamingStrategyTest {

  private static final String URI = "myuri";

  @Mock
  private HttpServletRequest request;

  @Test
  public void testGetName() {
    Mockito.when(request.getRequestURI()).thenReturn(URI);
    assertEquals(URI, new URIRequestTraceSpanNamingStrategy().getName(request));
  }
}

