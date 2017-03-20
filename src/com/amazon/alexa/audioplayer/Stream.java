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
package com.amazon.alexa.audioplayer;



import java.io.InputStream;

import com.google.gson.annotations.Expose;

public final class Stream {
  private String url;
  private String StreamFormat;
  private String token;
  private String expiryTime;
  private long offsetInMilliseconds;
  private ProgressReport progressReport;
  private String expectedPreviousToken;

  @Expose(serialize = false, deserialize = false)
  private InputStream attachedContent;
  @Expose(serialize = false, deserialize = false)
  private boolean urlIsAContentId;

  public String getUrl() {
    return url;
  }

  public String getStreamFormat() {
    return StreamFormat;
  }

  public long getOffsetInMilliseconds() {
    return offsetInMilliseconds;
  }

  public String getExpiryTime() {
    return expiryTime;
  }

  public ProgressReport getProgressReport() {
    return progressReport;
  }

  public String getToken() {
    return token;
  }

  public String getExpectedPreviousToken() {
    return expectedPreviousToken;
  }

  public void setUrl(String url) {
    urlIsAContentId = url.startsWith("cid");
    if (urlIsAContentId) {
      this.url = url.substring(4);
    } else {
      this.url = url;
    }
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setStreamFormat(String format) {
    this.StreamFormat = format;
  }

  public void setExpiryTime(String expiryTime) {
    this.expiryTime = expiryTime;
  }

  public void setProgressReport(ProgressReport progressReport) {
    this.progressReport = progressReport;
  }

  public void setExpectedPreviousToken(String expectedPreviousToken) {
    this.expectedPreviousToken = expectedPreviousToken;
  }

}
