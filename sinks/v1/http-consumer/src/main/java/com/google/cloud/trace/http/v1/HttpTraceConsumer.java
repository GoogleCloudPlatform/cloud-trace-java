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

package com.google.cloud.trace.http.v1;

import com.google.auth.oauth2.OAuth2Credentials;
import com.google.cloud.trace.v1.consumer.TraceConsumer;
import com.google.common.io.ByteStreams;
import com.google.devtools.cloudtrace.v1.Traces;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class HttpTraceConsumer implements TraceConsumer {
  private final static Logger logger = Logger.getLogger(HttpTraceConsumer.class.getName());

  private final OAuth2Credentials oAuth2Credentials;
  private final String hostUrl;

  public HttpTraceConsumer(OAuth2Credentials oAuth2Credentials, String hostUrl) {
    this.oAuth2Credentials = oAuth2Credentials;
    this.hostUrl = hostUrl;
  }

  public HttpTraceConsumer(OAuth2Credentials oAuth2Credentials) {
    this(oAuth2Credentials, "https://cloudtrace.googleapis.com");
  }

  @Override
  public void receive(Traces traces) {
    if (traces.getTracesCount() == 0) {
      return;
    }
    String projectId = traces.getTraces(0).getProjectId();
    processTraces(traces, projectId);
  }

  private void processTraces(Traces traces, String projectId) {
    URL url;
    String urlString = String.format("%s/v1/projects/%s/traces", hostUrl, projectId);
    try {
      url = new URL(urlString);
    } catch (MalformedURLException ex) {
      logger.severe("Malformed URL: " + urlString);
      return;
    }

    String jsonRequest;
    try {
      jsonRequest = JsonFormat.printer().print(traces);
    } catch (InvalidProtocolBufferException ex) {
      logger.severe("Failed to format: " + traces.toString());
      return;
    }

    try {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty(
          "Authorization", "Bearer " + oAuth2Credentials.getAccessToken().getTokenValue());
      connection.setDoOutput(true);
      OutputStream out = connection.getOutputStream();
      out.write(jsonRequest.getBytes());
      out.close();
      // Use the connection to make the request. This also sets the connection's response code.
      InputStream responseStream = connection.getInputStream();
      int responseCode = connection.getResponseCode();
      if (responseCode != 200) {
        byte[] responseBytes = ByteStreams.toByteArray(responseStream);
        logger.severe(String.format("Unsuccessful patchTraces (%s):\nResponse: %s",
            responseCode, new String(responseBytes)));
      }
    } catch (IOException ex) {
      logger.severe("Failed to send: " + traces.toString());
    }
  }
}
