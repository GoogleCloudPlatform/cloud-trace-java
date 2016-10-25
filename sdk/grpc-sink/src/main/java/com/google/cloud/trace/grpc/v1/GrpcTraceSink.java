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
import com.google.devtools.cloudtrace.v1.Traces;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.TraceServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;


/**
 * A trace sink that sends trace messages to the Stackdriver Trace API trace via gRPC.
 *
 * @see <a href="http://www.grpc.io">gRPC</a>
 * @see Credentials
 * @see Traces
 * @see TraceSink
 */
public class GrpcTraceSink implements TraceSink {
  private final ManagedChannel managedChannel;
  private final TraceServiceGrpc.TraceServiceBlockingStub traceService;

  /**
   * Creates a trace sink that sends trace messages to the Stackdriver Trace API via gRPC.
   *
   * @param apiHost     a string containing the API host name.
   * @param credentials a credentials used to authenticate API calls.
   */
  public GrpcTraceSink(String apiHost, Credentials credentials) {
    this.managedChannel = ManagedChannelBuilder.forTarget(apiHost).build();
    this.traceService = TraceServiceGrpc.newBlockingStub(managedChannel)
        .withCallCredentials(MoreCallCredentials.from(credentials));
  }

  @Override
  public void receive(Traces traces) {
    if (traces.getTracesCount() == 0) {
      return;
    }
    String projectId = traces.getTraces(0).getProjectId();
    PatchTracesRequest.Builder requestBuilder =
        PatchTracesRequest.newBuilder()
            .setProjectId(projectId)
            .setTraces(traces);

    traceService.patchTraces(requestBuilder.build());
  }

  /**
   * Closes the resources held by this trace sink.
   */
  public void close() {
    managedChannel.shutdown();
  }
}
