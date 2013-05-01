package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.entity.OvalEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;

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

	public OvalHandler() {
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
		
		c.scale(0.5f, 0.5f, size/2, size/2);
		
		Paint p = new Paint();
		RectF area = new RectF(0.0f, 0.0f, size, size);
		
		p.setColor(0xFFCC0000);
		p.setStyle(Style.FILL);
		
		c.drawArc(area, 0, 360, true, p);
		
		p.setColor(0xFF000000);
		p.setStyle(Style.STROKE);
		
		c.drawArc(area, 0, 360, true, p);
		
		return ret;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}
}
