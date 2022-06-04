package pt.up.fe.mobilecardriving.detection;

import pt.up.fe.mobilecardriving.util.Position;

public class DetectionObject {
    private final int classIndex;
    private final float score;
    private final Position position;

    public DetectionObject(int classIndex, float score, Position position) {
        this.classIndex = classIndex;
        this.score = score;
        this.position = position;
    }

    public int getClassIndex() {
        return this.classIndex;
    }

    public float getScore() {
        return this.score;
    }

    public Position getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        return "DetectionObject{" +
                "classIndex=" + this.classIndex +
                ", score=" + this.score +
                ", position=" + this.position +
                '}';
    }
}
