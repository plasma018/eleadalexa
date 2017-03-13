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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;
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
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;


public class ResponseParser {
  private static final String TAG = "ResponseParser";
  private static File file = new File("/sdcard/voice.mp3");


  // 播放聲音的資訊
  private static int sampleRate = 44100;
  private static boolean isPlaying = false;
  private static AudioTrack mAudioTrack;

  private static class DirectiveName {
    public static final String StopCapture = "StopCapture";
    public static final String ExpectSpeech = "ExpectSpeech";
    public static final String Speak = "Speak";
    public static final String SetVolume = "SetVolume";
    public static final String AdjustVolume = "AdjustVolume";
    public static final String SetMute = "SetMute";
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
        Log.i(TAG, "plasma018 header:" + header);
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
    } catch (Exception e) {
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
        Log.i(TAG, "item Namespace: " + speakerDirective.getHeader().getNamespace());
        Log.i(TAG, "item Name: " + speakerDirective.getHeader().getName());
        Log.i(TAG, "item MessageId: " + speakerDirective.getHeader().getMessageId());
        Log.i(TAG, "item DialogRequestId: " + speakerDirective.getHeader().getDialogRequestId());
        Log.i(TAG, "item url: " + speakerDirective.getPayload().getFormat());
        break;
      case DirectiveName.ExpectSpeech:
        break;
      default:
        break;
    }
  }
}
