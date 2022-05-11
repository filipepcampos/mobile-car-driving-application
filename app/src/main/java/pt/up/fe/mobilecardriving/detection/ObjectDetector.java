package pt.up.fe.mobilecardriving.detection;

import android.graphics.Bitmap;

import java.util.List;

import pt.up.fe.mobilecardriving.model.DetectionObject;

public interface ObjectDetector {
    List<DetectionObject> evaluate(Bitmap imageBitmap);
}
