package com.amazon.alexa.message.context;

import java.util.List;


public class ContextHeader {
  private List<Context> context;

  public ContextHeader(List<Context> context) {
    this.context = context;
  }

  public List<Context> getContext() {
    return context;
  }

}
