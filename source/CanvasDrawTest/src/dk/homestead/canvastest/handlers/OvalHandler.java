package dk.homestead.canvastest.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import dk.homestead.canvastest.EntityGroup;
import dk.homestead.canvastest.entity.OvalEntity;

/**
 * The RectHandler class handles drawing rectangles on the drawing surface.
 * Drawing is started when the user places their finger on the board, marking
 * a corner of the rect. Further movement resizes and places the opposing
 * corner of the rectangle. Lifting the finger saves the rectangle on the
 * stack.
 * @author lindhart
 *
 */
public class OvalHandler extends ShapeHandler {

	public OvalHandler(Bitmap buffersrc) {
		super(buffersrc);
		// Set up generic paint.
		strokePaint = new Paint();
		strokePaint.setARGB(255, 0, 0, 0); // Black!
		strokePaint.setStyle(Style.STROKE);
	}
	
	@Override
	public void drawBuffer(Canvas canvas) {
		calcRectBounds();
		
		RectF oval = new RectF(left, top, right, bottom);
		
		canvas.drawArc(oval, 0.0f, 360.0f, true, strokePaint);	
	}
	
	@Override
	public Bitmap getToolboxIcon(int size) {
		Bitmap ret = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(ret);
		c.rotate(30.0f);
		Paint p = new Paint();
		p.setColor(0xFF000000);
		c.drawRect(4, 4, size-4, size-4, p);
		return ret;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(new OvalEntity(left, top, right, bottom, strokePaint));
	}
}
