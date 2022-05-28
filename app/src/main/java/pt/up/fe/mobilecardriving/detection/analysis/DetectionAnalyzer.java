package pt.up.fe.mobilecardriving.detection.analysis;

import android.graphics.Bitmap;
import android.graphics.Rect;

import pt.up.fe.mobilecardriving.detection.analysis.warning.ProximityWarning;
import pt.up.fe.mobilecardriving.detection.analysis.warning.Warning;
import pt.up.fe.mobilecardriving.detection.model.EvaluationResult;
import pt.up.fe.mobilecardriving.model.AnalysisResult;
import pt.up.fe.mobilecardriving.model.DetectionObject;
import pt.up.fe.mobilecardriving.model.MotionState;
import pt.up.fe.mobilecardriving.util.VectorOperations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DetectionAnalyzer {
    private static final int LOOPBACK = 3;
    private static final float MINIMUM_SCORE = 0.8f * LOOPBACK;

    private final int width, height;
    private final Queue<EvaluationResult> resultsQueue;
    private float speed;
    private Bitmap bitmap;

    public DetectionAnalyzer(int width, int height) {
        this.width = width;
        this.height = height;
        this.resultsQueue = new LinkedList<>();
        this.speed = 0;
    }

    public void update(EvaluationResult results, float speed, Bitmap bitmap) {
        this.addEvaluationResult(results);
        this.speed = Math.max(0f, speed);
        this.bitmap = bitmap;
    }

    public AnalysisResult getAnalysisResult() {

        MotionState motionState = getMotionState(this.speed);
        List<DetectionObject> objects = this.getDetectionObjects();
        List<Warning> warnings = this.getWarnings();

        return new AnalysisResult(objects, warnings, motionState, bitmap);
    }

    private List<DetectionObject> getDetectionObjects() {
        List<DetectionObject> objects = new LinkedList<>();

        if (this.resultsQueue.size() < LOOPBACK) return objects;

        Iterator<EvaluationResult> iterator = this.resultsQueue.iterator();
        EvaluationResult firstResult = iterator.next();
        float[] result = firstResult.first;
        while (iterator.hasNext()) {
            result = VectorOperations.add(result, iterator.next().first);
        }

        for(int i = 0; i < this.height; ++i){
            for(int j = 0; j < this.width; ++j){
                float score = result[i*width+j];

                if (score >= MINIMUM_SCORE) {
                    objects.add(new DetectionObject(firstResult.second[i*this.width+j], score, new Rect(j, i, j+1, i+1)));
                }
            }
        }

        return objects;
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

    private void addEvaluationResult(EvaluationResult result) {
        this.resultsQueue.add(result);
        if (this.resultsQueue.size() > LOOPBACK)
            this.resultsQueue.remove();
    }
}
