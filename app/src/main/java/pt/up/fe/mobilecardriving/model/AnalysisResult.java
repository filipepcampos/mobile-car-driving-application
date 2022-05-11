package pt.up.fe.mobilecardriving.model;

import java.util.LinkedList;
import java.util.List;

public class AnalysisResult {
    private List<DetectionObject> objects;

    public AnalysisResult() {
        this(new LinkedList<>());
    }

    public AnalysisResult(List<DetectionObject> objects) {
        this.objects = objects;
    }

    public List<DetectionObject> getObjects() {
        return this.objects;
    }

    public void addObject(DetectionObject object) {
        this.objects.add(object);
    }
}
