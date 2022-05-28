package pt.up.fe.mobilecardriving.detection.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pt.up.fe.mobilecardriving.AssetLoader;
import pt.up.fe.mobilecardriving.model.DetectionObject;

public class PytorchModel implements ObjectDetector {
    private final static int height = 8, width = 32;
    private final static int numClasses = 7;
    private final Module module;

    public PytorchModel(Context context, String filepath) throws IOException {
        this.module = LiteModuleLoader.load(AssetLoader.getAssetPath(context, filepath));
    }

    public int getDetectionHeight() {
        return height;
    }

    public int getDetectionWidth() {
        return width;
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
        // classesTensor shape: [1, 7, 8, 32] (7 classes)
        Tensor classesTensor = Objects.requireNonNull(outputMap.get("classes")).toTensor();

        float[] hasObjsArray = hasObjsTensor.getDataAsFloatArray(); // Length 256 (8*32)
        float[] classesArray = classesTensor.getDataAsFloatArray();

        return processOutputs(hasObjsArray, classesArray);
    }

    private static Bitmap preProcessBitmap(Bitmap imageBitmap) {
        return Bitmap.createScaledBitmap(imageBitmap, 1024, 256, false);
    }

    private static EvaluationResult processOutputs(float[] hasObjsArray, float[] classesArray) {
        int arrayLength = height*width;
        float[] scores = new float[arrayLength];
        int[] classes = new int[arrayLength];

        // TODO: MAYBE SOME CALCULATIONS CAN BE OPTIMIZED
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                float score = hasObjsArray[i*width+j];

                float bestClassValue = 0;
                int bestClassIndex = 0;
                for(int k = 0; k < numClasses; ++k) {
                    float value = classesArray[k*arrayLength + i*width + j];
                    if(value > bestClassValue){
                        bestClassValue = value;
                        bestClassIndex = k;
                    }
                }

                scores[i*width+j] = score;
                classes[i*width+j] = bestClassIndex;
            }
        }
        return new EvaluationResult(scores, classes);
    }
}
