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

import pt.up.fe.mobilecardriving.detection.EvaluationResult;
import pt.up.fe.mobilecardriving.util.AssetLoader;

public class PytorchDetector implements ObjectDetector {
    private final static int HEIGHT = 8, WIDTH = 32;
    private final static int NUM_CLASSES = 12;

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

    public int getNumClasses() {
        return NUM_CLASSES;
    }

    public EvaluationResult evaluate(Bitmap imageBitmap) {
        imageBitmap = preProcessBitmap(imageBitmap);

        float[] NO_NORM_STD = {1.0f, 1.0f, 1.0f};
        float[] NO_NORM_MEAN = {0.0f, 0.0f, 0.0f};
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(imageBitmap,
                NO_NORM_MEAN, NO_NORM_STD);

        IValue output = this.module.forward(IValue.from(inputTensor));
        Map<String, IValue> outputMap = output.toDictStringKey();

        // hasObjs shape: [1, 1, 8, 32]
        Tensor hasObjsTensor = Objects.requireNonNull(outputMap.get("hasobjs")).toTensor();
        // classesTensor shape: [1, 12, 8, 32] (12 classes)
        Tensor classesTensor = Objects.requireNonNull(outputMap.get("classes")).toTensor();

        float[] hasObjsArray = hasObjsTensor.getDataAsFloatArray(); // Length 256 (8*32)
        float[] classesArray = classesTensor.getDataAsFloatArray();

        return processOutputs(hasObjsArray, classesArray);
    }

    private static Bitmap preProcessBitmap(Bitmap imageBitmap) {
        return Bitmap.createScaledBitmap(imageBitmap, 1024, 256, false);
    }

    private static EvaluationResult processOutputs(float[] hasObjsArray, float[] classesArray) {
        int arrayLength = HEIGHT * WIDTH;
        float[] scores = new float[arrayLength];
        int[] classes = new int[arrayLength];

        // TODO: MAYBE SOME CALCULATIONS CAN BE OPTIMIZED
        for(int i = 0; i < HEIGHT; ++i){
            for(int j = 0; j < WIDTH; ++j){
                float score = hasObjsArray[i* WIDTH +j];

                float bestClassValue = 0;
                int bestClassIndex = 0;
                for(int k = 0; k < NUM_CLASSES; ++k) {
                    float value = classesArray[k*arrayLength + i* WIDTH + j];
                    if(value > bestClassValue){
                        bestClassValue = value;
                        bestClassIndex = k;
                    }
                }

                scores[i* WIDTH +j] = score;
                classes[i* WIDTH +j] = bestClassIndex;
            }
        }
        return new EvaluationResult(scores, classes);
    }
}
