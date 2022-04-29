package pt.up.fe.mobilecardriving.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public abstract class CameraXActivity<R> extends BaseModuleActivity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};

    private long mLastAnalysisResultTime;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    protected abstract int getContentViewLayoutId();

    protected abstract PreviewView getCameraPreviewView();

    protected abstract Executor getExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                setupCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

        startBackgroundThread();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                setupCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                        this,
                        "You can't use object detection example without granting CAMERA permission",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            } else {
                // TODO: THIS TRY CATCH COULD BE A FUNCTION
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    setupCameraX(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        final CameraSelector cameraSelector = this.setupCameraSelector();
        final Preview preview = this.setupCameraPreview();
        //final ImageCapture imageCapture = this.setupImageCapture(); // TODO: MAYBE THIS IS NOT NECESSARY?
        //final ImageAnalysis imageAnalysis = this.setupImageAnalysis();

        //cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
        cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    private Preview setupCameraPreview() {
        final Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(getCameraPreviewView().getSurfaceProvider());
        return preview;
    }

    private CameraSelector setupCameraSelector() {
        return new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
    }

    private ImageCapture setupImageCapture() {
        return new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
    }

    private ImageAnalysis setupImageAnalysis() {
        final ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                // TODO: enable the following line if RGBA output is needed.
                //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                //.setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(this.getExecutor(), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                if (SystemClock.elapsedRealtime() - mLastAnalysisResultTime < 500) {
                    return;
                }
                final R result = analyzeImage(imageProxy);
                if (result != null) {
                    mLastAnalysisResultTime = SystemClock.elapsedRealtime();
                    runOnUiThread(() -> applyToUiAnalyzeImageResult(result));
                }
                imageProxy.close();
            }
        });

        return imageAnalysis;
    }

    @WorkerThread
    @Nullable
    protected abstract R analyzeImage(ImageProxy image);

    @UiThread
    protected abstract void applyToUiAnalyzeImageResult(R result);
}