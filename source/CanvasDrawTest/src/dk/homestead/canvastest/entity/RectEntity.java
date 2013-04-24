package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.RectShape;

/**
 * Simple Entity. It is basically a visible rect.
 * @author lindhart
 *
 */
public class RectEntity extends PrimitiveEntity {

	float left, top, right, bottom;
	
	public RectEntity(float left, float top, float right, float bottom, int fillColor, int strokeColor) {
		super(left, top, fillColor, strokeColor);
		
		this.left = left;
		this.top = top;
		this.bottom = bottom;
		this.right = right;
	}

	 @Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		 canvas.drawRect(left, top, right, bottom, paint);
	}
}
