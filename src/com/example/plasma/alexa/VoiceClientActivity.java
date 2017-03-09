package com.example.plasma.alexa;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.amazon.alexa.avs.http.HttpHeaders;
import com.amazon.alexa.setting.LocaleSetting;
import com.amazon.alexa.setting.Setting;
import com.amazon.alexa.setting.SettingEvent;
import com.amazon.alexa.speaker.SpeakerDirective;
import com.amazon.alexa.speaker.SpeakerEvent;
import com.example.alexa.lib.NoSSLv3SocketFactory;
import com.example.alexa.lib.ResponseParser;
import com.example.alexa.lib.Tls12SocketFactory;
import com.google.gson.Gson;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okio.Buffer;
import okio.BufferedSource;


public class VoiceClientActivity extends Activity {
  private OkHttpClient client = new OkHttpClient();
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private String token;
  private byte[] fileBytes;
  private Button startBty;
  private Button stopBty;
  private Button playBty;
  private Button finishBty;
  private File audioFile = new File("/sdcard/voice16K16bitmono.raw");
  MediaPlayer mPlayer;


  // audioRecoder parameter
  private static final int RECORDER_SAMPLERATE = 16000;
  private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
  private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  private RecordTask recorderTask = null;
  private boolean isRecording = false;


  private ByteArrayOutputStream dos;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.voice_layout);
    startBty = (Button) findViewById(R.id.startBty);
    stopBty = (Button) findViewById(R.id.stopBty);
    playBty = (Button) findViewById(R.id.playBty);
    finishBty = (Button) findViewById(R.id.finishBty);
    // try {
    // InputStream is = getAssets().open("weather.raw");
    // fileBytes = new byte[is.available()];
    // is.read(fileBytes);
    // } catch (FileNotFoundException e1) {
    // e1.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }



  }


  public void mediaPlay() {
    MediaPlayer mPlayer = MediaPlayer.create(VoiceClientActivity.this,
        Uri.parse("data/data/com.example.plasma.alexa/joke"));
    mPlayer.start();
  }

  public void mediaStop() {
    mPlayer.stop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mediaStop();
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
        Log.v("The DOS available:", "::" + audioFile.length());



      } catch (Exception e) {
        // TODO: handle exception
      }
      return null;
    }

    // 当在上面方法中调用publishProgress时，该方法触发,该方法在UI线程中被执行
    protected void onProgressUpdate(Integer... progress) {
      // stateView.setText(progress[0].toString());
    }

    protected void onPostExecute(Void result) {
      stopBty.setEnabled(false);
      startBty.setEnabled(true);
      // btnPlay.setEnabled(true);
      // btnFinish.setEnabled(false);
    }

    protected void onPreExecute() {
      // stateView.setText("正在录制");
      startBty.setEnabled(false);
      // btnPlay.setEnabled(false);
      // btnFinish.setEnabled(false);
      stopBty.setEnabled(true);
    }

  }


  public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.startBty:
        // 开始录制
        Log.i("plasma018", "plasma018: startBty");
        // 这里启动录制任务
        recorderTask = new RecordTask();
        recorderTask.execute();

        break;
      case R.id.stopBty:
        // 停止录制
        this.isRecording = false;
        // 更新状态
        // 在录制完成时设置，在RecordTask的onPostExecute中完成
        Log.i("plasma018", "plasma018: stopBty");
        break;
      case R.id.playBty:
        Log.i("plasma018", "plasma018: sendMessage()");
        sendMessage();
        startBty.setEnabled(false);
        playBty.setEnabled(false);
        finishBty.setEnabled(true);
        stopBty.setEnabled(false);
        // player = new PlayTask();
        // player.execute();
        break;
      case R.id.finishBty:
        startBty.setEnabled(true);
        playBty.setEnabled(true);
        finishBty.setEnabled(true);
        stopBty.setEnabled(true);
        // 完成播放
        // this.isPlaying = false;
        break;

    }
  }



  // ================ sendMessage() ===================================>

  private void sendMessage() {
    new Thread() {
      @Override
      public void run() {
        try {

          // ===================================================>



          token = getIntent().getStringExtra("token");
          OkHttpClient.Builder clientBuilder =
              new OkHttpClient.Builder().followRedirects(true).followSslRedirects(true)
                  .retryOnConnectionFailure(true).cache(null).connectTimeout(0, TimeUnit.SECONDS)
                  .writeTimeout(0, TimeUnit.SECONDS).readTimeout(0, TimeUnit.SECONDS);

          OkHttpClient downChannelClient = enableTls12OnPreLollipop(clientBuilder).build();

          Request request = new Request.Builder().get()
              .url("https://avs-alexa-na.amazon.com/v20160207/directives")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
          Response response = downChannelClient.newCall(request).execute();
          Log.i("plasma018", "plasma018 code:" + response.code());
          Log.i("plasma018", "plasma018 headers: " + response.headers());



          MultipartBody mBody = null;
          Request.Builder request2 =
              new Request.Builder().url("https://avs-alexa-na.amazon.com/v20160207/events")
                  .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
                  .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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

          client.newCall(requestAudio).enqueue(new Callback() {
            private OutputStream outs;
            private int numRead;

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
              Log.i("plasma018", "plasma018 responseAudio code:" + arg1.code());
              String boundary = arg1.headers().get("content-type").split(";")[1].substring(9);
              Log.i("plasma018", "plasma018 responseAudio boundary: " + boundary);
              ResponseParser.parseResponse(arg1.body().byteStream(), boundary);
              mediaPlay();
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
              Log.i("plasma018", "plasma018 responseAudio onFailure: " + arg1.getMessage());
            }
          });
        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      };
    }.start();
  }


  public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
    if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
      try {
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, null, null);
        client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

        ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2).build();

        List<ConnectionSpec> specs = new ArrayList<>();
        specs.add(cs);
        specs.add(ConnectionSpec.COMPATIBLE_TLS);
        specs.add(ConnectionSpec.CLEARTEXT);

        client.connectionSpecs(specs);
      } catch (Exception exc) {
        Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
      }
    }

    return client;
  }

  private OkHttpClient getNewHttpClient() {
    OkHttpClient.Builder client =
        new OkHttpClient.Builder().followRedirects(true).followSslRedirects(true)
            .retryOnConnectionFailure(true).cache(null).connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS);

    return enableTls12OnPreLollipop(client).build();
  }

}


