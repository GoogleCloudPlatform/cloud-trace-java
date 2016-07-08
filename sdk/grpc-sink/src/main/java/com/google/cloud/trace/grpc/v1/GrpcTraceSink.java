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

package com.google.cloud.trace.grpc.v1;

import com.google.auth.Credentials;
import com.google.cloud.trace.v1.sink.TraceSink;
import com.google.devtools.cloudtrace.v1.PatchTracesRequest;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.Traces;
import com.google.devtools.cloudtrace.v1.TraceServiceGrpc;

import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.Executor;

public class GrpcTraceSink implements TraceSink {
  private final ManagedChannel managedChannel;
  private final TraceServiceGrpc.TraceServiceBlockingStub traceService;

  public GrpcTraceSink(String apiHost, Credentials credentials, Executor executor) {
    this.managedChannel = ManagedChannelBuilder.forTarget(apiHost).build();
    Channel channel = ClientInterceptors.intercept(this.managedChannel,
        new ClientAuthInterceptor(credentials, executor));
    this.traceService = TraceServiceGrpc.newBlockingStub(channel);
  }

  @Override
  public void receive(Trace trace) {
    PatchTracesRequest.Builder requestBuilder = PatchTracesRequest.newBuilder()
        .setProjectId(trace.getProjectId());
    requestBuilder.getTracesBuilder().addTraces(trace);
    traceService.patchTraces(requestBuilder.build());
  }

  public void close() {
    managedChannel.shutdown();
  }
}
