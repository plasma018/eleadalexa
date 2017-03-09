package com.amazon.alexa.speaker;

import java.io.Serializable;

import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.context.VolumeStatePayload;

@SuppressWarnings("serial")
public class SpeakerEvent implements Serializable{
  MessageIdHeader header;
  VolumeStatePayload payload;

  public SpeakerEvent() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayLoad(long volume, boolean muted) {
    payload = new VolumeStatePayload(volume, muted);
  }

  public MessageIdHeader getHeader() {
    return header;
  }

  public VolumeStatePayload getPayLoad() {
    return payload;
  }
}
