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

package com.google.cloud.trace.sdk.gae;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.cloud.trace.sdk.CanInitFromProperties;
import com.google.cloud.trace.sdk.CloudTraceException;
import com.google.cloud.trace.sdk.CloudTraceReader;
import com.google.cloud.trace.sdk.CloudTraceRequestFactory;
import com.google.cloud.trace.sdk.CloudTraceResponse;
import com.google.cloud.trace.sdk.CloudTraceWriter;
import com.google.cloud.trace.sdk.CredentialProvider;

/**
 * Implementation of {@link CloudTraceRequestFactory} that understands App Engine. Unfortunately, we
 * can't just use the Apache http transport in GAE because it does disallowed things, and we also
 * can't use java.net.URL because it doesn't allow HTTP PATCH.
 */
public class UrlFetchCloudTraceRequestFactory implements CloudTraceRequestFactory {

  private static final Logger logger = Logger.getLogger(UrlFetchCloudTraceRequestFactory.class.getName());

  /**
   * Reflectively instantiates and initializes a class using a Properties file.
   */
  private static Object createFromProperties(String className, Properties props) {
    Object obj = null;
    try {
      obj = Class.forName(className).newInstance();
      if (obj != null && obj instanceof CanInitFromProperties) {
        ((CanInitFromProperties) obj).initFromProperties(props);
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException
        | CloudTraceException e) {
    }
    return obj;
  }

  private CredentialProvider credentialProvider;

  @Override
  public void initFromProperties(Properties props) throws CloudTraceException {
    String credentialProviderClassName =
        props.getProperty(getClass().getName() + ".credentialProvider");
    if (credentialProviderClassName != null && !credentialProviderClassName.isEmpty()) {
      this.credentialProvider =
          (CredentialProvider) createFromProperties(credentialProviderClassName, props);
    }
  }

  @Override
  public CloudTraceResponse executeGet(GenericUrl url) throws CloudTraceException {
    Credential credential = credentialProvider.getCredential(CloudTraceReader.SCOPES);
    String urlStr = url.toURL().toString();
    logger.info("Getting trace from: " + urlStr);
    HTTPRequest request = new HTTPRequest(url.toURL());
    request.addHeader(new HTTPHeader("Authorization", "Bearer " + credential.getAccessToken()));
    try {
      HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch(request);
      String content = "";
      if (response.getResponseCode() == 200) {
        content = new String(response.getContent(), "UTF-8");
      }
      return new CloudTraceResponse(content, response.getResponseCode());
    } catch (IOException e) {
      throw new CloudTraceException("Exception doing GET", e);
    }
  }

  @Override
  public CloudTraceResponse executePatch(GenericUrl url, String requestBody)
      throws CloudTraceException {
    Credential credential = credentialProvider.getCredential(CloudTraceWriter.SCOPES);
    String urlStr = url.toURL().toString();
    logger.info("Writing trace to: " + urlStr);
    HTTPRequest request = new HTTPRequest(url.toURL(), HTTPMethod.PATCH);
    request.addHeader(new HTTPHeader("Content-Type", "application/json"));
    request.addHeader(new HTTPHeader("Authorization", "Bearer " + credential.getAccessToken()));
    request.setPayload(requestBody.getBytes());
    HTTPResponse response = null;
    try {
      response = URLFetchServiceFactory.getURLFetchService().fetch(request);
    } catch (IOException e) {
      throw new CloudTraceException("Exception doing PATCH", e);
    }
    if (response == null || response.getResponseCode() != 200) {
      throw new CloudTraceException("Failed to write span, status = " + response.getResponseCode());
    }
    return new CloudTraceResponse("", response.getResponseCode());
  }

  public CredentialProvider getCredentialProvider() {
    return this.credentialProvider;
  }

  public void setCredentialProvider(CredentialProvider credentialProvider) {
    this.credentialProvider = credentialProvider;
  }
}
