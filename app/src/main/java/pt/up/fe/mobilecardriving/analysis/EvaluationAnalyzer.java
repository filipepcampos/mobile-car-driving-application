package pt.up.fe.mobilecardriving.analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import pt.up.fe.mobilecardriving.detection.DetectionObject;
import pt.up.fe.mobilecardriving.detection.EvaluationResult;
import pt.up.fe.mobilecardriving.util.Position;
import pt.up.fe.mobilecardriving.util.VectorOperation;

public class EvaluationAnalyzer {
    private static final int LOOPBACK = 3;
    private static final float MINIMUM_SCORE = 0.5f * LOOPBACK;

    private final int width, height;
    private final int numClasses;

    private final Queue<EvaluationResult> resultsQueue;

    public EvaluationAnalyzer(int width, int height, int numClasses) {
        this.width = width;
        this.height = height;
        this.numClasses = numClasses;

        this.resultsQueue = new LinkedList<>();
    }

    public List<DetectionObject> getDetectionObjects() {
        final List<DetectionObject> objects = new LinkedList<>();

        if (this.resultsQueue.size() < LOOPBACK) return objects;

        final Iterator<EvaluationResult> iterator = this.resultsQueue.iterator();
        final EvaluationResult firstResult = iterator.next();
        float[] result = firstResult.getScores();
        while (iterator.hasNext()) {
            result = VectorOperation.add(result, iterator.next().getScores());
        }

        for(int i = 0; i < this.height; ++i){
            for(int j = 0; j < this.width; ++j){
                float score = result[i*width+j];

                if (score >= MINIMUM_SCORE) {
                    objects.add(new DetectionObject(this.calculateBestClass(i, j),
                            score,
                            new Position(j, i)
                    ));
                }
            }
        }
        return objects;
    }

    public void addEvaluationResult(EvaluationResult result) {
        this.resultsQueue.add(result);
        if (this.resultsQueue.size() > LOOPBACK)
            this.resultsQueue.remove();
    }

    private int calculateBestClass(int i, int j) {
        final int[] classCounter = new int[this.numClasses];
        int classIdx = -1;

        for (EvaluationResult result : this.resultsQueue) {
            classIdx = result.getClasses()[i*width+j];
            if (++(classCounter[classIdx]) > LOOPBACK / 2) {
                break;
            }
        }
        return classIdx;
    }
}
