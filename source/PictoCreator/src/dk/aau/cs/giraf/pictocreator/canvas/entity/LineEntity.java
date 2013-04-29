package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LineEntity extends PrimitiveEntity {

	protected float x1, y1, x2, y2;
	
	protected Paint paint;
	
	public LineEntity(float x1, float y1, float x2, float y2, int strokeColor) {
		super(
				x1, // Left
				y1, // Top
				Math.abs(x1 - x2), // Right
				Math.abs(y1 - y2), // Bottom
				0, strokeColor);
		
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		//canvas.drawLine(getHitboxLeft(), getHitboxTop(), getHitboxRight(), getHitboxBottom(), paint);
		canvas.drawLine(x1, y1, x2, y2, paint);
	}
}
