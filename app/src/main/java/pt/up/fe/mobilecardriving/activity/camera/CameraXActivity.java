package pt.up.fe.mobilecardriving.activity.camera;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
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

        int width = this.getCameraPreviewView().getWidth();
        int height = width / 4;
        ViewPort viewPort = new ViewPort.Builder(
                new Rational(width, height),
                Surface.ROTATION_90)
                .setScaleType(ViewPort.FILL_END)
                .build();

        UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .setViewPort(viewPort)
                .build();
        cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup);
    }

    private Preview setupCameraPreview() {
        final Preview preview = new Preview.Builder()
                .setTargetResolution(new Size(1024, 256))
                .build();
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
                .setTargetResolution(new Size(1024, 256))
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