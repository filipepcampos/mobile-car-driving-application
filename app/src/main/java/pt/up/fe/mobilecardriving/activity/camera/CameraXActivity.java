package pt.up.fe.mobilecardriving.activity.camera;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public abstract class CameraXActivity<R> extends BaseModuleActivity {
    private long lastAnalysisResultTime;
    private int analysisTime;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    public CameraXActivity() {
        this.lastAnalysisResultTime = 0;
        this.analysisTime = 0;
    }

    protected abstract int getContentViewLayoutId();

    protected abstract PreviewView getCameraPreviewView();

    protected abstract Executor getExecutor();

    @WorkerThread
    @Nullable
    protected abstract R analyzeImage(ImageProxy image);

    @UiThread
    protected abstract void applyToUiAnalyzeImageResult(R result);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(this::setupCameraXWrapper, getExecutor());

        startBackgroundThread();

        this.setupCameraXWrapper();
    }

    private void setupCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        final CameraSelector cameraSelector = this.setupCameraSelector();
        final Preview preview = this.setupCameraPreview();
        final ImageAnalysis imageAnalysis = this.setupImageAnalysis();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
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

    private ImageAnalysis setupImageAnalysis() {
        final ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                // TODO: enable the following line if RGBA output is needed.
                //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setTargetResolution(new Size(720, 720/4))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(this.getExecutor(), imageProxy -> {
            long currentTime = SystemClock.elapsedRealtime();
            if (currentTime - this.lastAnalysisResultTime >= this.analysisTime) {
                final R result = analyzeImage(imageProxy);
                if (result != null) {
                    this.lastAnalysisResultTime = currentTime;
                    runOnUiThread(() -> applyToUiAnalyzeImageResult(result));
                }
            }
            imageProxy.close();
        });

        return imageAnalysis;
    }

    private void setupCameraXWrapper() {
        try {
            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
            setupCameraX(cameraProvider);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setAnalysisTime(int analysisTime) {
        this.analysisTime = analysisTime;
    }
}