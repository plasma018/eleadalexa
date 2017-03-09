package com.amazon.alexa.message.context;

import java.io.Serializable;
import java.util.List;

import com.amazon.alexa.alerts.Alert;
import com.amazon.alexa.message.Header;

@SuppressWarnings("serial")
public class AlertsState implements Serializable, Context  {
  Header header;
  AlertsStatePayload payload;

  public AlertsState() {}

  public void setHeader(String namespace, String name) {
    header = new Header(namespace, name);
  }

  public void setPayload(List<Alert> all, List<Alert> active) {
    payload = new AlertsStatePayload(all, active);
  }

  public Header getHeader() {
    return header;
  }

  public AlertsStatePayload getPayload() {
    return payload;
  }


}
