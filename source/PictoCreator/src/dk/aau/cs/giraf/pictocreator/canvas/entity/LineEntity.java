package dk.aau.cs.giraf.pictocreator.canvas.entity;

import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LineEntity extends PrimitiveEntity {
	
	protected FloatPoint endPoint;
	
	protected Paint paint = new Paint();
	
	public LineEntity(float x1, float y1, float x2, float y2, int strokeColor) {
		super(0, strokeColor);
		
		setX(x1);
		setY(y1);
		
		endPoint = new FloatPoint(x2-x1, y2-y1);
	}
	
	@Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		canvas.drawLine(0, 0, endPoint.x, endPoint.y, paint);
	}

	@Override
	public float getHeight() {
		return Math.abs(endPoint.y);
	}
	
	@Override
	public float getWidth() {
		return Math.abs(endPoint.x);
	}
	
	@Override
	public float getHitboxLeft() {
		return getX() + (endPoint.x < 0 ? endPoint.x : 0);
	}
	
	@Override
	public float getHitboxTop() {
		return getY() + (endPoint.y < 0 ? endPoint.y : 0);
	}
	
	@Override
	public float getHitboxRight() {
		return getX() + (endPoint.x > 0 ? endPoint.x : 0);
	}
	
	@Override
	public float getHitboxBottom() {
		return getY() + (endPoint.y > 0 ? endPoint.y : 0);
	}
	
}
