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

public class StartSpanOptions {
  private Timestamp timestamp;
  private SpanKind spanKind;
  private TraceOptions traceOptions;

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public StartSpanOptions setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public SpanKind getSpanKind() {
    return spanKind;
  }

  public StartSpanOptions setSpanKind(SpanKind spanKind) {
    this.spanKind = spanKind;
    return this;
  }

  public TraceOptions getTraceOptions() {
    return traceOptions;
  }

  public StartSpanOptions setTraceOptions(TraceOptions traceOptions) {
    this.traceOptions = traceOptions;
    return this;
  }
}
