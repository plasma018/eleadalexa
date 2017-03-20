package com.example.alexa.lib;

public class ControllerCenter {
  private String metadataToken;
  private String audioPlayerToken;
  private String speechStateToken;

  public void setMetadataToken(String Token) {
    this.metadataToken = Token;
  }

  public String getMetadataToken() {
    return metadataToken;
  }

  public void setAudioPlayerToken(String Token) {
    this.audioPlayerToken = Token;
  }

  public String getAudioPlayerToke() {
    return audioPlayerToken;
  }

  public void setSpeechStateToken(String Token) {
    this.speechStateToken = Token;
  }

  public String getSpeechStateToke() {
    return speechStateToken;
  }



}
