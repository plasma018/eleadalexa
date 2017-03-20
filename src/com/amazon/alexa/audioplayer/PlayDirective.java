package com.amazon.alexa.audioplayer;

import java.io.Serializable;

import com.amazon.alexa.message.DialogRequestIdHeader;


@SuppressWarnings("serial")
public class PlayDirective implements Serializable {
  private DialogRequestIdHeader header;
  private PlayPayload payload;

  public PlayDirective() {}

  public void setHeader(String namespace, String name, String dialogRequestId) {
    header = new DialogRequestIdHeader(namespace, name, dialogRequestId);
  }

  public void setPlayPayload(String playBehavior, AudioItem audioItem) {
    payload.setPlayBehavior(playBehavior);
    payload.setAudioItem(audioItem);
  }

  public DialogRequestIdHeader getHeader() {
    return header;
  }

  public PlayPayload getPlayPayload() {
    return payload;
  }

}
