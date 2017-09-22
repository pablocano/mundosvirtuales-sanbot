package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.FaceRecognizeBean;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.ModularMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.InfrareListener;
import com.qihancloud.opensdk.function.unit.interfaces.media.FaceRecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.WakenListener;

import com.google.code.chatterbotapi.*;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends TopBaseActivity {

    Boolean bVoiceRec = false;

    ConcurrentLinkedQueue<String> queueVoice = new ConcurrentLinkedQueue<String>();

    Thread thProcessVoice;

    SpeechManager speechManager;
    HardWareManager hardWareManager;
    HeadMotionManager headMotionManager;
    HandMotionManager handMotionManager;
    WheelMotionManager wheelMotionManager;
    SystemManager systemManager;
    ModularMotionManager modularMotionManager;

    MediaManager mediaManager;

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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, MainActivity.class.getName());

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        modularMotionManager = (ModularMotionManager) getUnitManager(FuncConstant.MODULARMOTION_MANAGER);

        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);

        ButterKnife.bind(this);

        thProcessVoice = new Thread(new ProcessVoice());

        initListener();
    }

    @Override
    protected void onMainServiceConnected() {
        speechManager.doSleep();
        speechManager.doWakeUp();
        thProcessVoice.start();
    }


    class ProcessVoice implements Runnable {

        @Override
        public void run() {
            try{

                ChatterBotFactory factory = new ChatterBotFactory();

                ChatterBot bot1 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
                ChatterBotSession bot1session = bot1.createSession();

                // hardWareManager.setLED(new LED(LED.PART_ALL,LED.MODE_FLICKER_RANDOM));

                while(true) {
                    synchronized (queueVoice){
                        while(queueVoice.isEmpty()) {
                            queueVoice.wait();
                        }

                        String s1 = queueVoice.remove();
                        String s2 = bot1session.think(s1);
                        speechManager.startSpeak(s2);
                    }
                }
            }catch(Exception e)
            {
                queueVoice.clear();
            }
        }
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initListener() {

        speechManager.setOnSpeechListener(new WakenListener() {
            @Override
            public void onWakeUp() {

            }

            @Override
            public void onSleep() {

            }
        });

        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                synchronized (queueVoice) {
                    String s = grammar.getText();
                    tvVoice.setText(s);
                    queueVoice.add(s);
                    queueVoice.notify();
                }
                return true;
            }

            @Override
            public void onRecognizeVolume(int volume) {

            }
        });

        speechManager.setOnSpeechListener(new SpeakListener() {
            @Override
            public void onSpeakFinish() {

            }

            @Override
            public void onSpeakProgress(int i) {

            }
        });

        hardWareManager.setOnHareWareListener(new InfrareListener() {
            @Override
            public void infrareDistance(int part, int distance) {

            }
        });

        mediaManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(List<FaceRecognizeBean> faceRecognizeBean) {
                //ImageView image = (ImageView) findViewById(R.id.imageView);
                Bitmap bitmap = mediaManager.getVideoImage();
                if(bitmap != null){
                    //image.setImageBitmap(bitmap);
                }

                for(FaceRecognizeBean face : faceRecognizeBean) {
                    speechManager.startSpeak("hello" + face.getUser());
                }

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sanbot:
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://en.sanbot.com/index.html"));
                startActivity(browserIntent1);
                break;
            case R.id.action_mv:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mundos-virtuales.com/"));
                startActivity(browserIntent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
