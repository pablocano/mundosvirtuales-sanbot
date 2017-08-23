package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends TopBaseActivity {

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

    }

    String stringSpeechRecognition = "";

    Thread thRobot;

    SpeechManager speechManager;
    HardWareManager hardWareManager;
    HeadMotionManager headMotionManager;
    HandMotionManager handMotionManager;
    WheelMotionManager wheelMotionManager;
    SystemManager systemManager;
    ModularMotionManager modularMotionManager;

    MediaManager mediaManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        modularMotionManager = (ModularMotionManager) getUnitManager(FuncConstant.MODULARMOTION_MANAGER);

        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);

        ButterKnife.bind(this);

        initListener();

        thRobot = new Thread(new TaskRobot());
    }

    @Override
    protected void onMainServiceConnected() {
        thRobot.start();
    }


    class TaskRobot implements Runnable {
        @Override
        public void run() {
            while(true) {
                try{
                    speechManager.doWakeUp();

                    if(!stringSpeechRecognition.equals("")) {
                        speechManager.startSpeak(stringSpeechRecognition);
                        hardWareManager.setLED(new LED(LED.PART_ALL,LED.MODE_FLICKER_RANDOM));
                        stringSpeechRecognition = "";
                    }
                    sleep(500);
                }catch(Exception e)
                {
                    return;
                }
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
                stringSpeechRecognition = grammar.getText();
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
            case R.id.action_loop:
                if(thRobot.isInterrupted()) {
                    thRobot.start();
                }
                else {
                    thRobot.interrupt();
                }
                break;
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
