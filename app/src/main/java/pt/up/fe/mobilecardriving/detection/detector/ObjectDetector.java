package pt.up.fe.mobilecardriving.detection.detector;

import android.graphics.Bitmap;

import pt.up.fe.mobilecardriving.detection.EvaluationResult;

public interface ObjectDetector {
    int getDetectionHeight();

    int getDetectionWidth();

    EvaluationResult evaluate(Bitmap imageBitmap);
}
