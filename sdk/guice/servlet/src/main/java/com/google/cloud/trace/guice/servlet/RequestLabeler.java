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

package com.google.cloud.trace.guice.servlet;

import com.google.cloud.trace.guice.annotation.Labeler;
import com.google.cloud.trace.servlet.RequestLabelsHelper;
import com.google.cloud.trace.util.Labels;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestLabeler implements Labeler {
  public final static String KEY = "http";

  private final Provider<HttpServletRequest> requestProvider;
  private final Provider<HttpServletResponse> responseProvider;

  @Inject
  RequestLabeler(Provider<HttpServletRequest> requestProvider,
      Provider<HttpServletResponse> responseProvider) {
    this.requestProvider = requestProvider;
    this.responseProvider = responseProvider;
  }

  @Override
  public String overrideName() {
    return RequestLabelsHelper.overrideName(requestProvider.get());
  }

  @Override
  public void addLabelsBeforeCall(Labels.Builder labelsBuilder) {
    RequestLabelsHelper.addRequestLabels(requestProvider.get(), labelsBuilder);
  }

  @Override
  public void addLabelsAfterCall(Labels.Builder labelsBuilder) {
    RequestLabelsHelper.addResponseLabels(responseProvider.get(), labelsBuilder);
  }
}
