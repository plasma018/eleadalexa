package com.amazon.alexa.system;

import java.io.Serializable;

import com.amazon.alexa.message.Header;
import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.Payload;
import com.amazon.alexa.message.context.PlaybackStatePayload;


@SuppressWarnings("serial")
public class ResetUserInactivityDirective implements Serializable {
  Payload payload = new Payload();
  MessageIdHeader header;

  public ResetUserInactivityDirective() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }
  public void setPayload(){}
  
  public Header getHeader() {
    return header;
  }

  public Payload getPayLoad() {
    return payload;
  }



}
