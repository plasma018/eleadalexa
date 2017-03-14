package com.example.plasma.alexa;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.fileupload.MultipartStream;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazon.alexa.avs.http.HttpHeaders;
import com.amazon.alexa.speechsynthesizer.SpeakDirective;
import com.example.alexa.lib.ResponseParser;
import com.example.alexa.lib.ResponseParser.DirectiveName;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;


@SuppressLint("Instantiatable")
public class PlasmaService extends Service {
  private static final String TAG = "PlasmaService";
  private Context mApplicationContext;
  private MainActivity mainActivity;
  private OkHttpClient AVSClient;


  private String loginToken;

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

  private static class DirectiveName {
    public static final String StopCapture = "StopCapture";
    public static final String ExpectSpeech = "ExpectSpeech";
    public static final String Speak = "Speak";
    public static final String SetVolume = "SetVolume";
    public static final String AdjustVolume = "AdjustVolume";
    public static final String SetMute = "SetMute";
  }


  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, TAG + " onCreate");
    init();

    int bufferSize = 2408;
    InputStream fin;
    try {
      fin = getAssets().open("timer.raw");
      dos = new ByteArrayOutputStream();
      Log.i(TAG, TAG + "fin byte: " + fin.available());
      byte[] buffer = new byte[1024];
      int len = fin.read(buffer);
      while (len != -1) {
        dos.write(buffer, 0, len);
        len = fin.read(buffer);
        Log.i(TAG, TAG + "READ BYTE: " + len);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
    // recorderTask = new RecordTask();
    // recorderTask.execute();
  }

  public void StopRecordVoice() {
    this.isRecording = false;
    sendMultiMessage();
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

    protected void onProgressUpdate(Integer... progress) {}

    protected void onPostExecute(Void result) {

    }

    protected void onPreExecute() {}

  }

  //
  private void startService() {
    new Thread() {
      @Override
      public void run() {
        OkHttpClient.Builder clientBuilder =
            new OkHttpClient.Builder().connectTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS).writeTimeout(0, TimeUnit.SECONDS);

        AVSClient = clientBuilder.build();
        Request downChannelRequest = new Request.Builder().get().url(HttpHeaders.DIRECT_URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken).build();

        AVSClient.newCall(downChannelRequest).enqueue(new Callback() {
          @Override
          public void onResponse(Call arg0, Response arg1) throws IOException {
            Log.i(TAG, "start downChannel  code:" + arg1.code());
            Log.i(TAG, "start downChannel headers: " + arg1.headers());
            Log.i(TAG, "Call " + arg0.toString());
            if (arg1.code() == 200) {
              MultipartBody mBody = null;

              mBody = new MultipartBody.Builder("__BOUNDARY__").setType(MultipartBody.FORM)
                  .addFormDataPart("name", "metadata",
                      okhttp3.RequestBody.create(MediaType.parse(HttpHeaders.MEDIATYPE_JSON),
                          JSONTest.synchronizeStateEvent()))
                  .build();

              Request.Builder syncRequestBuilder = new Request.Builder().url(HttpHeaders.EVENT_URL)
                  .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
                  .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=__BOUNDARY__")
                  .method("POST", mBody);

              Request syncRequest = syncRequestBuilder.build();
              Log.i(TAG, "syncRequest: " + syncRequest.toString());
              Response syncResponse = AVSClient.newCall(syncRequest).execute();
              Log.i(TAG, "syncResponse code:" + syncResponse.code());
              Log.i(TAG, "syncResponse headers: " + syncResponse.headers());
              Log.i(TAG, "syncResponse body: " + syncResponse.body().string());
              syncResponse.close();

              BufferedSource bufferedSource = arg1.body().source();
              Buffer buffer = new Buffer();

              while (!bufferedSource.exhausted()) {
                Log.w(TAG, "downchannel received data!!!");
                bufferedSource.read(buffer, 8192);
                Log.d(TAG, "Size of data read: " + buffer.size());
              }
              Log.i(TAG, "bufferedSource.exhausted(): " + bufferedSource.exhausted());
            }
          }

          @Override
          public void onFailure(Call arg0, IOException arg1) {


          }
        });
      }
    }.start();
  }


  private void sendRequest() {
    new Thread() {
      @Override
      public void run() {
        MultipartBody mBody = null;

        mBody = new MultipartBody.Builder("__BOUNDARY__").setType(MultipartBody.FORM)
            .addFormDataPart("name", "metadata", okhttp3.RequestBody.create(
                MediaType.parse(HttpHeaders.MEDIATYPE_JSON), JSONTest.synchronizeStateEvent()))
            .build();


        Request.Builder syncRequestBuilder = new Request.Builder().url(HttpHeaders.EVENT_URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
            .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=__BOUNDARY__")
            .method("POST", mBody);
        Request syncRequest = syncRequestBuilder.build();
        Log.i(TAG, "syncRequest: " + syncRequest.toString());



        try {
          Response syncResponse = AVSClient.newCall(syncRequest).execute();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }



      }
    }.start();
  }


  private void sendMultiMessage() {

    new Thread() {
      @Override
      public void run() {
        // 測試寫法，希望達成錄音的執行緒結束之後再開始傳送request
        // try {
        // recorderTask.get();
        // } catch (InterruptedException | ExecutionException e) {
        // e.printStackTrace();
        // }
        // 測試寫法，希望達成錄音的執行緒結束之後再開始傳送request
        MultipartBody mBody_audio = null;
        mBody_audio = new MultipartBody.Builder("__BOUNDARY__").setType(MultipartBody.FORM)
            .addFormDataPart("name", "metadata",
                okhttp3.RequestBody.create(MediaType.parse("application/json; charset=UTF-8"),
                    JSONTest.recognizeEvent()))
            .addFormDataPart("name", "audio", okhttp3.RequestBody
                .create(MediaType.parse(HttpHeaders.MEDIATYPE_AUDIO), dos.toByteArray()))
            .build();

        Request.Builder request_audio = new Request.Builder().url(HttpHeaders.EVENT_URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
            .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=__BOUNDARY__")
            .method("POST", mBody_audio);

        Request requestAudio = request_audio.build();

        AVSClient.newCall(requestAudio).enqueue(new Callback() {
          @Override
          public void onResponse(Call arg0, Response arg1) throws IOException {
            Log.i("plasma018", "plasma018 responseAudio code:" + arg1.code());
            mainActivity.setStatusGone();
            if (arg1.code() == 200) {
              // 測試寫法，要取的boundary的值，用來解開Multipart
              String boundary =
                  arg1.headers().get(HttpHeaders.CONTENT_TYPE).split(";")[1].substring(9);
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
      };
    }.start();
  }



  public void mediaPlay() {
    mPlayer = MediaPlayer.create(this, Uri.parse("/sdcard/voice.mp3"));
    mPlayer.start();
  }

  private void parseResponse(InputStream stream, String boundary)
      throws IOException, IllegalStateException {
    File file = new File("/sdcard/voice.mp3");

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
          getDirective(directive);
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

  private boolean isJson(String headers) {
    if (headers.contains("application/json")) {
      return true;
    }
    return false;
  }

  private void getDirective(String directive) throws JSONException {
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
        Log.i(TAG, "item token: " + speakerDirective.getPayload().getToken());
        break;
      case DirectiveName.ExpectSpeech:
        break;
      default:
        break;
    }
  }


}
