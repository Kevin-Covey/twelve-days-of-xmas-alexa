package com.quaildev.alexa

import com.amazon.speech.slu.Intent
import com.amazon.speech.slu.Slot
import com.amazon.speech.speechlet.IntentRequest
import com.amazon.speech.speechlet.SpeechletException
import com.quaildev.lean.tech.MyTrueLove
import spock.lang.Specification

import static com.quaildev.alexa.MyTrueLoveSpeechlet.Intents.MY_TRUE_LOVE

class MyTrueLoveSpeechletSpec extends Specification {

    IntentRequest mockRequest = Mock()
    MyTrueLove mockTrueLove = Mock()
    MyTrueLoveSpeechlet speechlet = new MyTrueLoveSpeechlet(mockTrueLove)

    def 'bogus intent throws exception'() {
        setup:
        def intent = Intent.builder().withName('BogusIntent').build()
        mockRequest.intent >> intent

        when:
        speechlet.onIntent(mockRequest, null)

        then:
        thrown(SpeechletException)
    }

    def 'sets day correctly for wordy descriptions'() {
        setup:
        setupMyTrueLoveIntentWithDaySlotValueAs slotValue
        mockTrueLove.hasGivenToMe() >> []

        when:
        speechlet.onIntent(mockRequest, null)

        then:
        1 * mockTrueLove.onDay(day)

        where:
        slotValue  | day
        'first'    | 1
        'second'   | 2
        'third'    | 3
        'fourth'   | 4
        'fifth'    | 5
        'sixth'    | 6
        'seventh'  | 7
        'eighth'   | 8
        'ninth'    | 9
        'tenth'    | 10
        'eleventh' | 11
        'twelfth'  | 12
    }

    def 'sets day correctly for numberish descriptions'() {
        setup:
        setupMyTrueLoveIntentWithDaySlotValueAs slotValue
        mockTrueLove.hasGivenToMe() >> []

        when:
        speechlet.onIntent(mockRequest, null)

        then:
        1 * mockTrueLove.onDay(day)

        where:
        slotValue | day
        '1ST'     | 1
        '2ND'     | 2
        '3RD'     | 3
        '4TH'     | 4
        '5TH'     | 5
        '6TH'     | 6
        '7TH'     | 7
        '8TH'     | 8
        '9TH'     | 9
        '10TH'    | 10
        '11TH'    | 11
        '12TH'    | 12
    }

    def 'singleton response (first day)'() {
        setup:
        setupMyTrueLoveIntentWithDaySlotValueAs 'first'
        mockTrueLove.hasGivenToMe() >> ['1 partridge in a pear tree']

        when:
        def response = speechlet.onIntent(mockRequest, null)

        then:
        response.outputSpeech.text ==
                'By the FIRST day of christmas, your true love had given to you:  1 partridge in a pear tree'
    }

    def "many item response, order is reversed and there's an 'and' before the last item"() {
        setup:
        setupMyTrueLoveIntentWithDaySlotValueAs 'third'
        mockTrueLove.hasGivenToMe() >> [firstGift, secondGift, thirdGift]

        when:
        def response = speechlet.onIntent(mockRequest, null)

        then:
        response.outputSpeech.text ==
                "By the THIRD day of christmas, your true love had given to you:  $thirdGift, $secondGift, and $firstGift"

        where:
        firstGift                     | secondGift       | thirdGift
        '3 partridges in a pear tree' | '4 turtle doves' | '3 french hens'
    }

    def 'response ends session'() {
        setup:
        setupMyTrueLoveIntentWithDaySlotValueAs 'first'
        mockTrueLove.hasGivenToMe() >> []

        when:
        def response = speechlet.onIntent(mockRequest, null)

        then:
        response.shouldEndSession
    }

    private void setupMyTrueLoveIntentWithDaySlotValueAs(String slotValue) {
        def daySlot = Slot.builder().withName('Day').withValue(slotValue).build()
        def intent = Intent.builder().withName(MY_TRUE_LOVE).withSlots(['Day': daySlot]).build()
        mockRequest.intent >> intent
    }
}
