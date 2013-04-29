package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LineEntity extends PrimitiveEntity {

	protected float x1, y1, x2, y2;
	
	protected Paint paint;
	
	public LineEntity(float x1, float y1, float x2, float y2, int strokeColor) {
		super(
				Math.min(x1, x2), // Left
				Math.min(y1, y2), // Top
				Math.abs(x1 - x2), // Right
				Math.abs(y1 - y2), // Bottom
				0, strokeColor);
	}

	@Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		canvas.drawLine(getHitboxLeft(), getHitboxTop(), getHitboxRight(), getHitboxBottom(), paint);
	}
}
