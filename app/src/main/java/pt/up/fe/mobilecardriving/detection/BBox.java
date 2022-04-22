package pt.up.fe.mobilecardriving.detection;

import android.graphics.Rect;

public class BBox {
    private int classIndex;
    private float score;
    private Rect rect;

    public BBox(int classIndex, float score, Rect rect) {
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
}
