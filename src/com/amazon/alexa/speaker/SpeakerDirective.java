package com.amazon.alexa.speaker;

import java.io.Serializable;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.context.VolumeStatePayload;

@SuppressWarnings("serial")
public class SpeakerDirective implements Serializable {
  VolumeStatePayload payload;
  DialogRequestIdHeader header;

  public SpeakerDirective() {};

  public void setHeader(String namespace, String name, String dialogRequestId) {
    header = new DialogRequestIdHeader(namespace, name, dialogRequestId);
  }

  public void setPayLoad(long volume, boolean muted) {
    payload = new VolumeStatePayload(volume, muted);
  }

  public DialogRequestIdHeader getHeader() {
    return header;
  }

  public VolumeStatePayload getPayLoad() {
    return payload;
  }
}


