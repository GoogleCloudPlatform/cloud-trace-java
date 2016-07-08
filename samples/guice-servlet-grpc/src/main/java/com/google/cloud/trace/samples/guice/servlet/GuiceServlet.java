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

package com.google.cloud.trace.samples.guice.servlet;

import com.google.cloud.trace.annotation.Name;
import com.google.cloud.trace.annotation.Option;
import com.google.cloud.trace.annotation.Span;
import com.google.cloud.trace.guice.servlet.RequestLabeler;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GuiceServlet extends HttpServlet {
  private static class MyExceptionMethodException extends Exception {
    private MyExceptionMethodException(String message) {
      super(message);
    }
  }

  @Span(labels={RequestLabeler.KEY},entry=true,trace=Option.TRUE)
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println("Hello, AppEngine-Java!");

    myMethod();
    stackTraceTrue();

    try {
      exceptionMethod();
    } catch(MyExceptionMethodException ex) {
      // Swallow the exception.
    }
  }

  @Span(callLabels=Option.TRUE)
  public void myMethod() {
    try {
      Thread.sleep(1);
    } catch (InterruptedException ex) {
    }
  }

  @Span(stackTrace=Option.TRUE)
  public void stackTraceTrue() {
    try {
      Thread.sleep(1);
    } catch (InterruptedException ex) {
    }
  }

  @Span(callLabels=Option.TRUE)
  public void exceptionMethod() throws MyExceptionMethodException {
    try {
      Thread.sleep(1);
    } catch (InterruptedException ex) {
    }
    throw new MyExceptionMethodException("A useless exception message.");
  }
}
