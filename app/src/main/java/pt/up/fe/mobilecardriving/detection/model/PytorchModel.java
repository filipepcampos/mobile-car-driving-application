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
    private final Module module;

    public PytorchModel(Context context, String filepath) throws IOException {
        this.module = LiteModuleLoader.load(AssetLoader.getAssetPath(context, filepath));
    }

    public List<DetectionObject> evaluate(Bitmap imageBitmap) {
        imageBitmap = this.preProcessBitmap(imageBitmap);

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

        return this.processOutputs(hasObjsArray, classesArray);
    }

    private static Bitmap preProcessBitmap(Bitmap imageBitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 1024, 256, false);
        return resizedBitmap;
    }

    private static List<DetectionObject> processOutputs(float[] hasObjsArray, float[] classesArray) {
        List<DetectionObject> detectionObjects = new ArrayList<>();
        for(int i = 0; i < 8; ++i){
            for(int j = 0; j < 32; ++j){
                float score = hasObjsArray[i*32+j];
                if(score > 0.90){
                    Rect rectangle = new Rect(j, i,j+1, i+1);

                    float bestClassValue = 0;
                    int bestClassIndex = 0;
                    for(int k = 0; k < 7; ++k){ // TODO: Confirm if this works as expected
                        float value = classesArray[k*32*8 + i*32 + j];
                        if(value > bestClassValue){
                            bestClassValue = value;
                            bestClassIndex = k;
                        }
                    }

                    detectionObjects.add(new DetectionObject(bestClassIndex, score, rectangle));
                }
            }
        }
        return detectionObjects;
    }
}
