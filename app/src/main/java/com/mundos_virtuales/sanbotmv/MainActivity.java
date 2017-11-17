package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.mundos_virtuales.sanbotmv.utils.AudioSanbot;
import com.mundos_virtuales.sanbotmv.utils.AudioAndroid;
import com.mundos_virtuales.sanbotmv.utils.IAudioRobot;
import com.mundos_virtuales.sanbotmv.utils.ProcessBufferVoice;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.StreamOption;
import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.ModularMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.VoiceLocateListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;

import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends TopBaseActivity {

    Boolean bVoiceRec = false;
    boolean isSpeech = false;
    Thread thProcessVoice;
    MediaManager mediaManager;

    SpeechManager speechManager;
    HardWareManager hardWareManager;
    HeadMotionManager headMotionManager;
    HandMotionManager handMotionManager;
    WheelMotionManager wheelMotionManager;
    SystemManager systemManager;
    ModularMotionManager modularMotionManager;

    final ConcurrentLinkedQueue<String> queueVoice = new ConcurrentLinkedQueue<String>();
    ProcessBufferVoice processBufferVoice = new ProcessBufferVoice(queueVoice);
    AudioSanbot audioSanbot;
    AudioAndroid audioAndroid;
    IAudioRobot audioRobot;

    @Bind(R.id.tvVoice)
    TextView tvVoice;

    @OnClick(R.id.ivLogoMV)
    public void onExit(){
        this.finish();
    }

    @OnClick(R.id.btnVoice)
    public void onVoice(){
        bVoiceRec = !bVoiceRec;
    }

    @OnClick(R.id.btnNumAttention)
    public void onNumAttention() {
        Intent intent = new Intent(this, EnterUserActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnMap)
    public void onMap() {
        Intent intent = new Intent(this, ActivityListArticles.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnEvalAttention)
    public void onEvalAttention() {
        Intent intent = new Intent(this, EvaluationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, MainActivity.class.getName());

        ButterKnife.bind(this);

        speechManager        = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        hardWareManager      = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        headMotionManager    = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        wheelMotionManager   = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        handMotionManager    = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        systemManager        = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        modularMotionManager = (ModularMotionManager) getUnitManager(FuncConstant.MODULARMOTION_MANAGER);

        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);

        audioSanbot = new AudioSanbot(mediaManager);
        audioAndroid = new AudioAndroid();

        audioRobot = audioAndroid;

        thProcessVoice = new Thread(new ThreadProcessVoice());
        thProcessVoice.setPriority(Thread.MAX_PRIORITY);

        StreamOption streamOption = new StreamOption();
        streamOption.setChannel(StreamOption.MAIN_STREAM);
        streamOption.setDecodType(StreamOption.HARDWARE_DECODE);
        streamOption.setJustIframe(false);
        mediaManager.openStream(streamOption);

        initListener();
    }

    @Override
    protected void onMainServiceConnected() {
        speechManager.doSleep();
        speechManager.doWakeUp();
        thProcessVoice.start();
        audioRobot.startRecording();
    }

    private class ThreadProcessVoice implements Runnable {

        @Override
        public void run() {
            while(true) {
                try {
                    synchronized (queueVoice){
                        while(queueVoice.isEmpty()) {
                            queueVoice.wait();
                        }

                        String s = queueVoice.remove();

                        audioRobot.pauseRecording();
                        isSpeech = true;
                        speechManager.startSpeak(s);
                    }
                }catch(Exception e) {
                    queueVoice.clear();
                }
            }
        }
    }

    private void initListener() {

        hardWareManager.setOnHareWareListener(new VoiceLocateListener() {

            private int lastAngle = 0;
            private final int MIN_ANGLE = 10;
            private final int MAX_ANGLE = 180;

            @Override
            public void voiceLocateResult(int i) {
                if (!isSpeech) {
                    int angle = (i > 180 ? 360 - i : i);
                    byte actionMove = i > 180 ? RelativeAngleHeadMotion.ACTION_LEFT : RelativeAngleHeadMotion.ACTION_RIGHT;

                    int diff_angle = Math.abs(angle - lastAngle);

                    if (diff_angle < MAX_ANGLE && diff_angle > MIN_ANGLE) {
                        RelativeAngleHeadMotion relativeAngleMotion = new RelativeAngleHeadMotion(actionMove, angle);
                        headMotionManager.doRelativeAngleMotion(relativeAngleMotion);
                        lastAngle = angle;
                    }
                }
            }
        });

        audioRobot.setProcessVoiceListener(processBufferVoice);

        speechManager.setOnSpeechListener(new SpeakListener() {
            @Override
            public void onSpeakFinish() {
                audioRobot.resumeRecording();
                isSpeech = false;
            }

            @Override
            public void onSpeakProgress(int i) {

            }
        });
    }

    @Override
    protected void finalize() throws Throwable {
        mediaManager.closeStream();
        audioRobot.stopRecording();
        super.finalize();
    }
}
