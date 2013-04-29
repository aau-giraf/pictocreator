package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;

/**
 * Simple Entity. It is basically a visible rect.
 * @author lindhart
 *
 */
public class OvalEntity extends PrimitiveEntity {
	
	public OvalEntity(float left, float top, float right, float bottom, int fillColor, int strokeColor) {
		super(left, top, right-left, bottom-top, fillColor, strokeColor);
	}

	@Override		
	public void drawWithPaint(Canvas canvas, Paint paint) {
		// RectF area = new RectF(getHitboxLeft(), getHitboxTop(), getHitboxRight(), getHitboxBottom());
		// canvas.drawArc(area, 0.0f, 360.0f, true, paint);
	 
		OvalShape rs = new OvalShape();
		rs.resize(getWidth(), getHeight());
		 
		canvas.save();
		canvas.translate(getX(), getY());
		canvas.rotate(getAngle());
		rs.draw(canvas, paint);
		canvas.restore();
	}
}
