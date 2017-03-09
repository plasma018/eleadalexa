package com.amazon.alexa.speaker;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.context.VolumeStatePayload;

public class VolumeChangedEvent {
  VolumeStatePayload payload;
  MessageIdHeader header;

  public VolumeChangedEvent() {};

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayLoad(long volume,boolean mute) {
    payload = new VolumeStatePayload(volume,mute);
  }

  public MessageIdHeader getHeader() {
    return header;
  }

  public VolumeStatePayload getPayLoad() {
    return payload;
  }

}
