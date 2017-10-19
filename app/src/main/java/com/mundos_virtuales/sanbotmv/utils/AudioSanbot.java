package com.mundos_virtuales.sanbotmv.utils;

import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.interfaces.media.MediaStreamListener;


public class AudioSanbot implements IAudioRobot {

    int sub_chunk_lsize = 16;
    int bits_per_sample = 16;
    int channels        = 2;
    int sample_rate     = 4000;
    int scale_upsample  = 2;
    float energyMean    = 2.6e7f;

    MediaManager mediaManager;

    public AudioSanbot(MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    @Override
    public void setProcessVoiceListener(final IProcessVoice processVoice) {
        final IAudioRobot audioRobot = this;
        mediaManager.setMediaListener(new MediaStreamListener() {
            @Override
            public void getVideoStream(byte[] bytes) {

            }

            @Override
            public void getAudioStream(byte[] bytes) {
                processVoice.processVoice(bytes, audioRobot);
            }
        });
    }

    @Override
    public void pauseRecording() {

    }

    @Override
    public void resumeRecording() {

    }

    @Override
    public void startRecording() {

    }

    @Override
    public void stopRecording() {

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
        return scale_upsample;
    }

    @Override
    public float getEnergyMean() {
        return energyMean;
    }
}
