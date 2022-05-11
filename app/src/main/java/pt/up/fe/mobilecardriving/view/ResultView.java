package pt.up.fe.mobilecardriving.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import pt.up.fe.mobilecardriving.model.AnalysisResult;
import pt.up.fe.mobilecardriving.model.DetectionObject;

public class ResultView extends View {
    private AnalysisResult result;

    private Paint textPaint;

    public ResultView(Context context) {
        super(context);
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setStrokeWidth(0);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(32);
    }

    public ResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setResult(AnalysisResult result) {
        this.result = result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: DELETE DEBUG CODE
        if (this.result != null) {
            List<DetectionObject> objects = this.result.getObjects();
            canvas.drawText("Objects Detected:", 10, 50, textPaint);
            for (int i = 0; i < objects.size(); ++i)
                canvas.drawText(objects.get(i).toString(), 20, 50+40 * (i+1), textPaint);
        }
        // TODO: SHOW THE INFORMATION OF ANALYSIS RESULT
    }
}
