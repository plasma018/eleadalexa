package com.amazon.alexa.message.context;

import java.io.Serializable;
import com.amazon.alexa.message.Header;
import com.amazon.alexa.message.context.PlaybackStatePayload.PlayerActivity;

@SuppressWarnings("serial")
public class SpeechSynthsizerSpeechState implements Serializable, Context {
  Header header;
  SpeechStatePayload payload;

  public SpeechSynthsizerSpeechState() {}

  public void setHeader(String namespace, String name) {
    header = new Header(namespace, name);
  }

  public void setPayLoad(String token, long offsetInMilliseconds, PlayerActivity playerActivity) {
    payload = new SpeechStatePayload(token, offsetInMilliseconds, playerActivity);
  }

  public Header getHeader() {
    return header;
  }

  public SpeechStatePayload getPayLoad() {
    return payload;
  }
}
