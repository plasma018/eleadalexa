package com.amazon.alexa.system;

import java.io.Serializable;
import java.util.List;

import com.amazon.alexa.avs.exception.DirectiveHandlingException.ExceptionType;
import com.amazon.alexa.message.MessageIdHeader;
import com.amazon.alexa.message.context.Context;

@SuppressWarnings("serial")
public class ExceptionEncounteredEvent implements Serializable {
  private Event event;
  private List<Context> context;

  public void setContext(List<Context> context) {
    this.context = context;
  }

  public void setEvent(String namespace, String name, String unparsedDirective, ExceptionType type,
      String message) {
    this.event = new Event();
    event.setHeader(namespace, name);
    event.setPayload(unparsedDirective, type, message);
  }

  public Event getEvent() {
    return event;
  }

  public List<Context> getContext() {
    return context;
  }

  public class Event {
    private MessageIdHeader header;
    private ExceptionEncounteredPayload payload;

    public void setHeader(String namespace, String name) {
      header = new MessageIdHeader(namespace, name);
    }

    public void setPayload(String unparsedDirective, ExceptionType type, String message) {
      payload = new ExceptionEncounteredPayload(unparsedDirective, type, message);
    }

    public MessageIdHeader getHeader() {
      return header;
    }

    public ExceptionEncounteredPayload getPayLoad() {
      return payload;
    }
  }
}
