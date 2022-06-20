package pt.up.fe.mobilecardriving.analysis;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.mobilecardriving.detection.DetectionObject;
import pt.up.fe.mobilecardriving.detection.EvaluationResult;
import pt.up.fe.mobilecardriving.motion.MotionState;
import pt.up.fe.mobilecardriving.warning.CarProximityWarning;
import pt.up.fe.mobilecardriving.warning.PedestrianProximityWarning;
import pt.up.fe.mobilecardriving.warning.TrafficSign;
import pt.up.fe.mobilecardriving.warning.TrafficSignWarning;
import pt.up.fe.mobilecardriving.warning.Warning;

public class DetectionAnalyzer {
    private final EvaluationAnalyzer evaluationAnalyzer;

    private float speed;
    private Bitmap bitmap;

    private static int CAR_DETECTION_LOWER_LIMIT = 7;
    private static int PEDESTRIAN_DETECTION_LOWER_LIMIT = 4;

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
        final List<Warning> warnings = new ArrayList<>();
        for(DetectionObject obj : evaluationAnalyzer.getDetectionObjects()){
            switch(obj.getClassIndex()){
                case 0:
                    if(obj.getPosition().getY() >= CAR_DETECTION_LOWER_LIMIT){
                        warnings.add(new CarProximityWarning(6));
                    }
                    break;
                case 1:
                    if(obj.getPosition().getY() >= PEDESTRIAN_DETECTION_LOWER_LIMIT){
                        warnings.add(new PedestrianProximityWarning(0));
                    }
                    break;
                case 2:
                    warnings.add(new TrafficSignWarning(TrafficSign.STOP, 1));
                    break;
                case 3:
                    warnings.add(new TrafficSignWarning(TrafficSign.GIVE_WAY, 2));
                    break;
                case 4:
                    warnings.add(new TrafficSignWarning(TrafficSign.PROHIBITED, 3));
                    break;
                case 5:
                    warnings.add(new TrafficSignWarning(TrafficSign.PROHIBITED_OVERTAKING, 4));
                    break;
                case 6:
                    warnings.add(new TrafficSignWarning(TrafficSign.ALLOWED_OVERTAKING, 5));
                    break;
            }
        }
        return  warnings;
    }

    private static MotionState getMotionState(float speed) {
        if (speed <= 1f) return MotionState.STATIONARY;
        else if (speed <= 10f) return MotionState.SLOW;
        else if (speed <= 50f) return MotionState.MEDIUM;
        else return MotionState.HIGH;
    }
}
