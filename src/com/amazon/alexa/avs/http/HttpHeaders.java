/**
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.amazon.alexa.avs.http;



public class HttpHeaders {
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  public static final String CONTENT_ID = "Content-ID";
  public static final String AUTHORIZATION = "Authorization";
  public static final String DIRECT_URL = "https://avs-alexa-na.amazon.com/v20160207/directives";
  public static final String EVENT_URL = "https://avs-alexa-na.amazon.com/v20160207/events";
  public static final String PING_URL = "https://avs-alexa-na.amazon.com/ping";
  public static final String MEDIATYPE_JSON = "application/json; charset=utf-8";
  public static final String MEDIATYPE_AUDIO = "application/octet-stream";
  public static final long CONNECTION_PING_MILLISECINDS = 5 * 60 * 1000;

  public static class Parameters {
    public static final String BOUNDARY = "boundary";
    public static final String CHARSET = "charset";
  }
}
