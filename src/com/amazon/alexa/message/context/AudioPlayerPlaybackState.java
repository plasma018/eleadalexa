package com.amazon.alexa.message.context;

import com.amazon.alexa.message.Header;
import com.amazon.alexa.message.context.PlaybackStatePayload.PlayerActivity;

public class AudioPlayerPlaybackState implements Context {

  Header header;
  PlaybackStatePayload payload;

  public AudioPlayerPlaybackState() {}

  public void setHeader(String namespace, String name) {
    header = new Header(namespace, name);
  }

  public void setPayLoad(String token, long offsetInMilliseconds, PlayerActivity playerActivity) {
    payload = new PlaybackStatePayload(token, offsetInMilliseconds, playerActivity);
  }

  public Header getHeader() {
    return header;
  }

  public PlaybackStatePayload getPayLoad() {
    return payload;
  }
}
