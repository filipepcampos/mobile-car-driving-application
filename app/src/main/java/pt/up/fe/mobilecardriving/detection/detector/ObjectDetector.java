package pt.up.fe.mobilecardriving.detection.detector;

import android.graphics.Bitmap;

import pt.up.fe.mobilecardriving.detection.EvaluationResult;

public interface ObjectDetector {
    int getDetectionHeight();

    int getDetectionWidth();

    int getNumClasses();

    EvaluationResult evaluate(Bitmap imageBitmap);
}
