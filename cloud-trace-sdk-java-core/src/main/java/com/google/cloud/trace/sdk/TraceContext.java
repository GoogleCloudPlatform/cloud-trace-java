// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Identifies the trace and span and whether it is currently slated to be written out.
 * Potentially forwarded over the wire for use in propagation to child spans.
 */
public class TraceContext {
  /**
   * The custom header name.
   */
  public static final String TRACE_HEADER = "X-Cloud-Trace-Context";

  public static final long TRACE_OPTIONS_NONE = 0;
  public static final long TRACE_ENABLED = 1;

  private final String traceId;
  private final BigInteger spanId;
  private Map<String, String> data;

  /**
   * The trace span options, which is a bitmasked long representing the state
   * of various tracing features (such as whether or not incoming/outgoing traces
   * are enabled).
   */
  private long options;

  /**
   * Creates a new trace context from a trace HTTP header string.
   * The raw trace header value takes a format like:
   *   traceid/spanid;o=options;key1=value1;key2=value2;
   * Where traceid is a string, spanid is a number, options is a number,
   * and options and the following key-value pairs are all optional.
   * @return the new TraceContext, or null if we could not adequately parse it.
   */
  public static TraceContext fromTraceHeader(String headerStr) {
    if (headerStr == null) {
      return null;
    }

    String traceId = null;
    BigInteger spanId = null;
    long options = 0;
    Map<String, String> data = new HashMap<>();

    String[] slashParts = headerStr.split("/");
    if (slashParts.length > 0) {
      traceId = slashParts[0];
      if (slashParts.length > 1) {
        String[] semiParts = slashParts[1].split(";");
        if (semiParts.length > 0) {
          try {
            spanId = new BigInteger(semiParts[0]);
          } catch (NumberFormatException nfe) {
          }
        }

        if (semiParts.length > 1) {
          for (int i = 1; i < semiParts.length; i++) {
            String[] kvParts = semiParts[i].split("=");
            if (kvParts.length != 2) {
              continue;
            }

            if (i == 1 && "o".equals(kvParts[0])) {
              // If there is an o= (options) string, it is supposed to come first.
              try {
                options = Long.parseLong(kvParts[1]);
              } catch (NumberFormatException nfe) {
              }
            } else if (!"o".equals(kvParts[0])){
              // Just stick it in a map.
              data.put(kvParts[0], kvParts[1]);
            }
          }
        }
      }
    }

    return new TraceContext(traceId, spanId, options, data);
  }

  /**
   * Creates a new trace context with the given identifiers and options and
   * an empty data map.
   */
  public TraceContext(String traceId, BigInteger spanId, long options) {
    this(traceId, spanId, options, new HashMap<String, String>());
  }

  /**
   * Creates a new trace context with the given identifiers and options and
   * the given data map.
   */
  public TraceContext(String traceId, BigInteger spanId, long options, Map<String, String> data) {
    this.traceId = traceId;
    this.spanId = spanId;
    this.options = options;
    this.data = data;
  }

  public String getTraceId() {
    return traceId;
  }

  public BigInteger getSpanId() {
    return spanId;
  }

  public boolean getShouldWrite() {
    return (options & TRACE_ENABLED) == TRACE_ENABLED;
  }

  public void setShouldWrite(boolean shouldWrite) {
    if (shouldWrite) {
      this.options |= TRACE_ENABLED;
    } else {
      this.options &= ~TRACE_ENABLED;
    }
  }

  public long getOptions() {
    return options;
  }

  public void setOptions(long options) {
    this.options = options;
  }

  public Map<String, String> getData() {
    return data;
  }

  @Override
  public String toString() {
    return traceId + '|' + spanId + '|' + options;
  }

  /**
   * Serializes this context into a string in the expected trace header format.
   */
  public String toTraceHeader() {
    StringBuilder sb = new StringBuilder();
    if (traceId != null) {
      sb.append(traceId);
    }
    sb.append('/');
    if (spanId != null) {
      sb.append(spanId);
    }
    if (options > 0) {
      sb.append(";o=");
      sb.append(options);
    }
    if (!data.isEmpty()) {
      for (Map.Entry<String, String> kvp : data.entrySet()) {
        sb.append(';');
        sb.append(kvp.getKey());
        sb.append('=');
        sb.append(kvp.getValue());
      }
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((data == null) ? 0 : data.hashCode());
    result = prime * result + (int) (options ^ (options >>> 32));
    result = prime * result + ((spanId == null) ? 0 : spanId.hashCode());
    result = prime * result + ((traceId == null) ? 0 : traceId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TraceContext other = (TraceContext) obj;
    if (data == null) {
      if (other.data != null)
        return false;
    } else if (!data.equals(other.data))
      return false;
    if (options != other.options)
      return false;
    if (spanId == null) {
      if (other.spanId != null)
        return false;
    } else if (!spanId.equals(other.spanId))
      return false;
    if (traceId == null) {
      if (other.traceId != null)
        return false;
    } else if (!traceId.equals(other.traceId))
      return false;
    return true;
  }
}
