package dk.homestead.canvastest.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import dk.homestead.canvastest.EntityGroup;
import dk.homestead.canvastest.entity.LineEntity;

/**
 * The LineHandler class allows drawing straight lines. Simple stuff.
 * @author lindhart
 */
public class LineHandler extends ShapeHandler {

	public LineHandler(Bitmap buffersrc) {
		super(buffersrc);
		// Set up generic paint.
		strokePaint = new Paint();
		strokePaint.setARGB(255, 0, 0, 0); // Black!
		strokePaint.setStrokeWidth(3);
	}

	@Override
	public Bitmap getToolboxIcon(int size) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void drawBuffer(Canvas canvas) {
		Log.i("LineHandler.drawBuffer", "Drawing onto canvas.");
		
		canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, strokePaint);
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(new LineEntity(startPoint.x, startPoint.y, endPoint.x, endPoint.y, strokePaint));
	}

}
