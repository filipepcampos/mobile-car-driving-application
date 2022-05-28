package pt.up.fe.mobilecardriving.detection.model;

import pt.up.fe.mobilecardriving.util.Pair;

public class EvaluationResult extends Pair<float[], int[]> {
    public EvaluationResult(float[] first, int[] second) {
        super(first, second);
    }
}
