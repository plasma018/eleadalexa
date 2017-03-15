package com.amazon.alexa.speechrecongizer;

import java.io.Serializable;

import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.Payload;
import com.example.plasma.alexa.EventInterface;

@SuppressWarnings("serial")
public class ExpectSpeechTimedOutEvent implements Serializable, EventInterface {
  MessageIdHeader header;
  Payload payload = new Payload();

  public ExpectSpeechTimedOutEvent() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayload() {};

  public MessageIdHeader getHeader() {
    return header;
  }

  public Payload getPayLoad() {
    return payload;
  }


}
