package pt.up.fe.mobilecardriving.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import pt.up.fe.mobilecardriving.detection.analysis.warning.Warning;
import pt.up.fe.mobilecardriving.model.AnalysisResult;
import pt.up.fe.mobilecardriving.model.DetectionObject;

public class ResultView extends View {
    private static final String[] MOTION_TEXT = new String[]{"STATIONARY", "SLOW", "MEDIUM", "HIGH"};
    private AnalysisResult result;

    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint[] detectionPaints;

    public ResultView(Context context) {
        this(context, null);
    }

    public ResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setStrokeWidth(0);
        backgroundPaint.setAlpha(40);
        backgroundPaint.setStyle(Paint.Style.FILL);

        this.textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setStrokeWidth(0);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(32);


        Paint carPaint = new Paint();
        carPaint.setColor(Color.RED);
        carPaint.setAlpha(128);
        carPaint.setStyle(Paint.Style.FILL);

        Paint pedestrianPaint = new Paint();
        pedestrianPaint.setColor(Color.GREEN);
        pedestrianPaint.setAlpha(128);
        pedestrianPaint.setStyle(Paint.Style.FILL);

        Paint signPaint = new Paint();
        signPaint.setColor(Color.BLUE);
        signPaint.setAlpha(128);
        signPaint.setStyle(Paint.Style.FILL);

        // 'Car', 'Cyclist', 'Pedestrian', 'Person_sitting', 'Tram', 'Truck', 'Van'
        // 'stop', 'Give way', 'prohibited', 'Prohibited overtaking', 'Allowed overtaking'
        this.detectionPaints = new Paint[]{
                carPaint,carPaint,pedestrianPaint,pedestrianPaint,carPaint,carPaint,carPaint,
                signPaint,signPaint,signPaint,signPaint,signPaint
        };
    }

    public void setResult(AnalysisResult result) {
        this.result = result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: DELETE DEBUG CODE
        if (this.result != null) {
            this.drawInformation(canvas);
            this.drawDetections(canvas);
        }
        // TODO: SHOW THE INFORMATION OF ANALYSIS RESULT
    }

    private void drawInformation(Canvas canvas){
        canvas.drawText("Speed: " + ResultView.MOTION_TEXT[this.result.getMotionState().ordinal()], 600, 50, textPaint);

        List<Warning> warnings = this.result.getWarnings();
        canvas.drawText("Warnings:", 10, 50+40, textPaint);
        for (int i = 0; i < warnings.size(); ++i)
            canvas.drawText(warnings.get(i).getMessage(), 20, 50+40 * (i+2), textPaint);
    }

    private void drawDetections(Canvas canvas){
        Bitmap imgBitmap = this.result.getBitmap();

        int imgHeight = (int) getWidth() / 4;
        int yOffset = getHeight() - imgHeight;

        imgBitmap = Bitmap.createScaledBitmap(imgBitmap, getWidth(), imgHeight, false);
        Rect srcBitmapRect = new Rect(0, 0, imgBitmap.getWidth(), imgBitmap.getHeight());
        Rect destBitmapRect = new Rect(0, yOffset, imgBitmap.getWidth(), yOffset + imgHeight);
        canvas.drawBitmap(imgBitmap, srcBitmapRect, destBitmapRect, null);

        Rect outOfFocusRect = new Rect(0,0,getWidth(), yOffset);
        canvas.drawRect(outOfFocusRect, backgroundPaint);

        int width = getWidth();
        List<DetectionObject> objects = this.result.getObjects();
        for (int i = 0; i < objects.size(); ++i){
            Rect rectangle = objects.get(i).getRect();

            rectangle.left = (int) rectangle.left * (width/32);
            rectangle.right = (int) rectangle.right * (width/32);
            rectangle.top = yOffset + rectangle.top * (yOffset/8);
            rectangle.bottom = yOffset + rectangle.bottom * (yOffset/8);

            canvas.drawRect(rectangle, this.detectionPaints[objects.get(i).getClassIndex()]);
        }
    }
}
