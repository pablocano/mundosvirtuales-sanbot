package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.FaceRecognizeBean;
import com.qihancloud.opensdk.function.beans.StreamOption;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.interfaces.media.FaceRecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.media.MediaStreamListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoTestActivity extends TopBaseActivity implements SurfaceHolder.Callback {

    private final static String TAG = VideoTestActivity.class.getSimpleName();

    @Bind(R.id.image_view_test)
    ImageView mImageView;       // View for showing image from Sanbot camera.
    @Bind(R.id.info_video)
    TextView mTextViewInfo;     // View for showing information about Sanbot Video
    @Bind(R.id.video_test_back)
    Button mButtonBack;         // Button for returning to Main Activity
    @Bind(R.id.sv_media)
    SurfaceView svMedia;        // Surface View

    MediaManager mediaManager;  // Handler for Sanbot camera.
    Thread thTestVideo;         // Thread for testing video.
    MediaCodec mediaCodec;      // Media Codec

    long decodeTimeout = 16000; // Timeout decode
    MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
    ByteBuffer[] videoInputBuffers; // Buffer

    /**
     * Init all properties of activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_test);
        ButterKnife.bind(this);

        // Gets handler from Sanbot SDK
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);

        svMedia.getHolder().addCallback(this);

        // Define thread
        thTestVideo = new Thread(new TaskVideoTest());

        // Initialize listeners
        initListener();
    }

    /**
     * Initialize listeners
     */
    private void initListener() {
        mediaManager.setMediaListener(new MediaStreamListener() {
            @Override
            public void getVideoStream(byte[] bytes) {
                showViewData(ByteBuffer.wrap(bytes));
            }

            @Override
            public void getAudioStream(byte[] bytes) {
            }
        });
        mediaManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(List<FaceRecognizeBean> list) {
                StringBuilder sb = new StringBuilder();
                for (FaceRecognizeBean bean : list) {
                    sb.append(new Gson().toJson(bean));
                    sb.append("\n");
                }
                mTextViewInfo.setText("Salida: " + sb.toString());
            }
        });
    }

    /**
     * Main function for TopBaseActivity.
     */
    @Override
    protected void onMainServiceConnected() {
        thTestVideo.start(); // Starting thread for testing Sanbot video.
    }

    /**
     * Thread for doing test on Sanbot video.
     */
    class TaskVideoTest implements Runnable {

        /***
         * Loop thread.
         */
        @Override
        public void run() {
            while(true) {
                try{
                    Thread.sleep(3000);
                }catch(Exception e)
                {
                    return;
                }
            }
        }
    }

    /**
     * Surface Created.
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        StreamOption streamOption = new StreamOption();
        streamOption.setChannel(StreamOption.MAIN_STREAM);
        streamOption.setDecodType(StreamOption.HARDWARE_DECODE);
        streamOption.setJustIframe(false);
        mediaManager.openStream(streamOption);

        startDecoding(holder.getSurface());
    }

    /**
     * Surface Changed.
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * Surface Destroyed.
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mediaManager.closeStream();
        stopDecoding();
    }

    /***
     * Start decoding.
     * @param surface
     */
    private void startDecoding(Surface surface) {
        if (mediaCodec != null) {
            return;
        }
        try {
            mediaCodec = MediaCodec.createDecoderByType("video/avc");
            MediaFormat format = MediaFormat.createVideoFormat(
                    "video/avc", 1280, 720);
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();
            videoInputBuffers = mediaCodec.getInputBuffers();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Stop decoding.
     */
    private void stopDecoding() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
            Log.i(TAG, "stopDecoding");
        }
        videoInputBuffers = null;
    }

    private void showViewData(ByteBuffer sampleData) {
        try {
            int inIndex = mediaCodec.dequeueInputBuffer(decodeTimeout);
            if (inIndex >= 0) {
                ByteBuffer buffer = videoInputBuffers[inIndex];
                int sampleSize = sampleData.limit();
                buffer.clear();
                buffer.put(sampleData);
                buffer.flip();
                mediaCodec.queueInputBuffer(inIndex, 0, sampleSize, 0, 0);
            }
            int outputBufferId = mediaCodec.dequeueOutputBuffer(videoBufferInfo, decodeTimeout);
            if (outputBufferId >= 0) {
                mediaCodec.releaseOutputBuffer(outputBufferId, true);
            } else {
                Log.e(TAG, "dequeueOutputBuffer() error");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        }
    }

    @OnClick(R.id.video_test_capture)
    public void onViewClicked() {
        Bitmap bitmap = mediaManager.getVideoImage();
        if(bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.video_test_back)
    public void onBackClicked() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
