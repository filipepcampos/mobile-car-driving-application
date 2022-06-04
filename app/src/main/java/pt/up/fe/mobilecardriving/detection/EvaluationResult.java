package pt.up.fe.mobilecardriving.detection;

import pt.up.fe.mobilecardriving.util.Pair;

public class EvaluationResult extends Pair<float[], int[]> {
    public EvaluationResult(float[] scores, int[] classes) {
        super(scores, classes);
    }

    public float[] getScores() {
        return super.first;
    }

    public int[] getClasses() {
        return super.second;
    }
}
