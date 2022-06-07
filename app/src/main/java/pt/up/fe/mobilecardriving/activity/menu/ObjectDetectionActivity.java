package pt.up.fe.mobilecardriving.activity.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;

import pt.up.fe.mobilecardriving.R;
import pt.up.fe.mobilecardriving.activity.camera.CameraXActivity;
import pt.up.fe.mobilecardriving.analysis.AnalysisResult;
import pt.up.fe.mobilecardriving.analysis.DetectionAnalyzer;
import pt.up.fe.mobilecardriving.analysis.EvaluationAnalyzer;
import pt.up.fe.mobilecardriving.detection.EvaluationResult;
import pt.up.fe.mobilecardriving.detection.detector.ObjectDetector;
import pt.up.fe.mobilecardriving.detection.detector.PytorchDetector;
import pt.up.fe.mobilecardriving.motion.MotionProvider;
import pt.up.fe.mobilecardriving.motion.MotionProviderListener;
import pt.up.fe.mobilecardriving.util.AssetLoader;
import pt.up.fe.mobilecardriving.view.ResultView;

public class ObjectDetectionActivity extends CameraXActivity<AnalysisResult> implements MotionProviderListener {
    private MotionProvider motionProvider;
    private ObjectDetector objectDetector;
    private DetectionAnalyzer detectionAnalyzer;
    private ResultView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //super.setAnalysisTime(40); // TODO: UNCOMMENT TO RELEASE

        try {
            this.objectDetector = new PytorchDetector(getApplicationContext(), "model.ptl");
            final EvaluationAnalyzer evaluationAnalyzer = new EvaluationAnalyzer(
                    this.objectDetector.getDetectionWidth(),
                    this.objectDetector.getDetectionHeight(),
                    this.objectDetector.getNumClasses()
            );
            this.detectionAnalyzer = new DetectionAnalyzer(evaluationAnalyzer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.resultView = findViewById(R.id.resultView);

        this.setupTracker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.motionProvider.pause();
    }

    private void setupTracker() {
        this.motionProvider = new MotionProvider((LocationManager) getSystemService(LOCATION_SERVICE));
        this.motionProvider.setProviderListener(this);
        this.motionProvider.resume();
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
        final Bitmap bitmap = AssetLoader.imgToBitmap(Objects.requireNonNull(image.getImage()));

        final int imageHeight = bitmap.getWidth() / 4; // 4:1 aspect ratio
        final Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()-imageHeight, bitmap.getWidth(), imageHeight);

        final EvaluationResult evaluationResult = this.objectDetector.evaluate(croppedBitmap);
        this.detectionAnalyzer.update(evaluationResult, this.motionProvider.getSpeed(), croppedBitmap);

        return this.detectionAnalyzer.getAnalysisResult();
    }

    @Override
    public void onProviderEnabled() {}

    @Override
    public void onProviderDisabled() {
        final Intent intent = new Intent(ObjectDetectionActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
