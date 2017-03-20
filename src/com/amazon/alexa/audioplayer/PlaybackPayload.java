package com.amazon.alexa.audioplayer;

public class PlaybackPayload {
  private String token;
  private long offsetInMilliseconds;

  public PlaybackPayload() {}

  public PlaybackPayload(String token, long offsetInMilliseconds) {
    this.token = token;
    this.offsetInMilliseconds = offsetInMilliseconds;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setOffsetInMillis(long offsetInMilliseconds) {
    this.offsetInMilliseconds = offsetInMilliseconds;
  }

  public String getToken() {
    return token;
  }

  public long getOffsetInMillis() {
    return offsetInMilliseconds;
  }

}
