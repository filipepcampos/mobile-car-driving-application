package pt.up.fe.mobilecardriving.detection.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.mobilecardriving.model.DetectionObject;

public class TestModel implements ObjectDetector {
    public List<DetectionObject> evaluate(Bitmap imageBitmap){
        List<DetectionObject> result = new ArrayList<>();

        // TODO: REPLACE MOCK DATA BY EVALUATION METHOD
        result.add(new DetectionObject(0, 0f, null));
        result.add(new DetectionObject(1, 0f, null));
        result.add(new DetectionObject(2, 0f, null));

        return result;
    }
}
