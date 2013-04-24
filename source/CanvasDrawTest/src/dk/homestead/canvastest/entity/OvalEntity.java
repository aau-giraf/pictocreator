package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RectShape;

/**
 * Simple Entity. It is basically a visible rect.
 * @author lindhart
 *
 */
public class OvalEntity extends PrimitiveEntity {

	float left, top, right, bottom;
	
	public OvalEntity(float left, float top, float right, float bottom, Paint paint) {
		this.left = left;
		this.top = top;
		this.bottom = bottom;
		this.right = right;
		this.paint = paint;
		
		RectShape sh = new RectShape();
		sh.resize(right-left, bottom-top);
		x = left;
		y = top;
	}

	 @Override
	public void draw(Canvas canvas) {
		 RectF area = new RectF(left, top, right, bottom);
		 canvas.drawArc(area, 0.0f, 360.0f, true, paint);
	}
}
