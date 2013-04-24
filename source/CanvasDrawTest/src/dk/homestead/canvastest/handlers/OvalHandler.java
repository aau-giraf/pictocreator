package dk.homestead.canvastest.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import dk.homestead.canvastest.EntityGroup;
import dk.homestead.canvastest.entity.OvalEntity;
import dk.homestead.canvastest.entity.PrimitiveEntity;

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
		strokeColor = 0xFF000000;
		fillColor = 0x55FF0000;
	}
	
	@Override
	public PrimitiveEntity updateBuffer() {
		calcRectBounds();
		this.bufferedEntity = new OvalEntity(left, top, right, bottom, fillColor, strokeColor);
		return bufferedEntity;
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
		drawStack.addEntity(bufferedEntity);
	}
}
