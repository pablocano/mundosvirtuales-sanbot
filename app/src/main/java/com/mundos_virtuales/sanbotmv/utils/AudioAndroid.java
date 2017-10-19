package com.mundos_virtuales.sanbotmv.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.concurrent.atomic.AtomicBoolean;


public class AudioAndroid implements IAudioRobot {

    private int sub_chunk_lsize = 16;
    private int bits_per_sample = 16;
    private int channels        = 2;
    private int sample_rate     = 22050; // 8000, 11025, 22050, 44100
    private float energyMean    = 1.5e6f;

    private AudioRecord recorder = null;
    private boolean isRecording = false;
    private int bufferSize = AudioRecord.getMinBufferSize(2 * getSampleRate(), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private Thread recordingThread = null;

    private IProcessVoice processVoice;

    private final AtomicBoolean pause = new AtomicBoolean(false);

    @Override
    public void setProcessVoiceListener(IProcessVoice processVoice) {
        this.processVoice = processVoice;
    }

    private void writeAudioData() {

        byte data[] = new byte[bufferSize];

        while (isRecording) {
            try {
                synchronized (pause) {
                    while (pause.get()) {
                        pause.wait();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            recorder.read(data, 0, data.length);
            processVoice.processVoice(data, this);
        }
    }

    @Override
    public void pauseRecording() {
        synchronized (pause){
            pause.set(true);
            pause.notifyAll();
        }
    }

    @Override
    public void resumeRecording() {
        synchronized (pause){
            pause.set(false);
            pause.notifyAll();
        }
    }

    @Override
    public void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 2 * getSampleRate(), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        try {
            recorder.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }

        isRecording = true;

        recordingThread = new Thread(new Runnable()
        {
            public void run() {
                writeAudioData();
            }

        });
        recordingThread.start();
    }

    @Override
    public void stopRecording() {
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
        recordingThread = null;
    }

    @Override
    public short getChannels() {
        return (short) channels;
    }

    @Override
    public short getFormatWav() {
        return 1; // 1 - PCM
    }

    @Override
    public int getSampleRate() {
        return sample_rate;
    }

    @Override
    public int getByteRate() {
        return getSampleRate() * getChannels() * getBitPerSample() / 8;
    }

    @Override
    public int getSubChunkLSise() {
        return sub_chunk_lsize;
    }

    @Override
    public short getBlockAlign() {
        return (short) (getChannels() * getBitPerSample() / 8);
    }

    @Override
    public short getBitPerSample() {
        return (short) bits_per_sample;
    }

    @Override
    public int getScaleUPSample() {
        return 1;
    }

    @Override
    public float getEnergyMean() {
        return energyMean;
    }
}
