package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LineEntity extends PrimitiveEntity {

	protected float x1, y1, x2, y2;
	
	protected Paint paint;
	
	public LineEntity(float x1, float y1, float x2, float y2, Paint paint) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.paint = paint;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawLine(x1, y1, x2, y2, paint);
	}
}
