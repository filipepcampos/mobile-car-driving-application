package pt.up.fe.mobilecardriving.detection.model;

import android.graphics.Bitmap;

import java.util.List;

import pt.up.fe.mobilecardriving.model.DetectionObject;

public interface ObjectDetector {
    int getDetectionHeight();

    int getDetectionWidth();

    int getNumClasses();

    EvaluationResult evaluate(Bitmap imageBitmap);
}
