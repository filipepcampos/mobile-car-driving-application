package pt.up.fe.mobilecardriving.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import pt.up.fe.mobilecardriving.R;
import pt.up.fe.mobilecardriving.detection.ObjectDetector;
import pt.up.fe.mobilecardriving.detection.TestModel;
import pt.up.fe.mobilecardriving.model.AnalysisResult;
import pt.up.fe.mobilecardriving.model.DetectionObject;
import pt.up.fe.mobilecardriving.view.ResultView;

public class ObjectDetectionActivity extends CameraXActivity<AnalysisResult> {
    private ObjectDetector objectDetector;
    private ResultView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.objectDetector = new TestModel(); // TODO: REPLACE MOCK MODEL
        this.resultView = findViewById(R.id.resultView);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_object_detection;
    }

    @Override
    protected PreviewView getCameraPreviewView() {
        return findViewById(R.id.previewView);
    }

    @Override
    protected Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @Override
    protected void applyToUiAnalyzeImageResult(AnalysisResult result) {
        this.resultView.setResult(result);
        this.resultView.invalidate();
    }

    @Override
    @WorkerThread
    @Nullable
    protected AnalysisResult analyzeImage(ImageProxy image) {
        @SuppressLint("UnsafeOptInUsageError")
        Bitmap bitmap = imgToBitmap(Objects.requireNonNull(image.getImage()));

        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f); // TODO: NOT SURE WHY WE NEED THIS
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        List<DetectionObject> objects = this.objectDetector.evaluate(bitmap);
        // TODO: CREATE METHOD THAT ANALYSES THE OBJECTS AND RETURNS AN ANALYSIS RESULT
        return new AnalysisResult(objects);
    }

    private static Bitmap imgToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}