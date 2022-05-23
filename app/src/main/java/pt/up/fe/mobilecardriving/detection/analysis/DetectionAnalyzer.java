package pt.up.fe.mobilecardriving.detection.analysis;

import android.graphics.Bitmap;

import pt.up.fe.mobilecardriving.detection.analysis.warning.ProximityWarning;
import pt.up.fe.mobilecardriving.detection.analysis.warning.Warning;
import pt.up.fe.mobilecardriving.model.AnalysisResult;
import pt.up.fe.mobilecardriving.model.DetectionObject;
import pt.up.fe.mobilecardriving.model.MotionState;

import java.util.ArrayList;
import java.util.List;

public class DetectionAnalyzer {
    private List<DetectionObject> objects;
    private float speed;
    private Bitmap bitmap;

    public  DetectionAnalyzer() {
        this.objects = new ArrayList<>();
        this.speed = 0;
    }

    public void update(List<DetectionObject> objects, float speed, Bitmap bitmap) {
        this.objects = objects;
        this.speed = Math.max(0f, speed);
        this.bitmap = bitmap;
    }

    public AnalysisResult getAnalysisResult() {

        MotionState motionState = getMotionState(this.speed);
        List<Warning> warnings = this.getWarnings();

        return new AnalysisResult(this.objects, warnings, motionState, bitmap);
    }

    private List<Warning> getWarnings() {
        // TODO: REPLACE MOCK DATA BY LOGIC
        List<Warning> warnings = new ArrayList<>();
        warnings.add(new ProximityWarning());
        return  warnings;
    }

    private static MotionState getMotionState(float speed) {
        if (speed <= 1f) return MotionState.STATIONARY;
        else if (speed <= 10f) return MotionState.SLOW;
        else if (speed <= 50f) return MotionState.MEDIUM;
        else return MotionState.HIGH;
    }
}
