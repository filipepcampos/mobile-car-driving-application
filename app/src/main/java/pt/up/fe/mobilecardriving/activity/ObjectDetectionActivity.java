package pt.up.fe.mobilecardriving.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.SensorManager;
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
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 201;
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    private MotionTracker motionTracker;
    private ObjectDetector objectDetector;
    private ResultView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setAnalysisTime(40);
        this.objectDetector = new TestModel(); // TODO: REPLACE MOCK MODEL
        this.resultView = findViewById(R.id.resultView);

        // TODO: MOVE VERIFICATION TO MENU
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            this.setupTracker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                                this,
                                "You can't use object detection example without granting GPS permission",
                                Toast.LENGTH_LONG)
                        .show();
                finish();
            } else {
                this.setupTracker();
            }
        }
    }

    private void setupTracker() {
        this.motionTracker = new MotionTracker((LocationManager) getSystemService(LOCATION_SERVICE));
        this.motionTracker.resume(this);
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
        return new AnalysisResult(objects, this.motionTracker.getSpeed());
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