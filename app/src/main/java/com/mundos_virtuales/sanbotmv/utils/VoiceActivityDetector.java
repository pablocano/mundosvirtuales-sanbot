package com.mundos_virtuales.sanbotmv.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;


public class VoiceActivityDetector {

    private static final int SEQUENCE_LENGTH_MILLIS = 100;
    private static final int MIN_SPEECH_SEQUENCE_COUNT = 3;
    private static final long MIN_SILENCE_MILLIS = 800;
    private static final long MAX_SILENCE_MILLIS = 3500;
    private static final long SILENCE_DIFF_MILLIS = MAX_SILENCE_MILLIS - MIN_SILENCE_MILLIS;
    private static final int NOISE_FRAMES = 15;
    private static final double ENERGY_FACTOR = 1.5;
    private static final int MIN_CZ = 5;
    private static final int MAX_CZ = 300;

    private SpeechEventsListener eventsListener;
    private double noiseEnergy = 0.0;
    private long lastActiveTime = -1;
    private long lastSequenceTime = 0;
    private int sequenceCounter = 0;
    private long time = 0;
    private int frameNumber;
    private long silenceMillis = MAX_SILENCE_MILLIS;
    private boolean speechActive = false;
    private double sum = 0;
    private int size = 0;

    private boolean isSpeechBegin = false;
    private boolean isSpeechEnd = false;

    public boolean getSpeechBegin() {
        return isSpeechBegin;
    }

    public boolean getSpeechEnd() {
        return isSpeechEnd;
    }

    public void processBuffer(final byte[] buffer, IAudioRobot audioRobot) {

        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, buffer.length).order(ByteOrder.LITTLE_ENDIAN);
        final ShortBuffer shorts = byteBuffer.asShortBuffer();

        final boolean active = isFrameActive(shorts);

        final int frameSize = buffer.length / 2; // 16 bit encoding
        time = frameNumber * frameSize * 1000 / audioRobot.getSampleRate();

        if (active) {
            if (lastActiveTime >= 0 && (time - lastActiveTime) < SEQUENCE_LENGTH_MILLIS) {
                if (++sequenceCounter >= MIN_SPEECH_SEQUENCE_COUNT) {
                    if (!speechActive) {
                        onSpeechBegin();
                    }

                    lastSequenceTime = time;
                    silenceMillis = Math.max(MIN_SILENCE_MILLIS, silenceMillis - SILENCE_DIFF_MILLIS / 4);
                }
            } else {
                sequenceCounter = 1;
            }
            lastActiveTime = time;
        } else {
            if (time - lastSequenceTime > silenceMillis) {
                if (speechActive) {
                    onSpeechEnd();
                } else {
                    onSpeechCancel();
                }
            }
        }
    }

    private boolean isFrameActive(final ShortBuffer frame) {

        int lastSign = 0;
        int czCount = 0;
        double energy = 0.0;

        final int frameSize = frame.limit();
        size += frameSize;

        for (int i = 0; i < frameSize; i++) {
            final short raw = frame.get(i);
            final double amplitude = (double) raw / (double) Short.MAX_VALUE;
            energy += (float) amplitude * (float) amplitude / (double) frameSize;

            sum += raw * raw;

            final int sign = (float) amplitude > 0 ? 1 : -1;
            if (lastSign != 0 && sign != lastSign) {
                czCount++;
            }
            lastSign = sign;
        }

        boolean result = false;
        if (++frameNumber < NOISE_FRAMES) {
            noiseEnergy += (energy / (double) NOISE_FRAMES);
        } else {
            if (czCount >= MIN_CZ && czCount <= MAX_CZ) {
                if (energy > noiseEnergy * ENERGY_FACTOR) {
                    result = true;
                }
            }
        }

        return result;
    }

    public void reset() {
        time = 0;
        frameNumber = 0;

        noiseEnergy = 0.0;
        lastActiveTime = -1;
        lastSequenceTime = 0;
        sequenceCounter = 0;
        silenceMillis = MAX_SILENCE_MILLIS;

        speechActive = false;

        isSpeechBegin = false;
        isSpeechEnd = false;
    }

    public void setSpeechListener(final SpeechEventsListener eventsListener) {
        this.eventsListener = eventsListener;
    }

    private void onSpeechEnd() {
        speechActive = false;
        isSpeechEnd = true;

        if (eventsListener != null) {
            eventsListener.onSpeechEnd();
        }
    }

    private void onSpeechCancel() {
        speechActive = false;
        isSpeechEnd = false;
        isSpeechBegin = false;

        if (eventsListener != null) {
            eventsListener.onSpeechCancel();
        }
    }

    private void onSpeechBegin() {
        speechActive = true;
        isSpeechBegin = true;

        if (eventsListener != null) {
            eventsListener.onSpeechBegin();
        }
    }

    /**
     * Used to notify about speech begin/end events
     */
    public interface SpeechEventsListener {
        void onSpeechBegin();

        void onSpeechCancel();

        void onSpeechEnd();
    }
}
