package com.google.cloud.trace.servlet;

import com.google.cloud.trace.util.Labels;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestLabelsHelper {
  public static String overrideName(HttpServletRequest request) {
    String name = request.getContextPath() + request.getServletPath() + request.getPathInfo();
    return name;
  }

  public static void addRequestLabels(HttpServletRequest request, Labels.Builder labelsBuilder) {
    labelsBuilder.add("trace.cloud.google.com/http/method", request.getMethod());
    labelsBuilder.add("trace.cloud.google.com/http/url", request.getRequestURL().toString());
    if (request.getContentLength() != -1) {
      labelsBuilder.add(
          "trace.cloud.google.com/http/request/size", Integer.toString(request.getContentLength()));
    }
    labelsBuilder.add("trace.cloud.google.com/http/host", request.getServerName());
    if (request.getHeader("user-agent") != null) {
      labelsBuilder.add("trace.cloud.google.com/http/user_agent", request.getHeader("user-agent"));
    }
  }

  public static void addResponseLabels(HttpServletResponse response, Labels.Builder labelsBuilder) {
    // Add "trace.cloud.google.com/http/status_code" to Integer.toString(response.getStatus()), if
    // GAE supports 3.0.
    if (response.getBufferSize() > 0) {
      labelsBuilder.add(
          "trace.cloud.google.com/http/response/size", Integer.toString(response.getBufferSize()));
    }
  }
}
