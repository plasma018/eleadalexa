package com.amazon.alexa.audioplayer;

import java.io.Serializable;

import com.amazon.alexa.message.MessageIdHeader;
import com.example.plasma.alexa.EventInterface;

@SuppressWarnings("serial")
public class PlaybackStartedEvent implements Serializable, EventInterface {
  MessageIdHeader header;
  PlaybackPayload payload;

  public PlaybackStartedEvent() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayload(String token, long offsetInMilliseconds) {
    payload = new PlaybackPayload(token, offsetInMilliseconds);
  }

  public MessageIdHeader getHeader() {
    return header;
  }

  public PlaybackPayload getPayload() {
    return payload;
  }


}
