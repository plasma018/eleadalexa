package com.example.alexa.lib;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class Input {
  private static final String TAG = "Aufnahme";
  private AudioRecord recorder = null;
  private boolean isRecording = false;
  private int SAMPLERATE = 8000;
  private int CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;
  private int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
  private int bufferSize = AudioRecord.getMinBufferSize(SAMPLERATE, CHANNELS, AUDIO_FORMAT);
  private Thread recordingThread = null;

  public void startRecording() {
    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLERATE, CHANNELS, AUDIO_FORMAT,
        bufferSize);

    recorder.startRecording();
    isRecording = true;

    recordingThread = new Thread(new Runnable()

    {
      public void run() {
        writeAudioData();
      }

    });
    recordingThread.start();

  }

  public void stopRecording() {
    isRecording = false;
    recorder.stop();
    recorder.release();
    recorder = null;
    recordingThread = null;
  }

  private void writeAudioData() {

    byte data[] = new byte[bufferSize];

    while (isRecording) {

      recorder.read(data, 0, bufferSize);
      send(data);

    }
  }

  public void send(byte[] data) {

    int minBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
        AudioFormat.ENCODING_PCM_16BIT);

    AudioTrack at =
        new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
            AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

    at.play();
    at.write(data, 0, bufferSize);
    at.stop();
    at.release();

  }
}
