package pt.up.fe.mobilecardriving.model;

import pt.up.fe.mobilecardriving.detection.analysis.warning.Warning;

import java.util.LinkedList;
import java.util.List;

public class AnalysisResult {
    private final List<DetectionObject> objects;
    private final List<Warning> warnings;
    private final MotionState motionState;

    public AnalysisResult(List<DetectionObject> objects, List<Warning> warnings, MotionState motionState) {
        this.objects = objects;
        this.warnings = warnings;
        this.motionState = motionState;
    }

    public List<DetectionObject> getObjects() {
        return this.objects;
    }

    public List<Warning> getWarnings() {
        return this.warnings;
    }

    public MotionState getMotionState() {
        return this.motionState;
    }
}
