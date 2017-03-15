package com.amazon.alexa.speechsynthesizer;

import java.io.Serializable;

import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.Payload;
import com.example.plasma.alexa.EventInterface;

@SuppressWarnings("serial")
public class SpeechStartedEvent implements Serializable, EventInterface {
  MessageIdHeader header;
  SpeechLifecyclePayload payload;

  public SpeechStartedEvent() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayload(String token) {
    payload = new SpeechLifecyclePayload(token);
  }

  public MessageIdHeader getHeader() {
    return header;
  }

  public Payload getPayLoad() {
    return payload;
  }

}
