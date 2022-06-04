package pt.up.fe.mobilecardriving.speech;

import android.content.Context;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

public class TextToSpeech {
    private final static float SPEECH_RATE = 0.7f;

    private TextToSpeechListener textToSpeechListener;
    private final android.speech.tts.TextToSpeech tts;

    public TextToSpeech(Context context) {
        this.textToSpeechListener = null;
        this.tts = new android.speech.tts.TextToSpeech(context, new android.speech.tts.TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == android.speech.tts.TextToSpeech.SUCCESS) {
                    final int result = tts.setLanguage(Locale.US); // TODO: GET LOCAL LANGUAGE
                    if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA ||
                            result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onError(String s) {}

                        @Override
                        public void onStart(String s) {}

                        @Override
                        public void onDone(String s) {
                            if (textToSpeechListener != null)
                                textToSpeechListener.onAudioFinished();
                        }
                    });
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        this.tts.setSpeechRate(SPEECH_RATE);
    }

    public void setTextToSpeechListener(TextToSpeechListener listener) {
        this.textToSpeechListener = listener;
    }

    public void speak(String sentence) {
        tts.speak(sentence, android.speech.tts.TextToSpeech.QUEUE_ADD, null, android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
    }
}
