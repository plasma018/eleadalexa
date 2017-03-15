package com.amazon.alexa.speechrecongizer;

import java.io.Serializable;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.amazon.alexa.message.Payload;



@SuppressWarnings("serial")
public class ExpectSpeechDirective implements Serializable {
  DialogRequestIdHeader header;
  Listen payload;

  public ExpectSpeechDirective() {}

  public void setHeader(String namespace, String name, String dialogRequestId) {
    header = new DialogRequestIdHeader(namespace, name, dialogRequestId);
  }

  public void setPayLoad(Long timeoutIntervalInMillis) {
    payload = new Listen();
    payload.setTimeoutIntervalInMillis(timeoutIntervalInMillis);
  }

  public DialogRequestIdHeader getHeader() {
    return header;
  }

  public Listen getPayLoad() {
    return payload;
  }
}


