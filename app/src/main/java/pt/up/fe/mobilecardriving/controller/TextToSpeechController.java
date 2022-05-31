package pt.up.fe.mobilecardriving.controller;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

import pt.up.fe.mobilecardriving.activity.TextToSpeechListener;

public class TextToSpeechController {
    private TextToSpeechListener textToSpeechListener;
    private TextToSpeech tts;
    private final static  float speechRate = 0.7f;

    public TextToSpeechController(Context context) {
        this.textToSpeechListener = null;
        this.tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US); // TODO
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {

                        }

                        @Override
                        public void onDone(String s) {
                            if (textToSpeechListener != null) {
                                textToSpeechListener.onAudioFinished();
                            }
                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        this.tts.setSpeechRate(speechRate);
    }

    public void setTextToSpeechListener(TextToSpeechListener listener) {
        this.textToSpeechListener = listener;
    }

    public void speak(String sentence) {
        tts.speak(sentence, TextToSpeech.QUEUE_ADD, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
    }
}
