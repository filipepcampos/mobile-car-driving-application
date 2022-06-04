package pt.up.fe.mobilecardriving.analysis;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.mobilecardriving.detection.DetectionObject;
import pt.up.fe.mobilecardriving.detection.EvaluationResult;
import pt.up.fe.mobilecardriving.motion.MotionState;
import pt.up.fe.mobilecardriving.warning.ProximityWarning;
import pt.up.fe.mobilecardriving.warning.Warning;

public class DetectionAnalyzer {
    private final EvaluationAnalyzer evaluationAnalyzer;

    private float speed;
    private Bitmap bitmap;

    public DetectionAnalyzer(EvaluationAnalyzer evaluationAnalyzer) {
        this.evaluationAnalyzer = evaluationAnalyzer;

        this.speed = 0;
    }

    public void update(EvaluationResult result, float speed, Bitmap bitmap) {
        this.evaluationAnalyzer.addEvaluationResult(result);
        this.speed = Math.max(0f, speed);
        this.bitmap = bitmap;
    }

    public AnalysisResult getAnalysisResult() {
        final MotionState motionState = getMotionState(this.speed);
        final List<DetectionObject> objects = this.evaluationAnalyzer.getDetectionObjects();
        final List<Warning> warnings = this.getWarnings();

        return new AnalysisResult(objects, warnings, motionState, bitmap);
    }

    private List<Warning> getWarnings() {
        // TODO: REPLACE MOCK DATA BY LOGIC
        final List<Warning> warnings = new ArrayList<>();
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
