package com.amazon.alexa.message.context;

import java.io.Serializable;

import com.amazon.alexa.message.Header;

@SuppressWarnings("serial")
public class SpeakerVolumeState implements Serializable, Context {
  Header header;
  VolumeStatePayload payload;

  public SpeakerVolumeState() {}

  public void setHeader(String namespace, String name) {
    header = new Header(namespace, name);
  }

  public void setPayLoad(long volume, boolean muted) {
    payload = new VolumeStatePayload(volume, muted);
  }

  public Header getHeader() {
    return header;
  }

  public VolumeStatePayload getPayLoad() {
    return payload;
  }

}
