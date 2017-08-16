package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.FaceRecognizeBean;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.handmotion.NoAngleHandMotion;
import com.qihancloud.opensdk.function.beans.handmotion.RelativeAngleHandMotion;
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

import java.util.HashMap;
import java.util.List;

public class MainActivity extends TopBaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    String stringSpeechRecognition = "";

    //private SliderLayout mDemoSlider;

    //Thread thRobot;

    ChatterBotSession bot1session;

    SpeechManager speechManager;
    //HardWareManager hardWareManager;
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

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        //hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        modularMotionManager = (ModularMotionManager) getUnitManager(FuncConstant.MODULARMOTION_MANAGER);

        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);

        initListener();

        try {
            ChatterBotFactory factory = new ChatterBotFactory();
            ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT);
            bot1session = bot1.createSession();
        }
        catch(Exception e) {
            speechManager.startSpeak("Bot Error");
        }

        //mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        /*HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Slide1",R.drawable.slide1);
        file_maps.put("Slide2",R.drawable.slide2);
        file_maps.put("Slide3",R.drawable.slide3);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);*/

        //thRobot = new Thread(new TaskRobot());

        //ListView l = (ListView)findViewById(R.id.transformers);
        //l.setAdapter(new TransformerAdapter(this));
        //l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        mDemoSlider.setPresetTransformer(((TextView) view).getText().toString());
        //        Toast.makeText(MainActivity.this, ((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
        //    }
        //});
    }

    @Override
    protected void onMainServiceConnected() {
        //thRobot.start();
    }


    class TaskRobot implements Runnable {
        @Override
        public void run() {
            while(true) {
                try{
                    speechManager.doWakeUp();

                    if(!stringSpeechRecognition.equals("")) {
                        speechManager.startSpeak(stringSpeechRecognition);
                        // String s = bot1session.think(stringSpeechRecognition);
                        // sleep(1000);
                        // speechManager.startSpeak(s);
                        //hardWareManager.setLED(new LED(LED.PART_ALL,LED.MODE_FLICKER_RANDOM));
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

    class TaskFace implements Runnable {
        @Override
        public void run() {
            while(true) {

                //hardWareManager.setLED(new LED(LED.PART_ALL,LED.MODE_FLICKER_RANDOM));

                handMotionManager.doRelativeAngleMotion(new RelativeAngleHandMotion(NoAngleHandMotion.PART_LEFT, 10, RelativeAngleHandMotion.ACTION_UP, 180));

                handMotionManager.doRelativeAngleMotion(new RelativeAngleHandMotion(NoAngleHandMotion.PART_RIGHT, 10, RelativeAngleHandMotion.ACTION_DOWN, 120));

                sleep(2000);

                //hardWareManager.setLED(new LED(LED.PART_ALL,LED.MODE_FLICKER_RANDOM));

                handMotionManager.doRelativeAngleMotion(new RelativeAngleHandMotion(NoAngleHandMotion.PART_LEFT, 10, RelativeAngleHandMotion.ACTION_DOWN, 100));

                handMotionManager.doRelativeAngleMotion(new RelativeAngleHandMotion(NoAngleHandMotion.PART_RIGHT, 10, RelativeAngleHandMotion.ACTION_UP, 120));

                sleep(2000);

                //hardWareManager.setLED(new LED(LED.PART_ALL,LED.MODE_FLICKER_RANDOM));

                handMotionManager.doRelativeAngleMotion(new RelativeAngleHandMotion(NoAngleHandMotion.PART_LEFT, 10, RelativeAngleHandMotion.ACTION_UP, 80));

                handMotionManager.doRelativeAngleMotion(new RelativeAngleHandMotion(NoAngleHandMotion.PART_RIGHT, 10, RelativeAngleHandMotion.ACTION_DOWN, 120));

                sleep(2000);

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

        /*hardWareManager.setOnHareWareListener(new InfrareListener() {
            @Override
            public void infrareDistance(int part, int distance) {

            }
        });*/

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
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        //mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
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
            /*case R.id.action_loop:
                if(thRobot.isInterrupted()) {
                    thRobot.start();
                }
                else {
                    thRobot.interrupt();
                }
                break;*/
            case R.id.action_sanbot:
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://en.sanbot.com/index.html"));
                startActivity(browserIntent1);
                break;
            case R.id.action_mv:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mundos-virtuales.com/"));
                startActivity(browserIntent2);
                break;
            case R.id.action_sv:
                Intent sensorActivity = new Intent(this, SensorsViewerActivity.class);
                startActivity(sensorActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
