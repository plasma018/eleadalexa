package com.amazon.alexa.speaker;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.amazon.alexa.message.context.VolumeStatePayload;

public class SetMuteDirective {
  VolumeStatePayload payload;
  DialogRequestIdHeader header;

  public SetMuteDirective() {};

  public void setHeader(String namespace, String name, String dialogRequestId) {
    header = new DialogRequestIdHeader(namespace, name, dialogRequestId);
  }

  public void setPayLoad(boolean mute) {
    payload = new VolumeStatePayload(mute);
  }

  public DialogRequestIdHeader getHeader() {
    return header;
  }

  public VolumeStatePayload getPayLoad() {
    return payload;
  }

}
