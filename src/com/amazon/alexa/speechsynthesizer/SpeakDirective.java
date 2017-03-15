package com.amazon.alexa.speechsynthesizer;

import java.io.Serializable;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.example.plasma.alexa.Directive;


@SuppressWarnings("serial")
public class SpeakDirective implements Serializable, Directive {
  DialogRequestIdHeader header;
  SpeakDirectivePayload payload;

  public SpeakDirective() {}

  public void setHeader(String namespace, String name, String dialogRequestId) {
    header = new DialogRequestIdHeader(namespace, name, dialogRequestId);
  }

  public void setPayload(String url, String format, String token) {
    payload = new SpeakDirectivePayload();
    payload.setUrl(url);
    payload.setFormat(format);
    payload.setToken(token);
  }

  public DialogRequestIdHeader getHeader() {
    return header;
  }

  public SpeakDirectivePayload getPayload() {
    return payload;
  }
  
  
  
  
  
}
