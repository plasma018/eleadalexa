package com.amazon.alexa.speaker;

import java.io.Serializable;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.context.VolumeStatePayload;

@SuppressWarnings("serial")
public class SetVolumeDirective implements Serializable {
  VolumeStatePayload payload;
  DialogRequestIdHeader header;

  public SetVolumeDirective() {};

  public void setHeader(String namespace, String name, String dialogRequestId) {
    header = new DialogRequestIdHeader(namespace, name, dialogRequestId);
  }

  public void setPayLoad(long volume) {
    payload = new VolumeStatePayload(volume);
  }

  public MessageIdHeader getHeader() {
    return header;
  }

  public VolumeStatePayload getPayLoad() {
    return payload;
  }
}
