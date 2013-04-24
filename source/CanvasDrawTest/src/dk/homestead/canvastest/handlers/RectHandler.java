package dk.homestead.canvastest.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import dk.homestead.canvastest.EntityGroup;
import dk.homestead.canvastest.entity.PrimitiveEntity;
import dk.homestead.canvastest.entity.RectEntity;

/**
 * The RectHandler class handles drawing rectangles on the drawing surface.
 * Drawing is started when the user places their finger on the board, marking
 * a corner of the rect. Further movement resizes and places the opposing
 * corner of the rectangle. Lifting the finger saves the rectangle on the
 * stack.
 * @author lindhart
 *
 */
public class RectHandler extends ShapeHandler {
	
	public RectHandler() {
		// Set up generic paint.
		strokeColor = 0xFF000000;
		fillColor = 0x55FF0000;
	}
	
	@Override
	public PrimitiveEntity updateBuffer() {
		calcRectBounds();
		this.bufferedEntity = new RectEntity(left, top, right, bottom, fillColor, strokeColor);
		return bufferedEntity;
	}
	
	/**
	 * Helper function that recalculates the bounds of the rectangle based on
	 * start and end points.
	 */
	protected void calcRectBounds(){
		top = Math.min(startPoint.y, endPoint.y);
		bottom = Math.max(startPoint.y, endPoint.y);
		left = Math.min(startPoint.x, endPoint.x);
		right = Math.max(startPoint.x, endPoint.x);
	}
	
	@Override
	public Bitmap getToolboxIcon(int size) {
		Bitmap ret = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(ret);
		c.rotate(30.0f, size/2, size/2);
		c.scale(0.5f, 0.5f, size/2, size/2);
		Paint p = new Paint();
		p.setColor(0xFF0000CC);
		p.setStyle(Style.FILL);
		c.drawRect(4, 4, size-4, size-4, p);
		p.setColor(0xFF000000);
		p.setStyle(Style.STROKE);
		c.drawRect(4, 4, size-4, size-4, p);
		return ret;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}
	
}
