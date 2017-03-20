package com.amazon.alexa.system;

import java.io.Serializable;

import com.amazon.alexa.message.Header;
import com.amazon.alexa.message.MessageIdHeader;


@SuppressWarnings("serial")
public class SetEndpointDirective implements Serializable {
  SetEndpointPayload payload;
  MessageIdHeader header;

  public SetEndpointDirective() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayload(String endpoint) {
    payload = new SetEndpointPayload();
    payload.setEndpoint(endpoint);
  }

  public Header getHeader() {
    return header;
  }

  public SetEndpointPayload getPayLoad() {
    return payload;
  }


}
