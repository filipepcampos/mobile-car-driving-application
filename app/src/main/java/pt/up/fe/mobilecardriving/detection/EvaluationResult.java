package pt.up.fe.mobilecardriving.detection;

public class EvaluationResult {
    float[] kittiScores;
    int[] kittiClasses;
    float[] gtsdbScores;
    int[] gtsdbClasses;

    public EvaluationResult(float[] kittiScores, int[] kittiClasses, float[] gtsdbScores, int[] gtsdbClasses) {
        this.kittiScores = kittiScores;
        this.kittiClasses = kittiClasses;
        this.gtsdbScores = gtsdbScores;
        this.gtsdbClasses = gtsdbClasses;
    }

    public float[] getKittiScores() {
        return kittiScores;
    }

    public float[] getGtsdbScores() {
        return gtsdbScores;
    }

    public int[] getGtsdbClasses() {
        return gtsdbClasses;
    }

    public int[] getKittiClasses() {
        return kittiClasses;
    }
}
