package com.amazon.alexa.system;

import com.amazon.alexa.message.Header;
import com.amazon.alexa.message.MessageIdHeader;

public class ExceptionMessages {
  private MessageIdHeader header;
  private Exception payload;

  public ExceptionMessages() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayload(String code, String description) {
    payload = new Exception();
    payload.setCode(code);
    payload.setDescription(description);
  }

  public Header getHeader() {
    return header;
  }

  public Exception getPayLoad() {
    return payload;
  }
}
