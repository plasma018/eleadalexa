package com.example.plasma.alexa;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.fileupload.MultipartStream;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazon.alexa.avs.http.HttpHeaders;
import com.amazon.alexa.speechrecongizer.ExpectSpeechDirective;
import com.amazon.alexa.speechrecongizer.ExpectSpeechTimedOutEvent;
import com.amazon.alexa.speechsynthesizer.SpeakDirective;
import com.amazon.alexa.speechsynthesizer.SpeechFinishedEvent;
import com.amazon.alexa.speechsynthesizer.SpeechStartedEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
  private static final String TAG = "plasmaService";
  private Context mApplicationContext;
  private MainActivity mainActivity;
  private OkHttpClient AVSClient = new OkHttpClient.Builder().connectTimeout(0, TimeUnit.SECONDS)
      .readTimeout(0, TimeUnit.SECONDS).writeTimeout(0, TimeUnit.SECONDS).build();
  private Request downChannelRequest;
  private String loginToken;
  // 錄音程式的資訊
  private static final int RECORDER_SAMPLERATE = 16000;
  private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
  private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  private RecordTask recorderTask = null;
  private boolean isRecording = false;
  private MediaPlayer mPlayer;
  // 播放聲音的資訊
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

  private final int SPEAK = 1;
  private Handler DirectiveHandler;
  private Map<String, InputStream> AudioStream = new HashMap();



  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, TAG + " onCreate");
    init();

    // int bufferSize = 2408;
    // InputStream fin;
    // try {
    // fin = getAssets().open("timer.raw");
    // dos = new ByteArrayOutputStream();
    // Log.i(TAG, TAG + "fin byte: " + fin.available());
    // byte[] buffer = new byte[1024];
    // int len = fin.read(buffer);
    // while (len != -1) {
    // dos.write(buffer, 0, len);
    // len = fin.read(buffer);
    // Log.i(TAG, TAG + "READ BYTE: " + len);
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
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
        setPingTimer();
        Log.i(TAG, TAG + "startService finish!!!!");
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
    new Thread() {
      public void run() {
        Looper.prepare();
        DirectiveHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {}
        };
        Looper.loop();
      }
    }.start();
  }

  public void RecordVoice() {
    recorderTask = new RecordTask();
    recorderTask.execute();
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

        downChannelRequest = new Request.Builder().get().url(HttpHeaders.DIRECT_URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken).build();

        try {
          final Response response = AVSClient.newCall(downChannelRequest).execute();

          Log.i(TAG, "start downChannel  code:" + response.code());
          Log.i(TAG, "start downChannel headers: " + response.headers());

          new Thread() {
            @Override
            public void run() {

              BufferedSource bufferedSource = response.body().source();
              Buffer buffer = new Buffer();

              try {
                while (!bufferedSource.exhausted()) {
                  Log.w(TAG, "downchannel received data!!!");
                  bufferedSource.read(buffer, 8192);
                  Log.d(TAG, "Size of data read: " + buffer.size());
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }.start();

          if (response.code() == 200) {
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
            Response syncResponse = AVSClient.newCall(syncRequest).execute();
            Log.i(TAG, "syncResponse code:" + syncResponse.code());
            Log.i(TAG, "syncResponse headers: " + syncResponse.headers());
            Log.i(TAG, "syncResponse body: " + syncResponse.body().string());
            syncResponse.close();

          }
        } catch (IOException e) {
          e.printStackTrace();
        }


        // AVSClient.newCall(downChannelRequest).enqueue(new Callback() {
        // @Override
        // public void onResponse(Call arg0, Response arg1) throws IOException {
        // Log.i(TAG, "start downChannel code:" + arg1.code());
        // Log.i(TAG, "start downChannel headers: " + arg1.headers());
        // Log.i(TAG, "Call " + arg0.toString());
        // if (arg1.code() == 200) {
        // MultipartBody mBody = null;
        //
        // mBody = new MultipartBody.Builder("__BOUNDARY__").setType(MultipartBody.FORM)
        // .addFormDataPart("name", "metadata",
        // okhttp3.RequestBody.create(MediaType.parse(HttpHeaders.MEDIATYPE_JSON),
        // JSONTest.synchronizeStateEvent()))
        // .build();
        //
        // Request.Builder syncRequestBuilder = new Request.Builder().url(HttpHeaders.EVENT_URL)
        // .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
        // .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=__BOUNDARY__")
        // .method("POST", mBody);
        //
        // Request syncRequest = syncRequestBuilder.build();
        // Log.i(TAG, "syncRequest: " + syncRequest.toString());
        // Response syncResponse = AVSClient.newCall(syncRequest).execute();
        // Log.i(TAG, "syncResponse code:" + syncResponse.code());
        // Log.i(TAG, "syncResponse headers: " + syncResponse.headers());
        // Log.i(TAG, "syncResponse body: " + syncResponse.body().string());
        // syncResponse.close();
        //
        // BufferedSource bufferedSource = arg1.body().source();
        // Buffer buffer = new Buffer();
        //
        // while (!bufferedSource.exhausted()) {
        // Log.w(TAG, "downchannel received data!!!");
        // bufferedSource.read(buffer, 8192);
        // Log.d(TAG, "Size of data read: " + buffer.size());
        // }
        // Log.i(TAG, "bufferedSource.exhausted(): " + bufferedSource.exhausted());
        // }
        // }
        //
        // @Override
        // public void onFailure(Call arg0, IOException arg1) {
        //
        //
        // }
        // });
      }
    }.start();
  }

  private void setPingTimer() {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Request pingRequest = new Request.Builder().get().url(HttpHeaders.PING_URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken).build();
        try {
          Response pingResponse = AVSClient.newCall(pingRequest).execute();
          Log.i(TAG, TAG + " pingResponse code: " + pingResponse.code());
          Log.i(TAG, TAG + " pingResponse headers: " + pingResponse.headers());
          Log.i(TAG, TAG + " pingResponse body: " + pingResponse.message());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }, 2000, HttpHeaders.CONNECTION_PING_MILLISECINDS);
  }


  private void sendRequest(Event event) {

    GsonBuilder bulider = new GsonBuilder();
    bulider.serializeNulls();


    MultipartBody mBody = null;
    mBody = new MultipartBody.Builder("__BOUNDARY__").setType(MultipartBody.FORM)
        .addFormDataPart("name", "metadata", okhttp3.RequestBody
            .create(MediaType.parse(HttpHeaders.MEDIATYPE_JSON), bulider.create().toJson(event)))
        .build();

    Request.Builder syncRequestBuilder = new Request.Builder().url(HttpHeaders.EVENT_URL)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
        .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=__BOUNDARY__")
        .method("POST", mBody);
    Request EventRequest = syncRequestBuilder.build();
    Log.i(TAG, "EventRequest: " + EventRequest.body());


    try {
      Response EventResponse = AVSClient.newCall(EventRequest).execute();
      Log.i(TAG, TAG + "EventResponse code:" + EventResponse.code());
      Log.i(TAG, TAG + "EventResponse headers: " + EventResponse.headers());
      Log.i(TAG, TAG + "EventResponse body: " + EventResponse.body().string());
      EventResponse.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
            Log.i(TAG, "plasma018 responseAudio code:" + arg1.code());
            mainActivity.setStatusGone();
            if (arg1.code() == 200) {
              // 測試寫法，要取的boundary的值，用來解開Multipart
              String boundary =
                  arg1.headers().get(HttpHeaders.CONTENT_TYPE).split(";")[1].substring(9);
              Log.i(TAG, "plasma018 responseAudio boundary: " + boundary);
              parseVoiceResponse(arg1.body().byteStream(), boundary);

            }
          }

          @Override
          public void onFailure(Call arg0, IOException arg1) {
            Log.i(TAG, "plasma018 responseAudio onFailure: " + arg1.getMessage());
          }
        });
      };
    }.start();
  }


  public void mediaPlay() {
    mPlayer = MediaPlayer.create(this, Uri.parse("/sdcard/voice.mp3"));
    mPlayer.start();
  }

  
  private void parseVoiceResponse(InputStream stream, String boundary)
      throws IOException, IllegalStateException {
    File file = new File("/sdcard/voice.mp3");
    LinkedList<String> requestList = new LinkedList();
    // String directive = null;
    try {
      MultipartStream multipartStream =
          new MultipartStream(stream, boundary.getBytes(), 100000, null);
      boolean nextPart = multipartStream.skipPreamble();
      OutputStream output;

      while (nextPart) {
        String header = multipartStream.readHeaders();
        Log.i(TAG, "plasma018 header:" + header);
        if (!header.contains("application/json")) {
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
          Log.i(TAG, "directive TEST: " + directive);
          requestList.add(directive);
        }
        nextPart = multipartStream.readBoundary();
      }
      getDirective(requestList, null);
    } catch (MultipartStream.MalformedStreamException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void getDirective(List<String> requestList, final InputStream audioInput)
      throws JSONException {

    while (requestList.iterator().hasNext()) {
      String directive = requestList.iterator().next();

      if (directive == null) {
        Log.i(TAG, TAG + "Error !!!");
        return;
      }

      String directiveName = new JSONObject(directive).getJSONObject("directive")
          .getJSONObject("header").getString("name");
      Gson gson = new Gson();

      switch (directiveName) {
        case DirectiveName.Speak:
          final SpeakDirective speakerDirective =
              gson.fromJson(new JSONObject(directive).getString("directive"), SpeakDirective.class);
          Log.i(TAG, "item Namespace: " + speakerDirective.getHeader().getNamespace());
          Log.i(TAG, "item Name: " + speakerDirective.getHeader().getName());
          Log.i(TAG, "item MessageId: " + speakerDirective.getHeader().getMessageId());
          Log.i(TAG, "item DialogRequestId: " + speakerDirective.getHeader().getDialogRequestId());
          Log.i(TAG, "item url: " + speakerDirective.getPayload().getFormat());
          Log.i(TAG, "item token: " + speakerDirective.getPayload().getToken());


          Runnable runnable = new Runnable() {
            @Override
            public void run() {
              final Event event = new Event();
              SpeechStartedEvent speechStarted = new SpeechStartedEvent();
              speechStarted.setHeader("SpeechSynthesizer", "SpeechStarted");
              speechStarted.setPayload(speakerDirective.getPayload().getToken());
              event.setEvet(speechStarted);
              sendRequest(event);


              mediaPlay();

              SpeechFinishedEvent speechFinished = new SpeechFinishedEvent();
              speechFinished.setHeader("SpeechSynthesizer", "SpeechFinished");
              speechFinished.setPayload(speakerDirective.getPayload().getToken());
              sendRequest(event);
            }
          };

          // DirectiveHandler.removeCallbacks(runnable, speakerDirective.getPayload().getToken());
          DirectiveHandler.postAtTime(runnable, speakerDirective.getPayload().getToken(), 0);


          break;
        case DirectiveName.ExpectSpeech:

          final ExpectSpeechDirective expectSpeech = gson.fromJson(
              new JSONObject(directive).getString("directive"), ExpectSpeechDirective.class);
          Log.i(TAG, "expectSpeech Namespace: " + expectSpeech.getHeader().getNamespace());
          Log.i(TAG, "expectSpeech Name: " + expectSpeech.getHeader().getName());
          Log.i(TAG, "expectSpeech MessageId: " + expectSpeech.getHeader().getMessageId());
          Log.i(TAG,
              "expectSpeech DialogRequestId: " + expectSpeech.getHeader().getDialogRequestId());
          Log.i(TAG, "item timeoutInMilliseconds: "
              + expectSpeech.getPayLoad().getTimeoutIntervalInMillis());

          Runnable expectSpeechR = new Runnable() {
            @Override
            public void run() {
              final Event event = new Event();
              ExpectSpeechTimedOutEvent timeOutEvent = new ExpectSpeechTimedOutEvent();
              timeOutEvent.setHeader("SpeechRecognizer", "ExpectSpeechTimeOut");
              sendRequest(event);
            }
          };
          DirectiveHandler.postAtTime(expectSpeechR, expectSpeech.getHeader().getDialogRequestId(),
              expectSpeech.getPayLoad().getTimeoutIntervalInMillis());
          break;

        default:
          break;
      }
    }
  }


}
