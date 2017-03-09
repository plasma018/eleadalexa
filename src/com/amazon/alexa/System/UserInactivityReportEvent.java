package com.amazon.alexa.System;

import java.io.Serializable;

import com.amazon.alexa.message.Header;
import com.amazon.alexa.message.MessageIdHeader;

@SuppressWarnings("serial")
public class UserInactivityReportEvent implements Serializable {
  private MessageIdHeader header;
  private UserInactivityReportPayload payload;

  public UserInactivityReportEvent() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayLoad(long inactiveTimeInSeconds) {
    payload = new UserInactivityReportPayload(inactiveTimeInSeconds);
  }

  public Header getHeader() {
    return header;
  }

  public UserInactivityReportPayload getPayLoad() {
    return payload;
  }


}
