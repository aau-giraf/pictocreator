package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Simple Entity. It is basically a visible rect.
 * @author lindhart
 *
 */
public class OvalEntity extends PrimitiveEntity {

	float left, top, right, bottom;
	
	public OvalEntity(float left, float top, float right, float bottom, int fillColor, int strokeColor) {
		super(left, top, fillColor, strokeColor);
		this.left = left;
		this.top = top;
		this.bottom = bottom;
		this.right = right;
	}

	 @Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		 RectF area = new RectF(left, top, right, bottom);
		 canvas.drawArc(area, 0.0f, 360.0f, true, paint);
	}
}
