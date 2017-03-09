package com.amazon.alexa.setting;

import java.io.Serializable;
import java.util.List;

import com.amazon.alexa.message.MessageIdHeader;


@SuppressWarnings("serial")
public class SettingUpdatedEvent implements Serializable {
  MessageIdHeader header;
  SettingsUpdatedPayload payload;
  
  public SettingUpdatedEvent() {}

  public void setHeader(String namespace, String name) {
    header = new MessageIdHeader(namespace, name);
  }

  public void setPayLoad(List<Setting> settings) {
    payload = new SettingsUpdatedPayload(settings);
  }

  public MessageIdHeader getHeader() {
    return header;
  }

  public SettingsUpdatedPayload getPayLoad() {
    return payload;
  }

}
