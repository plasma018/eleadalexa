package com.amazon.alexa.System;

import java.io.Serializable;
import java.util.List;

import com.amazon.alexa.System.ExceptionEncounteredEvent.Event;
import com.amazon.alexa.avs.exception.DirectiveHandlingException.ExceptionType;
import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.Payload;
import com.amazon.alexa.message.context.Context;

@SuppressWarnings("serial")
public class SynchronizeStateEvent implements Serializable {
  private List<Context> context;
  private Event event;

  // private MessageIdHeader header;
  // private Payload payload = new Payload();

  public SynchronizeStateEvent() {}

  // public void setHeader(String namespace, String name) {
  // header = new MessageIdHeader(namespace, name);
  // }

  // public void setPayload() {};

  public void setContext(List<Context> context) {
    this.context = context;
  }

  public void setEvent(String namespace, String name) {
    this.event = new Event();
    event.setHeader(namespace, name);
  }

  public Event getEvent() {
    return event;
  }

  public List<Context> getContext() {
    return context;
  }

  // public MessageIdHeader getHeader() {
  // return header;
  // }
  //
  // public Payload getPayLoad() {
  // return payload;
  // }

  public class Event {
    private MessageIdHeader header;
    private Payload payload = new Payload();

    public void setHeader(String namespace, String name) {
      header = new MessageIdHeader(namespace, name);
    }

    public void setPayload() {};

    public MessageIdHeader getHeader() {
      return header;
    }

    public Payload getPayLoad() {
      return payload;
    }
  }



}
