package com.mundos_virtuales.sanbotmv.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ProcessBufferVoice implements IProcessVoice  {

    private VoiceActivityDetector VADetector;
    private ByteArrayOutputStream outDataWavStream = new ByteArrayOutputStream();
    private ConcurrentLinkedQueue<String> queueVoice;

    private final float MIN_TIME_VOICE = 1.0f; // Seconds

    public ProcessBufferVoice(ConcurrentLinkedQueue<String> queueVoice) {
        this.queueVoice = queueVoice;
        this.VADetector = new VoiceActivityDetector();

        this.VADetector.setSpeechListener(new VoiceActivityDetector.SpeechEventsListener() {
            @Override
            public void onSpeechBegin() {

            }

            @Override
            public void onSpeechCancel() {
                outDataWavStream.reset();
            }

            @Override
            public void onSpeechEnd() {

            }
        });
    }

    @Override
    public void processVoice(byte[] bytes, IAudioRobot audioRobot) {
        try {

            VADetector.processBuffer(bytes, audioRobot);

            outDataWavStream.write(bytes);

            if(VADetector.getSpeechBegin() && VADetector.getSpeechEnd()) {

                int time = (outDataWavStream.size() / 2) * 1000 / audioRobot.getSampleRate();

                if(time > MIN_TIME_VOICE) {
                    // resample
                    byte[] input = outDataWavStream.toByteArray();
                    byte[] output = getUPSampling(input, audioRobot);

                    ByteArrayOutputStream outFile = getBytesWAVFormatFile(output, audioRobot);

                    new Thread(new RequestWatson(outFile.toByteArray(), queueVoice)).start();
                }

                outDataWavStream.reset();

                VADetector.reset();

            }
        }catch (IOException e) {

        }
    }

    public ByteArrayOutputStream getBytesWAVFormatFile(byte[] output, IAudioRobot audioRobot) throws IOException {
        ByteArrayOutputStream outFile = new ByteArrayOutputStream();

        long myDataSize = output.length;
        long myChunk2Size = myDataSize;
        long myChunkSize = 36 + myChunk2Size;

        outFile.write("RIFF".getBytes(StandardCharsets.US_ASCII));     // 00 - RIFF
        outFile.write(Utils.intToByteArray((int)myChunkSize), 0, 4);   // 04 - how big is the rest of this file?
        outFile.write("WAVE".getBytes(StandardCharsets.US_ASCII));     // 08 - WAVE
        outFile.write("fmt ".getBytes(StandardCharsets.US_ASCII));     // 12 - fmt

        outFile.write(Utils.intToByteArray(audioRobot.getSubChunkLSise()), 0, 4);   // 16 - size of this chunk
        outFile.write(Utils.shortToByteArray(audioRobot.getFormatWav()), 0, 2);     // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
        outFile.write(Utils.shortToByteArray(audioRobot.getChannels()), 0, 2);      // 22 - mono or stereo? 1 or 2?  (or 5 or ???)
        outFile.write(Utils.intToByteArray(audioRobot.getSampleRate()), 0, 4);      // 24 - samples per second (numbers per second)
        outFile.write(Utils.intToByteArray(audioRobot.getByteRate()), 0, 4);        // 28 - bytes per second
        outFile.write(Utils.shortToByteArray(audioRobot.getBlockAlign()), 0, 2);    // 32 - # of bytes in one sample, for all channels
        outFile.write(Utils.shortToByteArray(audioRobot.getBitPerSample()), 0, 2);  // 34 - how many bits in a sample(number)?  usually 16 or 24

        outFile.write("data".getBytes(StandardCharsets.US_ASCII));     // 36 - data
        outFile.write(Utils.intToByteArray((int)myDataSize), 0, 4);    // 40 - how big is this data chunk
        outFile.write(output);                                         // 44 - the actual data itself - just a long string of numbers

        return outFile;
    }

    public byte[] getUPSampling(byte[] input, IAudioRobot audioRobot){
        if(audioRobot.getScaleUPSample() == 1) {
            return input;
        } else {
            byte[] output = new byte[input.length * audioRobot.getScaleUPSample()];
            final int jumpSample = (int) audioRobot.getChannels() * 2;
            for (int i = 0; i < input.length - jumpSample; i += jumpSample) {
                short sample1CH1 = (short) (((input[i + 1]) << 8) | (input[i + 0] & 0x00FF));
                short sample1CH2 = (short) (((input[i + 3]) << 8) | (input[i + 2] & 0x00FF));
                short sample2CH1 = (short) (((input[i + 1 + jumpSample]) << 8) | (input[i + 0 + jumpSample] & 0x00FF));
                short sample2CH2 = (short) (((input[i + 3 + jumpSample]) << 8) | (input[i + 2 + jumpSample] & 0x00FF));

                int scaleUPSample = audioRobot.getScaleUPSample();
                for (int j = 0; j < scaleUPSample; j++) {
                    short newSampleCH1 = (short) ((((float) sample1CH1) * ((float) (scaleUPSample - j) / (float) scaleUPSample)) + ((float) sample2CH1) * ((float) j / (float) scaleUPSample));
                    short newSampleCH2 = (short) ((((float) sample1CH2) * ((float) (scaleUPSample - j) / (float) scaleUPSample)) + ((float) sample2CH2) * ((float) j / (float) scaleUPSample));

                    int m = i * scaleUPSample + j * jumpSample;

                    output[m + 0] = (byte) (newSampleCH1 & 0xFF);
                    output[m + 1] = (byte) ((newSampleCH1 >>> 8) & 0xFF);
                    output[m + 2] = (byte) (newSampleCH2 & 0xFF);
                    output[m + 3] = (byte) ((newSampleCH2 >>> 8) & 0xFF);
                }
            }

            return output;
        }
    }
}
