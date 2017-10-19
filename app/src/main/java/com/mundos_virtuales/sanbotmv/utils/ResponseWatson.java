package com.mundos_virtuales.sanbotmv.utils;


public class ResponseWatson {

    private float confidence;

    private String input;

    private String output;

    public ResponseWatson(float _confidence, String _input, String _output) {
        this.confidence = _confidence;
        this.input = _input;
        this.output = _output;
    }

    public float getConfidence(){
        return confidence;
    }

    public String getInput(){
        return input;
    }

    public String getOutput(){
        return output;
    }
}
