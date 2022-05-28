package pt.up.fe.mobilecardriving.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import pt.up.fe.mobilecardriving.R;
import pt.up.fe.mobilecardriving.detection.model.EvaluationResult;
import pt.up.fe.mobilecardriving.detection.model.PytorchModel;
import pt.up.fe.mobilecardriving.detection.analysis.DetectionAnalyzer;
import pt.up.fe.mobilecardriving.detection.model.ObjectDetector;
import pt.up.fe.mobilecardriving.model.AnalysisResult;
import pt.up.fe.mobilecardriving.model.DetectionObject;
import pt.up.fe.mobilecardriving.view.ResultView;

public class ObjectDetectionActivity extends CameraXActivity<AnalysisResult> implements ProviderListener {
    private MotionTracker motionTracker;
    private ObjectDetector objectDetector;
    private DetectionAnalyzer detectionAnalyzer;
    private ResultView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setAnalysisTime(40);

        try {
            this.objectDetector = new PytorchModel(getApplicationContext(), "model.ptl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.detectionAnalyzer = new DetectionAnalyzer(this.objectDetector.getDetectionWidth(), this.objectDetector.getDetectionHeight());
        this.resultView = findViewById(R.id.resultView);

        this.setupTracker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.motionTracker.pause();
    }

    private void setupTracker() {
        this.motionTracker = new MotionTracker((LocationManager) getSystemService(LOCATION_SERVICE));
        this.motionTracker.setProviderListener(this);
        this.motionTracker.resume();
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

        int imageHeight = (int) bitmap.getWidth() / 4; // 4:1 aspect ratio
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()-imageHeight, bitmap.getWidth(), imageHeight);

        EvaluationResult evaluationResult = this.objectDetector.evaluate(croppedBitmap);
        this.detectionAnalyzer.update(evaluationResult, this.motionTracker.getSpeed(), croppedBitmap);

        return this.detectionAnalyzer.getAnalysisResult();
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

    @Override
    public void onProviderEnabled() {}

    @Override
    public void onProviderDisabled() {
        Intent intent = new Intent(ObjectDetectionActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}