package pt.up.fe.mobilecardriving.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import pt.up.fe.mobilecardriving.util.Position;
import pt.up.fe.mobilecardriving.warning.Warning;
import pt.up.fe.mobilecardriving.analysis.AnalysisResult;
import pt.up.fe.mobilecardriving.detection.DetectionObject;

public class ResultView extends View {
    private static final String[] MOTION_TEXT = new String[]{"STATIONARY", "SLOW", "MEDIUM", "HIGH"};
    private AnalysisResult result;

    private final Paint backgroundPaint;
    private final Paint textPaint;
    private final Paint[] detectionPaints;

    public ResultView(Context context) {
        this(context, null);
    }

    public ResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(Color.BLACK);
        this.backgroundPaint.setStrokeWidth(0);
        this.backgroundPaint.setAlpha(100);
        this.backgroundPaint.setStyle(Paint.Style.FILL);

        this.textPaint = new Paint();
        this.textPaint.setColor(Color.YELLOW);
        this.textPaint.setStrokeWidth(0);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setTextSize(32);


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

        // 'Vehicle', 'Pedestrian'
        // 'stop', 'Give way', 'prohibited', 'Prohibited overtaking', 'Allowed overtaking'
        this.detectionPaints = new Paint[]{
                carPaint,pedestrianPaint,
                signPaint,signPaint,signPaint,signPaint,signPaint
        };
    }

    public void setResult(AnalysisResult result) {
        this.result = result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.result != null) {
            this.drawDetections(canvas);
            this.drawInformation(canvas);
        }
    }

    private void drawInformation(Canvas canvas){
        canvas.drawText("Speed: " + ResultView.MOTION_TEXT[this.result.getMotionState().ordinal()], 600, 50, textPaint);
    }

    private void drawDetections(Canvas canvas){
        int imgHeight = getWidth() / 4;
        int yOffset = getHeight() - imgHeight;

        final Rect outOfFocusRect = new Rect(0,0, getWidth(), yOffset);
        canvas.drawRect(outOfFocusRect, backgroundPaint);

        int rectangleWidth = getWidth() / 32, rectangleHeight = yOffset / 8;
        List<DetectionObject> objects = this.result.getObjects();
        for (int i = 0; i < objects.size(); ++i){
            Position position = objects.get(i).getPosition();

            int rectangleX = position.getX() * rectangleWidth, rectangleY = yOffset + position.getY() * rectangleHeight;

            final Rect rectangle = new Rect(
                    rectangleX,
                    rectangleY,
                    rectangleX + rectangleWidth,
                    rectangleY + rectangleHeight
            );

            canvas.drawRect(rectangle, this.detectionPaints[objects.get(i).getClassIndex()]);
        }
    }
}
