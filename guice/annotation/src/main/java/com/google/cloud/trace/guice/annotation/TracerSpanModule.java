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

package com.google.cloud.trace.guice.annotation;

import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.annotation.Span;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.MapBinder;

import java.util.Map;

public class TracerSpanModule extends AbstractModule {
  private final String labelHost;

  public TracerSpanModule() {
    this("");
  }

  public TracerSpanModule(String labelHost) {
    this.labelHost = labelHost;
  }

  @Override
  protected void configure() {
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(Span.class),
        new TracerSpanInterceptor(getProvider(Tracer.class),
            getProvider(Key.get(new TypeLiteral<Map<String, Labeler>>(){})), labelHost));
    MapBinder<String, Labeler> mapBinder =
        MapBinder.newMapBinder(binder(), String.class, Labeler.class);
  }
}
