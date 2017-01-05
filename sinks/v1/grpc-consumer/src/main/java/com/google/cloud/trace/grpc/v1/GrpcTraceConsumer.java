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
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.devtools.cloudtrace.v1.PatchTracesRequest;
import com.google.devtools.cloudtrace.v1.TraceServiceGrpc;
import com.google.devtools.cloudtrace.v1.Traces;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;


/**
 * A trace consumer that sends trace messages to the Stackdriver Trace API trace via gRPC.
 *
 * @see <a href="http://www.grpc.io">gRPC</a>
 * @see Credentials
 * @see Traces
 * @see TraceConsumer
 */
public class GrpcTraceConsumer implements TraceConsumer {
  private final TraceServiceGrpc.TraceServiceBlockingStub traceService;

  /**
   * Creates a trace consumer that sends trace messages to the Stackdriver Trace API via gRPC.
   *
   * @param traceService the trace service to use for sending API calls.
   */
  public GrpcTraceConsumer(TraceServiceGrpc.TraceServiceBlockingStub traceService) {
    this.traceService = traceService;
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
   * Creates a trace consumer that sends trace messages to the Stackdriver Trace API via gRPC.
   *
   * @param apiHost     a string containing the API host name.
   * @param credentials a credentials used to authenticate API calls.
   */
  public static GrpcTraceConsumer create(String apiHost, Credentials credentials) {
    Channel channel = ManagedChannelBuilder.forTarget(apiHost).build();
    return new GrpcTraceConsumer(TraceServiceGrpc.newBlockingStub(channel)
        .withCallCredentials(MoreCallCredentials.from(credentials)));
  }
}
