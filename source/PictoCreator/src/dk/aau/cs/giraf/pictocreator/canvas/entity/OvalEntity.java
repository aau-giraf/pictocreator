package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.OvalShape;

/**
 * Simple Entity. It is basically a visible rect.
 * @author Croc
 *
 */
public class OvalEntity extends PrimitiveEntity {
	
	public OvalEntity(float left, float top, float right, float bottom, int fillColor, int strokeColor) {
		super(left, top, right-left, bottom-top, fillColor, strokeColor);
	}

	@Override		
	public void drawWithPaint(Canvas canvas, Paint paint) {
		OvalShape rs = new OvalShape();
		rs.resize(getWidth(), getHeight());
		 
		rs.draw(canvas, paint);
	}
}
