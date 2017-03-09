package com.example.plasma.alexa;

import java.util.ArrayList;
import java.util.List;

import com.amazon.alexa.System.ExceptionEncounteredEvent;
import com.amazon.alexa.System.SynchronizeStateEvent;
import com.amazon.alexa.alerts.Alert;
import com.amazon.alexa.avs.exception.DirectiveHandlingException.ExceptionType;
import com.amazon.alexa.message.context.AlertsState;
import com.amazon.alexa.message.context.AudioPlayerPlaybackState;
import com.amazon.alexa.message.context.Context;
import com.amazon.alexa.message.context.ContextHeader;
import com.amazon.alexa.message.context.PlaybackStatePayload.PlayerActivity;
import com.amazon.alexa.message.context.SpeakerVolumeState;
import com.amazon.alexa.message.context.SpeechSynthsizerSpeechState;
import com.amazon.alexa.setting.LocaleSetting;
import com.amazon.alexa.setting.Setting;
import com.amazon.alexa.setting.SettingEvent;
import com.amazon.alexa.setting.SettingUpdatedEvent;
import com.amazon.alexa.speaker.MuteChangedEvent;
import com.amazon.alexa.speaker.SetMuteDirective;
import com.amazon.alexa.speaker.SetVolumeDirective;
import com.amazon.alexa.speaker.SpeakerDirective;
import com.amazon.alexa.speaker.SpeakerEvent;
import com.amazon.alexa.speaker.VolumeChangedEvent;
import com.amazon.alexa.speechrecongizer.ExpectSpeechDirective;
import com.amazon.alexa.speechrecongizer.ExpectSpeechTimedOutEvent;
import com.amazon.alexa.speechrecongizer.RecognizeEvent;
import com.amazon.alexa.speechrecongizer.SpeechRecognizerPayload.SpeechProfile;
import com.amazon.alexa.speechrecongizer.StopCaptureDirective;
import com.amazon.alexa.speechsynthesizer.SpeakDirective;
import com.amazon.alexa.speechsynthesizer.SpeechFinishedEvent;
import com.amazon.alexa.speechsynthesizer.SpeechStartedEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.util.Log;

public class JSONTest {
  // SpeechRecognizer Interface
  public static String recognizeEvent() {
    AudioPlayerPlaybackState playbackState = new AudioPlayerPlaybackState();
    playbackState.setHeader("AudioPlayer", "PlaybackState");
    playbackState.setPayLoad("", 0, PlayerActivity.IDLE);

    List<Alert> allAlerts = new ArrayList<Alert>();
    AlertsState alertsState = new AlertsState();
    alertsState.setHeader("Alerts", "AlertsState");
    alertsState.setPayload(allAlerts, allAlerts);

    SpeakerVolumeState volumeState = new SpeakerVolumeState();
    volumeState.setHeader("Speaker", "VolumeState");
    volumeState.setPayLoad(100, false);

    SpeechSynthsizerSpeechState speechState = new SpeechSynthsizerSpeechState();
    speechState.setHeader("SpeechSynthesizer", "SpeechState");
    speechState.setPayLoad("", 0, PlayerActivity.IDLE);

    List<Context> context = new ArrayList<>();
    context.add(alertsState);
    context.add(playbackState);
    context.add(volumeState);
    context.add(speechState);

    RecognizeEvent recognizeEvent = new RecognizeEvent();
    recognizeEvent.setContext(context);
    recognizeEvent.setEvent("SpeechRecognizer", "Recognize", "plasma", SpeechProfile.CLOSE_TALK,
        "AUDIO_L16_RATE_16000_CHANNELS_1");
     Log.i("plasma018", "plasma018 recognizeEvent: " + new Gson().toJson(recognizeEvent));
    return new Gson().toJson(recognizeEvent);
  }



  // 有問題
  public static void stopCaptureDirective() {
    String string = new String("{\"header\": {\"namespace\": \"SpeechRecognizer\","
        + "\"name\": \"StopCapture\",\"messageId\": \"123\",\"dialogRequestId\": \"123\" },"
        + "\"payload\": {}");
    StopCaptureDirective stopCaptureDirective =
        new Gson().fromJson(string, StopCaptureDirective.class);
    Log.i("plasma018",
        "plasma018 stopCaptureDirective: " + new Gson().toJson(stopCaptureDirective));
  }

  public static void expectSpeechEvent() {
    ExpectSpeechTimedOutEvent expectSpeechEvent = new ExpectSpeechTimedOutEvent();
    expectSpeechEvent.setHeader("SpeechRecognizer", "ExpectSpeechTimedOut");
    Log.i("plasma018", "plasma018 expectSpeechEvent: " + new Gson().toJson(expectSpeechEvent));
  }

  public static void expectSpeechDirective() {
    String string = new String("{\"header\": {\"namespace\": \"SpeechRecognizer\","
        + "\"name\": \"ExpectSpeech\",\"messageId\": \"123\",\"dialogRequestId\": \"123\" },"
        + "\"payload\": {\"timeoutInMilliseconds\": 10.0}}");
    ExpectSpeechDirective expectSpeechDirective =
        new Gson().fromJson(string, ExpectSpeechDirective.class);
    Log.i("plasma018",
        "plasma018 expectSpeechDirective: " + new Gson().toJson(expectSpeechDirective));
  }

  // SpeechSynthesizer Interface

  public static void speakDirective() {
    String string = new String("{\"header\": {\"namespace\": \"SpeechSynthesizer\","
        + "\"name\": \"Speak\",\"messageId\": \"123\",\"dialogRequestId\": \"123\" },"
        + "\"payload\": {\"url\": \"http://www.facebook.com/\",\"format\": \"AUDIO_MPEG\", \"token\": \"token123456\"}}");
    SpeakDirective speakDirective = new Gson().fromJson(string, SpeakDirective.class);
    Log.i("plasma018", "plasma018 speakDirective: " + new Gson().toJson(speakDirective));

  }

  public static void speechStartedEvent() {
    SpeechStartedEvent SpeechStartedEvent = new SpeechStartedEvent();
    SpeechStartedEvent.setHeader("SpeechSynthesizer", "SpeechStarted");
    SpeechStartedEvent.setPayload("token:123456");
    Log.i("plasma018", "plasma018 SpeakerEvent: " + new Gson().toJson(SpeechStartedEvent));
  }

  public static void speechFinishedEvent() {
    SpeechFinishedEvent speechFinishedEvent = new SpeechFinishedEvent();
    speechFinishedEvent.setHeader("SpeechSynthesizer", "SpeechFinished");
    speechFinishedEvent.setPayload("token:123456");
    Log.i("plasma018", "plasma018 SpeakerEvent: " + new Gson().toJson(speechFinishedEvent));
  }

  // Alerts Interface



  // Speaker Interface
  public static void setVolumeDirective() {
    SetVolumeDirective setVolumeDirective = new SetVolumeDirective();
    setVolumeDirective.setHeader("Speaker", "SetVolume", "dialogRequestId");
    setVolumeDirective.setPayLoad(100);
    Log.i("plasma018", "plasma018 setVolumeDirective: " + new Gson().toJson(setVolumeDirective));
  }

  public static void adjustVolumeDirective() {
    SetVolumeDirective setVolumeDirective = new SetVolumeDirective();
    setVolumeDirective.setHeader("Speaker", "AdjustVolume", "dialogRequestId");
    setVolumeDirective.setPayLoad(100);
    Log.i("plasma018", "plasma018 setVolumeDirective: " + new Gson().toJson(setVolumeDirective));
  }

  public static void volumeChangedEvent() {
    VolumeChangedEvent volumeChangedEvent = new VolumeChangedEvent();
    volumeChangedEvent.setHeader("Speaker", "VolumeChanged");
    volumeChangedEvent.setPayLoad(100, true);
    Log.i("plasma018", "plasma018 setVolumeDirective: " + new Gson().toJson(volumeChangedEvent));
  }


  public static void setMuteDirective() {
    SetMuteDirective setMuteDirective = new SetMuteDirective();
    setMuteDirective.setHeader("Speaker", "SetMute", "dialogRequestId");
    setMuteDirective.setPayLoad(false);
    Log.i("plasma018", "plasma018 setVolumeDirective: " + new Gson().toJson(setMuteDirective));
  }

  public static void muteChangedEvent() {
    MuteChangedEvent muteChangedEvent = new MuteChangedEvent();
    muteChangedEvent.setHeader("Speaker", "MuteChanged");
    muteChangedEvent.setPayLoad(100, false);
    Log.i("plasma018", "plasma018 muteChangedEvent: " + new Gson().toJson(muteChangedEvent));
  }


  //
  public static void speakerEvent() {
    SpeakerEvent speaker = new SpeakerEvent();
    speaker.setHeader("Speaker", "SetMute");
    speaker.setPayLoad((long) 10.0, true);
    Log.i("plasma018", "plasma018 SpeakerEvent: " + new Gson().toJson(speaker));
  }

  public static void speakerDirective() {
    String string = new String("{\"header\": {\"namespace\": \"Speaker\","
        + "\"name\": \"SetVolume\",\"messageId\": \"123\",\"dialogRequestId\": \"123\" },"
        + "\"payload\": {\"volume\": 10.0}}");
    SpeakerDirective speaker_1 = new Gson().fromJson(string, SpeakerDirective.class);
    Log.i("plasma018", "plasma018 SpeakerDirective: " + new Gson().toJson(speaker_1));
  }

  // Settings Interface

  public static void settingEvent() {
    ArrayList<Setting> arraylist = new ArrayList<Setting>();
    Setting setting = new LocaleSetting("eee");
    arraylist.add(setting);
    arraylist.add(setting);
    SettingEvent settingEvent = new SettingEvent();
    settingEvent.setHeader("Settings", "SettingsUpdated");
    settingEvent.setPayLoad(arraylist);
    Log.i("plasma018", "plasma018 SettingEvent: " + new Gson().toJson(settingEvent));
  }

  public static void settingUpdatedEvent() {
    ArrayList<Setting> arraylist = new ArrayList<Setting>();
    Setting setting = new LocaleSetting("eee");
    arraylist.add(setting);
    arraylist.add(setting);
    SettingUpdatedEvent settingUpdatedEvent = new SettingUpdatedEvent();
    settingUpdatedEvent.setHeader("Settings", "SettingsUpdated");
    settingUpdatedEvent.setPayLoad(arraylist);
    Log.i("plasma018", "plasma018 settingUpdatedEvent: " + new Gson().toJson(settingUpdatedEvent));
  }

  // Context
  public static void getContext() {
    AudioPlayerPlaybackState playbackState = new AudioPlayerPlaybackState();
    playbackState.setHeader("AudioPlayer", "PlaybackState");
    playbackState.setPayLoad("token123456", 12345678, PlayerActivity.IDLE);

    Alert a = new Alert("token123456", "typeA", "scheduledTime");
    Alert b = new Alert("token123456", "typeB", "scheduledTime");
    Alert c = new Alert("token123456", "typeC", "scheduledTime");
    List<Alert> allAlerts = new ArrayList<Alert>();
    allAlerts.add(a);
    allAlerts.add(b);
    allAlerts.add(c);

    AlertsState alertsState = new AlertsState();
    alertsState.setHeader("Alerts", "AlertsState");
    alertsState.setPayload(allAlerts, allAlerts);


    SpeakerVolumeState volumeState = new SpeakerVolumeState();
    volumeState.setHeader("Speaker", "VolumeState");
    volumeState.setPayLoad(100, true);


    SpeechSynthsizerSpeechState speechState = new SpeechSynthsizerSpeechState();
    speechState.setHeader("SpeechSynthesizer", "SpeechState");
    speechState.setPayLoad("token123456", 1000, PlayerActivity.IDLE);

    List<Context> context = new ArrayList<>();
    context.add(playbackState);
    context.add(alertsState);
    context.add(volumeState);
    context.add(speechState);
    ContextHeader conextHeader = new ContextHeader(context);
    Log.i("plasma018", "plasma018 getContext: " + new Gson().toJson(conextHeader));
  }

  // System Interface
  public static String synchronizeStateEvent() {
    AudioPlayerPlaybackState playbackState = new AudioPlayerPlaybackState();
    playbackState.setHeader("AudioPlayer", "PlaybackState");
    playbackState.setPayLoad("", 0, PlayerActivity.IDLE);

    // Alert a = new Alert("token123456", "typeA", "scheduledTime");
    // Alert b = new Alert("token123456", "typeB", "scheduledTime");
    // Alert c = new Alert("token123456", "typeC", "scheduledTime");

    List<Alert> allAlerts = new ArrayList<Alert>();
    AlertsState alertsState = new AlertsState();
    alertsState.setHeader("Alerts", "AlertsState");
    alertsState.setPayload(allAlerts, allAlerts);

    SpeakerVolumeState volumeState = new SpeakerVolumeState();
    volumeState.setHeader("Speaker", "VolumeState");
    volumeState.setPayLoad(100, false);

    SpeechSynthsizerSpeechState speechState = new SpeechSynthsizerSpeechState();
    speechState.setHeader("SpeechSynthesizer", "SpeechState");
    speechState.setPayLoad("", 0, PlayerActivity.IDLE);

    List<Context> context = new ArrayList<>();
    context.add(alertsState);
    context.add(playbackState);
    context.add(volumeState);
    context.add(speechState);
    SynchronizeStateEvent synchronizeStateEvent = new SynchronizeStateEvent();
    synchronizeStateEvent.setContext(context);
    synchronizeStateEvent.setEvent("System", "SynchronizeState");
    // Log.i("plasma018",
    // "plasma018 synchronizeStateEvent: " + new Gson().toJson(synchronizeStateEvent));
    GsonBuilder bulider = new GsonBuilder();
    bulider.serializeNulls();
    return bulider.create().toJson(synchronizeStateEvent);
  }

  public static String exceptionEncounteredEvent() {
    AudioPlayerPlaybackState playbackState = new AudioPlayerPlaybackState();
    playbackState.setHeader("AudioPlayer", "PlaybackState");
    playbackState.setPayLoad("token123456", 12345678, PlayerActivity.IDLE);

    Alert a = new Alert("token123456", "typeA", "scheduledTime");
    Alert b = new Alert("token123456", "typeB", "scheduledTime");
    Alert c = new Alert("token123456", "typeC", "scheduledTime");
    List<Alert> allAlerts = new ArrayList<Alert>();
    allAlerts.add(a);
    allAlerts.add(b);
    allAlerts.add(c);

    AlertsState alertsState = new AlertsState();
    alertsState.setHeader("Alerts", "AlertsState");
    alertsState.setPayload(allAlerts, allAlerts);

    SpeakerVolumeState volumeState = new SpeakerVolumeState();
    volumeState.setHeader("Speaker", "VolumeState");
    volumeState.setPayLoad(100, true);

    SpeechSynthsizerSpeechState speechState = new SpeechSynthsizerSpeechState();
    speechState.setHeader("SpeechSynthesizer", "SpeechState");
    speechState.setPayLoad("token123456", 1000, PlayerActivity.IDLE);

    List<Context> context = new ArrayList<>();
    context.add(playbackState);
    context.add(alertsState);
    context.add(volumeState);
    context.add(speechState);

    ExceptionEncounteredEvent exceptionEncounteredEvent = new ExceptionEncounteredEvent();
    exceptionEncounteredEvent.setContext(context);
    exceptionEncounteredEvent.setEvent("System", "ExceptionEncountered", "unparsedDirective",
        ExceptionType.UNEXPECTED_INFORMATION_RECEIVED,
        "The directive sent to your client was malformed or the payload does not conform to the directive specification.");
    // Log.i("plasma018",
    // "plasma018 exceptionEncounteredEvent: " + new Gson().toJson(exceptionEncounteredEvent));
    return new Gson().toJson(exceptionEncounteredEvent);
  }



}
