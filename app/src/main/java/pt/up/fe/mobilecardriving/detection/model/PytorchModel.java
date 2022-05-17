package pt.up.fe.mobilecardriving.detection.model;

import android.content.Context;
import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.up.fe.mobilecardriving.AssetLoader;
import pt.up.fe.mobilecardriving.model.DetectionObject;

public class PytorchModel implements ObjectDetector {
    private final Module module;

    public PytorchModel(Context context, String filepath) throws IOException {
        this.module = Module.load(AssetLoader.getAssetPath(context, filepath));
    }

    public List<DetectionObject> evaluate(Bitmap imageBitmap) {
        imageBitmap = this.preProcessBitmap(imageBitmap);
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(imageBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);

        IValue[] outputTuple = this.module.forward(IValue.from(inputTensor)).toTuple();
        Tensor outputTensor = outputTuple[0].toTensor();
        float[] outputs = outputTensor.getDataAsFloatArray();
        return this.processOutputs(outputs);
    }

    private static Bitmap preProcessBitmap(Bitmap imageBitmap) {
        /*TODO: RESIZE
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(mBitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);
        */
        return imageBitmap;
    }

    private static List<DetectionObject> processOutputs(float[] outputs) {
        // TODO
        return new ArrayList<>();
    }
}
