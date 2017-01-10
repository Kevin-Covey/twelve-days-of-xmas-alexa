package com.quaildev.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.quaildev.lean.tech.MyTrueLove;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;
import static org.apache.commons.lang3.StringUtils.isNumeric;

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
        Day day = translateDayFromSlot(intent);

        MyTrueLove myTrueLove = new MyTrueLove();
        myTrueLove.onDay(day.getIntValue());
        Iterator<String> descendingIterator = myTrueLove.hasGivenToMe().stream()
                .collect(Collectors.toCollection(LinkedList::new))
                .descendingIterator();
        String responseDialogue = stream(spliteratorUnknownSize(descendingIterator, Spliterator.ORDERED), false)
                .collect(joining(", ", preambleFor(day), ""))
                .replaceAll("-", " ");

        int indexOfFirstComma = responseDialogue.indexOf(',');
        int indexOfLastComma = responseDialogue.lastIndexOf(',');
        if (indexOfLastComma > indexOfFirstComma) {
            responseDialogue = replaceLastCommaWithAnd(responseDialogue, indexOfLastComma);
        }

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(responseDialogue);
        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    private Day translateDayFromSlot(Intent intent) {
        String slotValue = intent.getSlot("Day").getValue();
        log.debug("Day value from slot is " + slotValue);
        String dayString = slotValue.replaceAll("[a-zA-Z]", "");
        return isNumeric(dayString)
                ? Day.forIntValue(Integer.valueOf(dayString))
                : Day.valueOf(slotValue.toUpperCase());
    }

    private String preambleFor(Day day) {
        return format("By the %s day of christmas, your true love had given to you:  ", day);
    }

    private String replaceLastCommaWithAnd(String responseDialogue, int indexOfLastComma) {
        return new StringBuilder(responseDialogue).replace(indexOfLastComma, indexOfLastComma + 1, ", and").toString();
    }

    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
    }

}
