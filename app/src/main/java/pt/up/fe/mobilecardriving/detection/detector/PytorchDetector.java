package pt.up.fe.mobilecardriving.detection.detector;

import android.content.Context;
import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import pt.up.fe.mobilecardriving.detection.Dataset;
import pt.up.fe.mobilecardriving.detection.EvaluationResult;
import pt.up.fe.mobilecardriving.util.AssetLoader;

public class PytorchDetector implements ObjectDetector {
    private final static int HEIGHT = 8, WIDTH = 32;

    private final Module module;

    public PytorchDetector(Context context, String filepath) throws IOException {
        this.module = LiteModuleLoader.load(AssetLoader.getAssetPath(context, filepath));
    }

    public int getDetectionHeight() {
        return HEIGHT;
    }

    public int getDetectionWidth() {
        return WIDTH;
    }

    public EvaluationResult evaluate(Bitmap imageBitmap) {
        imageBitmap = preProcessBitmap(imageBitmap);

        float[] NO_NORM_STD = {1.0f, 1.0f, 1.0f};
        float[] NO_NORM_MEAN = {0.0f, 0.0f, 0.0f};
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(imageBitmap,
                NO_NORM_MEAN, NO_NORM_STD);

        IValue output = this.module.forward(IValue.from(inputTensor));
        Map<String, IValue> outputMap = output.toDictStringKey();
        System.out.println(outputMap.keySet());

        // scoresTensor shape: [1, 1, 8, 32]
        Tensor kittiScoresTensor = Objects.requireNonNull(outputMap.get("scores_kitti")).toTensor();
        Tensor gtsdbScoresTensor = Objects.requireNonNull(outputMap.get("scores_gtsdb")).toTensor();
        // kittiClassesTensor shape: [1, 2, 8, 32] (2 classes)
        Tensor kittiClassesTensor = Objects.requireNonNull(outputMap.get("classes_kitti")).toTensor();
        // gtsdbClassesTensor shape: [1, 5, 8, 32] (5 classes)
        Tensor gtsdbClassesTensor = Objects.requireNonNull(outputMap.get("classes_gtsdb")).toTensor();

        float[] kittiScoresArray = kittiScoresTensor.getDataAsFloatArray(); // Length 256 (8*32)
        float[] kittiClassesArray = kittiClassesTensor.getDataAsFloatArray();
        float[] gtsdbScoresArray = gtsdbScoresTensor.getDataAsFloatArray();
        float[] gtsdbClassesArray = gtsdbClassesTensor.getDataAsFloatArray();

        return processOutputs(kittiScoresArray, kittiClassesArray, gtsdbScoresArray, gtsdbClassesArray);
    }

    private static Bitmap preProcessBitmap(Bitmap imageBitmap) {
        return Bitmap.createScaledBitmap(imageBitmap, 1024, 256, false);
    }

    private static EvaluationResult processOutputs(float[] kittiScoresArray, float[] kittiClassesArray,
                                                   float[] gtsdbScoresArray, float[] gtsdbClassesArray) {
        int arrayLength = HEIGHT * WIDTH;
        float[] kittiScores = new float[arrayLength];
        int[] kittiClasses = new int[arrayLength];
        float[] gtsdbScores = new float[arrayLength];
        int[] gtsdbClasses = new int[arrayLength];

        for(int i = 0; i < HEIGHT; ++i){
            for(int j = 0; j < WIDTH; ++j){
                // KITTI
                float score = kittiScoresArray[i* WIDTH +j];
                float bestClassValue = 0;
                int bestClassIndex = 0;
                for(int k = 0; k < Dataset.getNumClassesKitti(); ++k) {
                    float value = kittiClassesArray[k*arrayLength + i* WIDTH + j];
                    if(value > bestClassValue){
                        bestClassValue = value;
                        bestClassIndex = k;
                    }
                }
                kittiScores[i* WIDTH +j] = score;
                kittiClasses[i* WIDTH +j] = bestClassIndex;

                // GTSDB
                score = gtsdbScoresArray[i* WIDTH +j];
                bestClassValue = 0;
                bestClassIndex = 0;
                for(int k = 0; k < Dataset.getNumClassesGtsdb(); ++k) {
                    float value = gtsdbClassesArray[k*arrayLength + i* WIDTH + j];
                    if(value > bestClassValue){
                        bestClassValue = value;
                        bestClassIndex = k;
                    }
                }
                gtsdbScores[i* WIDTH +j] = score;
                gtsdbClasses[i* WIDTH +j] = bestClassIndex;
            }
        }
        return new EvaluationResult(kittiScores, kittiClasses, gtsdbScores, gtsdbClasses);
    }
}
