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
