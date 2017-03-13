package com.example.plasma.alexa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import com.amazon.alexa.avs.http.HttpHeaders;

import com.example.alexa.lib.ResponseParser;

import com.example.plasma.alexa.VoiceClientActivity.RecordTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaCodec.BufferInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.media.MediaFormat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@SuppressLint("Instantiatable")
public class PlasmaService extends Service {
  private static final String TAG = "PlasmaService";
  private Context mApplicationContext;
  private MainActivity mainActivity;
  private OkHttpClient downChannelClient;
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private String loginToken;
  private String url;
  private static int number = 0;

  // 錄音程式的資訊
  private static final int RECORDER_SAMPLERATE = 16000;
  private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
  private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  private RecordTask recorderTask = null;
  private boolean isRecording = false;

  private MediaPlayer mPlayer;

  // 播放聲音的資訊
  private MediaCodec codec;
  private MediaExtractor extractor;
  private MediaFormat format;
  private ByteBuffer[] codecInputBuffers;
  private ByteBuffer[] codecOutputBuffers;
  private Boolean sawInputEOS = false;
  private Boolean sawOutputEOS = false;
  private boolean isPlaying = false;
  private AudioTrack mAudioTrack;
  private BufferInfo info;

  private File audioFile = new File("/sdcard/voice16K16bitmono.raw");
  private ByteArrayOutputStream dos;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, TAG + " onCreate");
    init();

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, TAG + " onStartCommand--> " + intent);
    int ret = super.onStartCommand(intent, flags, startId);
    String action = intent.getAction();
    switch (action) {
      case App.ServiceAction.startService:
        loginToken = intent.getStringExtra("token");
        Log.i(TAG, TAG + "token: " + loginToken);
        startService();
        break;
      default:
        Log.i(TAG, TAG + "onStartCommand default nothing to say");
        break;
    }
    return ret;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i(TAG, TAG + " onDestroy");
    mPlayer.stop();
  }

  private void init() {
    mApplicationContext = getApplicationContext();
    mainActivity = App.getMainActivity();
    mainActivity.setService(this);
  }

  public void RecordVoice() {
    recorderTask = new RecordTask();
    recorderTask.execute();
  }

  public void StopRecordVoice() {
    this.isRecording = false;
    sendMessage();
  }


  class RecordTask extends AsyncTask<Void, Integer, Void> {
    @Override
    protected Void doInBackground(Void... arg0) {
      isRecording = true;
      try {
        dos = new ByteArrayOutputStream();

        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS,
            RECORDER_AUDIO_ENCODING);

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
            RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);

        byte[] buffer = new byte[bufferSize];
        record.startRecording();

        // 定义循环，根据isRecording的值来判断是否继续录制
        while (isRecording) {
          int bufferReadResult = record.read(buffer, 0, 160);
          // 循环将buffer中的音频数据写入到OutputStream中
          for (int i = 0; i < bufferReadResult; i++) {
            dos.write(buffer[i]);
          }
        }
        record.stop();
      } catch (Exception e) {
        Log.i(TAG, TAG + " RecordTask error");
        e.printStackTrace();
      }
      return null;
    }

    // 当在上面方法中调用publishProgress时，该方法触发,该方法在UI线程中被执行
    protected void onProgressUpdate(Integer... progress) {
      // stateView.setText(progress[0].toString());
    }

    protected void onPostExecute(Void result) {}

    protected void onPreExecute() {}

  }

  //
  private void startService() {
    new Thread() {
      @Override
      public void run() {
        try {

          OkHttpClient.Builder clientBuilder =
              new OkHttpClient.Builder().connectTimeout(0, TimeUnit.SECONDS);

          downChannelClient = clientBuilder.build();
          Request downChannelRequest = new Request.Builder().get()
              .url("https://avs-alexa-na.amazon.com/v20160207/directives")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken).build();
          Response downChannelResponse;
          downChannelResponse = downChannelClient.newCall(downChannelRequest).execute();
          Log.i(TAG, "start downChannel  code:" + downChannelResponse.code());
          Log.i(TAG, "start downChannel headers: " + downChannelResponse.headers());

          MultipartBody mBody = null;
          Request.Builder syncRequestBuilder =
              new Request.Builder().url("https://avs-alexa-na.amazon.com/v20160207/events")
                  .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
                  .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=__BOUNDARY__")
                  .method("POST", mBody);

          mBody = new MultipartBody.Builder("__BOUNDARY__").setType(MultipartBody.FORM)
              .addFormDataPart("name", "metadata",
                  okhttp3.RequestBody.create(MediaType.parse("application/json; charset=UTF-8"),
                      JSONTest.synchronizeStateEvent()))
              .build();

          Request syncRequest = syncRequestBuilder.build();
          Log.i(TAG, "syncRequest: " + syncRequest.toString());
          Response syncResponse = downChannelClient.newCall(syncRequest).execute();
          Log.i(TAG, "syncResponse code:" + syncResponse.code());
          Log.i(TAG, "syncResponse headers: " + syncResponse.headers());
          Log.i(TAG, "syncResponse body: " + syncResponse.body().string());
          syncResponse.close();

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }


  private void sendMessage() {
    new Thread() {
      @Override
      public void run() {
        try {
          // InputStream in = getAssets().open("weather.raw");
          // int byteCount = in.available();
          // byte[] buff = new byte[byteCount];
          // in.read(buff, 0, byteCount);

          OkHttpClient.Builder clientBuilder =
              new OkHttpClient.Builder().connectTimeout(0, TimeUnit.SECONDS);
          downChannelClient = clientBuilder.build();

          Request request = new Request.Builder().get()
              .url("https://avs-alexa-na.amazon.com/v20160207/directives")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken).build();
          Response response = downChannelClient.newCall(request).execute();
          Log.i("plasma018", "plasma018 code:" + response.code());
          Log.i("plasma018", "plasma018 headers: " + response.headers());


          MultipartBody mBody = null;
          Request.Builder request2 =
              new Request.Builder().url("https://avs-alexa-na.amazon.com/v20160207/events")
                  .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
                  .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=__BOUNDARY__");

          mBody = new MultipartBody.Builder("__BOUNDARY__").setType(MultipartBody.FORM)
              .addFormDataPart("name", "metadata",
                  okhttp3.RequestBody.create(MediaType.parse("application/json; charset=UTF-8"),
                      JSONTest.synchronizeStateEvent()))
              .build();

          request2.method("POST", mBody);
          Request request3 = request2.build();
          Log.i("plasma018", "plasma018 Request: " + request3.toString());
          Response response2 = downChannelClient.newCall(request3).execute();

          Log.i("plasma018", "plasma018 response2 code:" + response2.code());
          Log.i("plasma018", "plasma018 response2 headers: " + response2.headers());
          Log.i("plasma018", "plasm0a18 response2 body: " + response2.body().string());
          response2.close();

          Request.Builder request_audio =
              new Request.Builder().url("https://avs-alexa-na.amazon.com/v20160207/events")
                  .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
                  .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=__BOUNDARY__");
          MultipartBody mBody_audio = null;
          mBody_audio = new MultipartBody.Builder("__BOUNDARY__").setType(MultipartBody.FORM)
              .addFormDataPart("name", "metadata",
                  okhttp3.RequestBody.create(MediaType.parse("application/json; charset=UTF-8"),
                      JSONTest.recognizeEvent()))
              .addFormDataPart("name", "audio", okhttp3.RequestBody
                  .create(MediaType.parse("application/octet-stream"), dos.toByteArray()))
              .build();


          request_audio.method("POST", mBody_audio);
          Request requestAudio = request_audio.build();
          OkHttpClient client = new OkHttpClient();
          client.newCall(requestAudio).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
              Log.i("plasma018", "plasma018 responseAudio code:" + arg1.code());
              mainActivity.setStatusGone();
              if (arg1.code() == 200) {
                String boundary = arg1.headers().get("content-type").split(";")[1].substring(9);
                Log.i("plasma018", "plasma018 responseAudio boundary: " + boundary);
                ResponseParser.parseResponse(arg1.body().byteStream(), boundary);
                mediaPlay();
              }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
              Log.i("plasma018", "plasma018 responseAudio onFailure: " + arg1.getMessage());
            }
          });
        } catch (MalformedURLException e) {
          Log.i("plasma018", "MalformedURLException: " + e);
          e.printStackTrace();
        } catch (IOException e) {

          Log.i("plasma018", "IOException: " + e);
          e.printStackTrace();
        }
      };
    }.start();
  }

  public void mediaPlay() {
    mPlayer = MediaPlayer.create(this, Uri.parse("/sdcard/voice.mp3"));
    mPlayer.start();
  }
}
