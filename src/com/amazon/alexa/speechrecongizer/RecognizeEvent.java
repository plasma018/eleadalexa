package com.amazon.alexa.speechrecongizer;

import java.io.Serializable;
import java.util.List;

import com.amazon.alexa.message.DialogRequestIdHeader;
import com.amazon.alexa.message.context.Context;
import com.amazon.alexa.speechrecongizer.SpeechRecognizerPayload.SpeechProfile;

@SuppressWarnings("serial")
public class RecognizeEvent implements Serializable {
  private List<Context> context;
  private Event event;

  public RecognizeEvent() {}

  public void setContext(List<Context> context) {
    this.context = context;
  }

  public void setEvent(String namespace, String name, String dialogRequestId, SpeechProfile profile,
      String format) {
    this.event = new Event();
    event.setHeader(namespace, name, dialogRequestId);
    event.setSpeechRecognizerPayload(profile, format);
  }

  public Event getEvent() {
    return event;
  }

  public List<Context> getContext() {
    return context;
  }


  public class Event {
    private DialogRequestIdHeader header;
    private SpeechRecognizerPayload payload;

    public void setHeader(String namespace, String name, String dialogRequestId) {
      header = new DialogRequestIdHeader(namespace, name, dialogRequestId);
    }

    public void setSpeechRecognizerPayload(SpeechProfile profile, String format) {
      payload = new SpeechRecognizerPayload(profile, format);
    };

    public DialogRequestIdHeader getHeader() {
      return header;
    }

    public SpeechRecognizerPayload getPayLoad() {
      return payload;
    }
  }


}
