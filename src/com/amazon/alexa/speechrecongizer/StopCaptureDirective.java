package com.amazon.alexa.speechrecongizer;

import java.io.Serializable;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.Payload;
import com.amazon.alexa.message.context.VolumeStatePayload;

@SuppressWarnings("serial")
public class StopCaptureDirective implements Serializable {
  Payload payload;
  DialogRequestIdHeader header;

  public StopCaptureDirective() {}

  public void setHeader(String namespace, String name, String dialogRequestId) {
    header = new DialogRequestIdHeader(namespace, name, dialogRequestId);
  }

  public void setPayload(Object object) {
    payload = new Payload();
  };

  public DialogRequestIdHeader getHeader() {
    return header;
  }

  public Payload getPayLoad() {
    return payload;
  }

}
