package com.amazon.alexa.alerts;


public class Alert {
  private final String token;
  private final String type;
  private final String scheduledTime;

  public Alert(String token, String type, String scheduledTime) {
    this.token = token;
    this.type = type;
    this.scheduledTime = scheduledTime;
  }

  public String getToken() {
    return this.token;
  }

  public String getType() {
    return this.type;
  }

  public String getScheduledTime() {
    return scheduledTime;
  }


}
