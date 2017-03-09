package com.amazon.alexa.speechsynthesizer;

import java.io.Serializable;

import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.Payload;

@SuppressWarnings("serial")
public class SpeechFinishedEvent implements Serializable {
  MessageIdHeader header;
  SpeechLifecyclePayload payload;

  public SpeechFinishedEvent() {}

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
