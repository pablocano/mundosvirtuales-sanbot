package com.mundos_virtuales.sanbotmv.utils;


public interface IAudioRobot {

    void setProcessVoiceListener(IProcessVoice processVoice);

    void pauseRecording();

    void resumeRecording();

    void startRecording();

    void stopRecording();

    short getChannels();

    short getFormatWav();

    int getSampleRate();

    int getByteRate();

    int getSubChunkLSise();

    short getBlockAlign();

    short getBitPerSample();

    int getScaleUPSample();

    float getEnergyMean();
}
