package pt.up.fe.mobilecardriving.detection;

import android.content.Context;
import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import pt.up.fe.mobilecardriving.AssetLoader;

public class PytorchModel {
    private Module module;

    public PytorchModel(Context context, String filepath) throws IOException {
        this.module = Module.load(AssetLoader.getAssetPath(context, filepath));
    }

    public List<BBox> evaluate(Bitmap bitmap) {
        //TODO: RESIZE Bitmap resizedBitmap = Bitmap.createScaledBitmap(mBitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);

        IValue[] outputTuple = this.module.forward(IValue.from(inputTensor)).toTuple();
        Tensor outputTensor = outputTuple[0].toTensor();
        float[] outputs = outputTensor.getDataAsFloatArray();
        return this.processOutputs(outputs);
    }

    private List<BBox> processOutputs(float[] outputs) {
        // TODO
        return Collections.emptyList();
    }
}
