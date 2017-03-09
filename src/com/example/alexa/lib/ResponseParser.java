package com.example.alexa.lib;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.fileupload.MultipartStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazon.alexa.message.context.Context;
import com.amazon.alexa.speaker.SpeakerDirective;
import com.amazon.alexa.speechsynthesizer.SpeakDirective;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


public class ResponseParser {
  private static final String TAG = "ResponseParser";
  private static InputStream fin;
  private static File file = new File("data/data/com.example.plasma.alexa/joke");
  private static int minBufferSize = AudioTrack.getMinBufferSize(8000,
      AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
  private static int bufferSize = 1024;
  private static AudioTrack at =
      new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
          AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STATIC);

  private static class DirectiveName {
    public static final String StopCapture = "StopCapture";
    public static final String ExpectSpeech = "ExpectSpeech";
    public static final String Speak = "Speak";
    public static final String SetVolume = "SetVolume";
    public static final String AdjustVolume = "AdjustVolume";
    public static final String SetMute = "SetMute";
  }

  public static void setContext(InputStream in) {
    fin = in;
  }

  public static void parseResponse(InputStream stream, String boundary)
      throws IOException, IllegalStateException {
    try {
      MultipartStream multipartStream =
          new MultipartStream(stream, boundary.getBytes(), 100000, null);
      boolean nextPart = multipartStream.skipPreamble();
      OutputStream output;
      while (nextPart) {
        String header = multipartStream.readHeaders();
        Log.i("plasm018", "plasma018 header:" + header);

        if (!isJson(header)) {
          ByteArrayOutputStream data = new ByteArrayOutputStream();
          multipartStream.readBodyData(data);
          output = new FileOutputStream(file);
          output.write(data.toByteArray());
          output.close();
          data.close();

        } else {
          ByteArrayOutputStream data = new ByteArrayOutputStream();
          multipartStream.readBodyData(data);
          String directive = data.toString(Charset.defaultCharset().displayName());
          ResponseParser.getDirective(directive);
        }
        nextPart = multipartStream.readBoundary();
      }
    } catch (

    MultipartStream.MalformedStreamException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private static boolean isJson(String headers) {
    if (headers.contains("application/json")) {
      return true;
    }
    return false;
  }

  private static void getDirective(String directive) throws JSONException {
    String directiveName = new JSONObject(directive).getJSONObject("directive")
        .getJSONObject("header").getString("name");

    switch (directiveName) {
      case DirectiveName.Speak:
        Gson gson = new Gson();
        SpeakDirective speakerDirective =
            gson.fromJson(new JSONObject(directive).getString("directive"), SpeakDirective.class);
        Log.i("plasm0a18", "item Namespace: " + speakerDirective.getHeader().getNamespace());
        Log.i("plasm0a18", "item Name: " + speakerDirective.getHeader().getName());
        Log.i("plasm0a18", "item MessageId: " + speakerDirective.getHeader().getMessageId());
        Log.i("plasm0a18",
            "item DialogRequestId: " + speakerDirective.getHeader().getDialogRequestId());
        Log.i("plasm0a18", "item url: " + speakerDirective.getPayload().getFormat());
        break;
      case DirectiveName.ExpectSpeech:
        break;
      default:
        break;
    }
  }


  private byte[] decode(byte[] content) {
    return content;
  }



  private static void play() {
    int i = 0;
    byte[] s = new byte[bufferSize];
    try {
      DataInputStream dis = new DataInputStream(fin);
      while ((i = dis.read(s, 0, bufferSize)) > -1) {
        at.write(s, 0, i);
        at.play();
      }
      at.stop();
      at.release();
      dis.close();
      fin.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
