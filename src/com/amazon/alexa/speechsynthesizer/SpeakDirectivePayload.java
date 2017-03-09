package com.amazon.alexa.speechsynthesizer;

import com.amazon.alexa.message.Payload;

public class SpeakDirectivePayload extends Payload {
  private String url;
  private String format;
  private String token;

  public String getUrl() {
    return url;
  }

  public String getFormat() {
    return format;
  }

  public String getToken() {
    return token;
  }

  public void setUrl(String url) {
    // The format we get from the server has the audioContentId as "cid:%CONTENT_ID%" whereas
    // the actual Content-ID HTTP Header value is "%CONTENT_ID%".
    // This normalizes that
    this.url = url.substring(4);
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public void setToken(String token) {
    this.token = token;
  }


}
