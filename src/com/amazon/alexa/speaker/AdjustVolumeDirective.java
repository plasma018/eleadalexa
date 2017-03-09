package com.amazon.alexa.speaker;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.context.VolumeStatePayload;

public class AdjustVolumeDirective {
  VolumeStatePayload payload;
  DialogRequestIdHeader header;

  public AdjustVolumeDirective() {};

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
