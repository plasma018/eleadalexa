//
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.ByteBuffer;
//
//import javazoom.jl.decoder.Bitstream;
//import javazoom.jl.decoder.BitstreamException;
//import javazoom.jl.decoder.Decoder;
//import javazoom.jl.decoder.DecoderException;
//import javazoom.jl.decoder.Header;
//import javazoom.jl.decoder.SampleBuffer;
//import android.app.Activity;
//import android.media.AudioFormat;
//import android.media.AudioManager;
//import android.media.AudioTrack;
//import android.media.MediaCodec;
//import android.media.MediaCodec.BufferInfo;
//import android.media.MediaExtractor;
//import android.media.MediaFormat;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//
//public class MainActivity extends Activity {
//
//    public final String LOG_TAG = "mediadecoderexample";
//    MediaCodec codec;
//    MediaExtractor extractor; 
//    MediaFormat format;
//    ByteBuffer[] codecInputBuffers;
//    ByteBuffer[] codecOutputBuffers;
//    Boolean sawInputEOS = false;
//    Boolean sawOutputEOS = false;
//    AudioTrack mAudioTrack;
//    BufferInfo info;
//    String url = "http://82.201.100.9:8000/RADIO538_WEB_MP3";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        extractor = new MediaExtractor();
//
//        try {
//            extractor.setDataSource(url);
//        } catch (IOException e) {
//        }
//
//        format = extractor.getTrackFormat(0);
//        String mime = format.getString(MediaFormat.KEY_MIME);
//        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
//
//        Log.i(LOG_TAG, "===========================");
//        Log.i(LOG_TAG, "url "+url);
//        Log.i(LOG_TAG, "mime type : "+mime);
//        Log.i(LOG_TAG, "sample rate : "+sampleRate);
//        Log.i(LOG_TAG, "===========================");
//
//        codec = MediaCodec.createDecoderByType(mime);
//        codec.configure(format, null , null , 0);
//        codec.start();
//
//        codecInputBuffers = codec.getInputBuffers();
//        codecOutputBuffers = codec.getOutputBuffers();
//
//        extractor.selectTrack(0); 
//
//        mAudioTrack = new AudioTrack(
//                AudioManager.STREAM_MUSIC, 
//                sampleRate, 
//                AudioFormat.CHANNEL_OUT_STEREO, 
//                AudioFormat.ENCODING_PCM_16BIT, 
//                AudioTrack.getMinBufferSize (
//                        sampleRate, 
//                        AudioFormat.CHANNEL_OUT_STEREO, 
//                        AudioFormat.ENCODING_PCM_16BIT
//                        ), 
//                AudioTrack.MODE_STREAM
//                );
//         mAudioTrack.play();
//         new LongOperation().execute("");
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//     public short[] decode(byte[] mp3_data) throws IOException {
//
//            SampleBuffer output = null;
//            InputStream inputStream = new ByteArrayInputStream(mp3_data);
//            short[] pcmOut = {};
//            try {
//                Bitstream bitstream = new Bitstream(inputStream);
//                Decoder decoder = new Decoder();
//                boolean done = false;
//                int i = 0;
//                while (! done) {
//                    Header frameHeader = bitstream.readFrame();
//                    if (frameHeader == null) {
//                        done = true;
//                    } else {
//                        output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
//                        short[] next = output.getBuffer();
//                        pcmOut = concatArrays(pcmOut, next);
//                        mAudioTrack.write(pcmOut, 0, pcmOut.length);
//                    }
//
//                    bitstream.closeFrame();
//                    i++;
//                }
//                return pcmOut;
//
//            } catch (BitstreamException e) {
//                throw new IOException("Bitstream error: " + e);
//            } catch (DecoderException e) {
//                Log.w(LOG_TAG, "Decoder error", e);
//            }
//            return null;
//        }
//
//
//        short[] concatArrays(short[] A, short[] B) {
//
//            int aLen = A.length;
//            int bLen = B.length;
//            short[] C= new short[aLen+bLen];
//
//            System.arraycopy(A, 0, C, 0, aLen);
//            System.arraycopy(B, 0, C, aLen, bLen);
//
//            return C;
//        }
//
//    private class LongOperation extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//
//
//            URL u;
//            HttpURLConnection c;
//            try {
//                u = new URL(url);
//                c = (HttpURLConnection) u.openConnection();
//
//                c.setRequestMethod("GET");
//                c.setDoOutput(false);
//                c.connect();
//
//                InputStream in = c.getInputStream();
//
//
//                byte[] buffer = new byte[1024];
//                int len1 = 0;
//                while ( (len1 = in.read(buffer)) > 0 ) {
//                    Log.i(LOG_TAG, buffer.length+" bytes input"); 
//                    short[] pcmOut = MainActivity.this.decode(buffer);
//                    Log.i(LOG_TAG, pcmOut.length+" bytes ouput"); 
//                }
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//    }
//}
