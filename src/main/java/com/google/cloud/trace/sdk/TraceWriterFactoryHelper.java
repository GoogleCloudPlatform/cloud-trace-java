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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for dealing with {@link TraceWriterFactory}s.
 */
public class TraceWriterFactoryHelper {

  private static final Logger logger = Logger.getLogger(TraceWriterFactoryHelper.class.getName());

  public static TraceWriterFactory createTraceWriterFactory(String factoryClassName) {
    TraceWriterFactory factory = null;
    try {
      factory = (TraceWriterFactory) Class.forName(factoryClassName).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      logger.log(Level.WARNING, "Error creating TraceWriter", e);
    }
    return factory;
  }
}
