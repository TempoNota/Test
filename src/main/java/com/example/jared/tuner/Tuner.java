package com.example.jared.tuner;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Tuner {

    public static final int FREQUENCY_MESSAGE = 1;

    private FFT fft; // Fast Fourier Transform
    //This sample size must be over 2^12.03 ~= 4186Hz; it would under-sample the highest piano note
    //This sample size must be under 2^15.43 ~= 44100Hz; it would perform in excess of the recorder
    //This sample size must be a power of 2; there are math reasons (symmetry I think?)
    //TODO: Find balance between accuracy and time
    //THEORY: Each extra power makes it twice as accurate, but also twice as slow
    private static final int FOURIER_SAMPLE_SIZE = (int) Math.pow(2, 13);

    private AudioRecord audioRecord;
    private Handler uiHandler;

    //Stuff for audioRecord
    private int recordingDevice = MediaRecorder.AudioSource.MIC;
    private int sampleRate = 44100;
    private int channel = AudioFormat.CHANNEL_IN_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, audioEncoding) + 1024;

    public Tuner(Handler handler) {
        uiHandler = handler;
        audioRecord = new AudioRecord(recordingDevice, sampleRate, channel, audioEncoding, bufferSize);
        fft = new FFT(FOURIER_SAMPLE_SIZE);
    }

    public void start(){
        audioRecord.startRecording();
    }

    public void stop() {
        audioRecord.stop();
    }

    public void handleFrequencyState() {
        Bundle bundle = new Bundle();
        bundle.putDouble("frequency", this.getFrequency());

        Message message = uiHandler.obtainMessage(FREQUENCY_MESSAGE);
        message.setData(bundle);

        uiHandler.sendMessage(message);
    }

    private double getFrequency() {
        double frequency;

        double[] imaginaryArray = new double[FOURIER_SAMPLE_SIZE];
        double[] realArray = getAmplitudeArray();
        //Makes both passed arrays represent complex vectors whose magnitudes represent a graph of intensity by frequency
        fft.fft(realArray, imaginaryArray);

        //Interprets the graph to find the highest intensity, and its position
        double largestMagnitudeIndex = 0;
        double largestMagnitude = Math.sqrt(Math.pow(realArray[0], 2) + Math.pow(imaginaryArray[0], 2));
        for(int i = 1; i < FOURIER_SAMPLE_SIZE; i++) {
            double magnitude = Math.sqrt(Math.pow(realArray[i], 2) + Math.pow(imaginaryArray[i], 2));

            if(magnitude > largestMagnitude) {
                largestMagnitudeIndex = i;
                largestMagnitude = magnitude;
            }
        }

        //Turns the position of the highest intensity into the frequency it represents
        frequency = (largestMagnitudeIndex*(sampleRate/FOURIER_SAMPLE_SIZE));
        return frequency;
    }

    private double[] getAmplitudeArray() {
        double[] amplitudeDoubles = new double[FOURIER_SAMPLE_SIZE];
        short[] amplitudeShorts = new short[FOURIER_SAMPLE_SIZE];
        audioRecord.read(amplitudeShorts, 0, FOURIER_SAMPLE_SIZE);

        for(int i = 0; i < FOURIER_SAMPLE_SIZE; i++) {
            amplitudeDoubles[i] = (double) amplitudeShorts[i];
        }
        return amplitudeDoubles;
    }

}