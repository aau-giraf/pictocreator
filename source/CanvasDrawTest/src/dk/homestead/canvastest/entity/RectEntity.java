package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.RectShape;
import dk.homestead.canvastest.Entity;
import dk.homestead.canvastest.graphic.ShapeGraphic;

/**
 * Simple Entity. It is basically a visible rect.
 * @author lindhart
 *
 */
public class RectEntity extends Entity {

	float left, top, right, bottom;
	Paint paint;
	
	public RectEntity(float left, float top, float right, float bottom, Paint paint) {
		this.left = left;
		this.top = top;
		this.bottom = bottom;
		this.right = right;
		this.paint = paint;
		
		RectShape sh = new RectShape();
		sh.resize(right-left, bottom-top);
		graphic = new ShapeGraphic(sh, paint);
		x = left;
		y = top;
	}

	 @Override
	public void draw(Canvas canvas) {
		 canvas.drawRect(left, top, right, bottom, paint);
	}
}
