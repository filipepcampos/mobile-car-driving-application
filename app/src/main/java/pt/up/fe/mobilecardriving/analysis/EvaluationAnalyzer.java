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
    private static final float MINIMUM_SCORE_KITTI = 0.5f * LOOPBACK;
    private static final float MINIMUM_SCORE_GTSDB = 0.4f * LOOPBACK;
    enum Dataset {
        KITTI,
        GTSDB
    };

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

        float[] kittiResult = firstResult.getKittiScores();
        float[] gtsdbResult = firstResult.getGtsdbScores();
        while (iterator.hasNext()) {
            kittiResult = VectorOperation.add(kittiResult, iterator.next().getKittiScores());
            gtsdbResult = VectorOperation.add(gtsdbResult, iterator.next().getGtsdbScores());
        }

        for(int i = 0; i < this.height; ++i){
            for(int j = 0; j < this.width; ++j){
                float score = kittiResult[i*width+j];
                if (score >= MINIMUM_SCORE_KITTI) {
                    objects.add(new DetectionObject(this.calculateBestClass(i, j, Dataset.KITTI),
                            score,
                            new Position(j, i)
                    ));
                }
                score = gtsdbResult[i*width+j];
                if (score >= MINIMUM_SCORE_GTSDB) {
                    objects.add(new DetectionObject(this.calculateBestClass(i, j, Dataset.GTSDB),
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

    private int calculateBestClass(int i, int j, Dataset dataset) {
        final int[] classCounter = new int[this.numClasses];
        int classIdx = -1;

        for (EvaluationResult result : this.resultsQueue) {
            if(dataset == Dataset.KITTI){
                classIdx = result.getKittiClasses()[i*width+j];
            } else {
                classIdx = result.getGtsdbClasses()[i*width+j];
            }
            if (++(classCounter[classIdx]) > LOOPBACK / 2) {
                break;
            }
        }
        if(dataset == Dataset.GTSDB){
            classIdx += 2; // TODO: Receive kitti dataset label size
        }
        return classIdx;
    }
}
