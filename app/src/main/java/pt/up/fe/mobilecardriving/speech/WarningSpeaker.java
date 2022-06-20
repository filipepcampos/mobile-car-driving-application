package pt.up.fe.mobilecardriving.speech;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.mobilecardriving.warning.Warning;

public class WarningSpeaker implements TextToSpeechListener {
    private final TextToSpeech textToSpeech;
    private boolean isSpeaking;
    private final Map<Class<?>, Long> lastWarningMap;
    private final int WARNING_WAIT_TIME = 5000;


    public WarningSpeaker(TextToSpeech textToSpeech){
        this.textToSpeech = textToSpeech;
        this.textToSpeech.setTextToSpeechListener(this);
        this.isSpeaking = false;
        this.lastWarningMap = new HashMap<>();
    }

    public void speak(List<Warning> warningList){
        if(!warningList.isEmpty() && !this.isSpeaking){
            Collections.sort(warningList);
            for(Warning warning : warningList){
                if(this.lastWarningMap.containsKey(warning.getClass())) {
                    long delta = System.currentTimeMillis() - this.lastWarningMap.get(warning.getClass());
                    if(delta <= WARNING_WAIT_TIME) {
                        continue;
                    }
                }
                this.lastWarningMap.put(warning.getClass(), System.currentTimeMillis());
                this.textToSpeech.speak(warning.getMessage());
                this.isSpeaking = true;
                break;
            }
        }
    }

    @Override
    public void onAudioFinished() {
        this.isSpeaking = false;
    }
}
