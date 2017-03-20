package com.amazon.alexa.audioplayer;

public class PlayPayload {
  private String playBehavior;
  private AudioItem audioItem;

  public PlayPayload() {}

  public static class PlayBehavior {
    public static final String REPLACE_ALL = "REPLACE_ALL";
    public static final String ENQUEUE = "ENQUEUE";
    public static final String REPLACE_ENQUEUED = "REPLACE_ENQUEUED";
  }

  public void setPlayBehavior(String playBehavior) {
    this.playBehavior = playBehavior;
  }

  public void setAudioItem(AudioItem audioItem) {
    this.audioItem = audioItem;
  }

  public String getPlayBehavior() {
    return playBehavior;
  }

  public AudioItem getAudioItem() {
    return audioItem;
  }


}
