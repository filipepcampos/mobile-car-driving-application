package pt.up.fe.mobilecardriving.model;

import android.graphics.Rect;

public class DetectionObject {
    private int classIndex;
    private float score;
    private Rect rect;

    public DetectionObject(int classIndex, float score, Rect rect) {
        this.classIndex = classIndex;
        this.score = score;
        this.rect = rect;
    }

    public int getClassIndex() {
        return this.classIndex;
    }

    public float getScore() {
        return this.score;
    }

    public Rect getRect() {
        return this.rect;
    }

    @Override
    public String toString() {
        return "DetectionObject{" +
                "classIndex=" + classIndex +
                ", score=" + score +
                ", rect=" + rect +
                '}';
    }
}
