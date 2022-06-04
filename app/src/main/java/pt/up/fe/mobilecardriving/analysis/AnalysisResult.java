package pt.up.fe.mobilecardriving.analysis;
import android.graphics.Bitmap;

import pt.up.fe.mobilecardriving.warning.Warning;
import pt.up.fe.mobilecardriving.detection.DetectionObject;
import pt.up.fe.mobilecardriving.motion.MotionState;

import java.util.List;

public class AnalysisResult {
    private final List<DetectionObject> objects;
    private final List<Warning> warnings;
    private final MotionState motionState;
    private final Bitmap bitmap;

    public AnalysisResult(List<DetectionObject> objects, List<Warning> warnings, MotionState motionState, Bitmap bitmap) {
        this.objects = objects;
        this.warnings = warnings;
        this.motionState = motionState;
        this.bitmap = bitmap;
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

    public Bitmap getBitmap() {
        return bitmap;
    }
}
