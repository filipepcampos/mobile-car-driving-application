package pt.up.fe.mobilecardriving.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import pt.up.fe.mobilecardriving.R;
import pt.up.fe.mobilecardriving.model.AnalysisResult;
import pt.up.fe.mobilecardriving.view.ResultView;

public class ObjectDetectionActivity extends CameraXActivity<AnalysisResult> {
    private ResultView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_object_detection;
    }

    @Override
    protected PreviewView getCameraPreviewView() {
        /*resultView = findViewById(R.id.resultView);
        return ((ViewStub) findViewById(R.id.object_detection_texture_view_stub))
                .inflate()
                .findViewById(R.id.object_detection_texture_view);
    }*/
        return findViewById(R.id.previewView);
    }

    @Override
    protected Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @Override
    protected void applyToUiAnalyzeImageResult(AnalysisResult result) {
        /*mResultView.setResults(result.mResults);
        mResultView.invalidate();*/
    }

    @Override
    @WorkerThread
    @Nullable
    protected AnalysisResult analyzeImage(ImageProxy image) {
        /*try {
            if (mModule == null) {
                mModule = LiteModuleLoader.load(MainActivity.assetFilePath(getApplicationContext(), "yolov5s.torchscript.ptl"));
            }
        } catch (IOException e) {
            Log.e("Object Detection", "Error reading assets", e);
            return null;
        }
        Bitmap bitmap = imgToBitmap(image.getImage());
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);

        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);
        IValue[] outputTuple = mModule.forward(IValue.from(inputTensor)).toTuple();
        final Tensor outputTensor = outputTuple[0].toTensor();
        final float[] outputs = outputTensor.getDataAsFloatArray();

        float imgScaleX = (float)bitmap.getWidth() / PrePostProcessor.mInputWidth;
        float imgScaleY = (float)bitmap.getHeight() / PrePostProcessor.mInputHeight;
        float ivScaleX = (float)mResultView.getWidth() / bitmap.getWidth();
        float ivScaleY = (float)mResultView.getHeight() / bitmap.getHeight();

        final ArrayList<Result> results = PrePostProcessor.outputsToNMSPredictions(outputs, imgScaleX, imgScaleY, ivScaleX, ivScaleY, 0, 0);
        return new AnalysisResult(results);*/
        return null;
    }
}