package com.quaildev.alexa;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.quaildev.lean.tech.MyTrueLove;

import java.util.HashSet;

import static java.util.Collections.singletonList;

public class MyTrueLoveSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    public MyTrueLoveSpeechletRequestStreamHandler() {
        super(
                new MyTrueLoveSpeechlet(new MyTrueLove()),
                new HashSet<String>(singletonList("amzn1.ask.skill.c5ad38f7-9e42-435d-8d41-a100d2280d4e"))
        );
    }
}
