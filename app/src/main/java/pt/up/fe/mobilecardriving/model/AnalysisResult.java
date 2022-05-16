package pt.up.fe.mobilecardriving.model;

import java.util.LinkedList;
import java.util.List;

public class AnalysisResult {
    private List<DetectionObject> objects;
    private float speed;

    public AnalysisResult() {
        this(0);
    }

    public AnalysisResult(float speed) {
        this(new LinkedList<>(), speed);
    }

    public AnalysisResult(List<DetectionObject> objects, float speed) {
        this.objects = objects;
        this.speed = speed;
    }

    public List<DetectionObject> getObjects() {
        return this.objects;
    }

    public void addObject(DetectionObject object) {
        this.objects.add(object);
    }

    public float getSpeed() {
        return this.speed;
    }
}
