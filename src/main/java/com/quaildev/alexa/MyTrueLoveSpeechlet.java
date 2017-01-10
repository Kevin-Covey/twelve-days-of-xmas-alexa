package com.quaildev.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.quaildev.lean.tech.MyTrueLove;
import org.apache.log4j.Logger;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class MyTrueLoveSpeechlet implements Speechlet {

    private static final Logger log = Logger.getLogger(MyTrueLoveSpeechlet.class);

    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
    }

    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        return null;
    }

    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        Intent intent = request.getIntent();
        String intentName = intent != null ? intent.getName() : null;

        if ("MyTrueLoveIntent".equals(intentName)) {
            return createResponse(intent);
        }
        throw new SpeechletException("Invalid Intent");
    }

    private SpeechletResponse createResponse(Intent intent) {
        String dayValue = intent.getSlot("Day").getValue();
        log.debug("Day value from slot is " + dayValue);
        int whichDay = Integer.parseInt(dayValue.replaceAll("[a-zA-Z]", ""));
        Day day = Day.forIntValue(whichDay);

        MyTrueLove myTrueLove = new MyTrueLove();
        myTrueLove.onDay(whichDay);
        String responseDialogue = myTrueLove.hasGivenToMe().stream().collect(joining(", ", preambleFor(day), ""));

        int indexOfFirstComma = responseDialogue.indexOf(',');
        int indexOfLastComma = responseDialogue.lastIndexOf(',');
        if (indexOfLastComma > indexOfFirstComma) {
            responseDialogue = replaceLastCommaWithAnd(responseDialogue, indexOfLastComma);
        }

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(responseDialogue);
        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    private String preambleFor(Day day) {
        return format("By the %s day of christmas, your true love had given to you:  ", day);
    }

    private String replaceLastCommaWithAnd(String responseDialogue, int indexOfLastComma) {
        return new StringBuilder(responseDialogue).replace(indexOfLastComma, indexOfLastComma + 1, " and").toString();
    }

    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
    }

}
