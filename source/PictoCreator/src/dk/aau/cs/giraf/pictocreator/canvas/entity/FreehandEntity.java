package dk.aau.cs.giraf.pictocreator.canvas.entity;

import java.util.ArrayList;

import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;

public class FreehandEntity extends PrimitiveEntity {
	
	protected ArrayList<FloatPoint> drawPoints = new ArrayList<FloatPoint>();

	/**
	 * Create a new FreehandEntity with a specific stroke color. 
	 * @param strokeColor Hexadecimal color. Alpha channel: higher > more opaque.
	 */
	public FreehandEntity(int strokeColor) {
		super(0, 0, 0, 0, strokeColor, strokeColor);
	}

	@Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		if (drawPoints.size() <= 1) return;
		
		paint = new Paint(paint); // Copy.
		paint.setStyle(Style.STROKE);
		
		Path p = new Path();
		FloatPoint tp = drawPoints.get(0);
		p.moveTo(tp.x, tp.y);
		for (int i = 1; i < drawPoints.size(); i++) {
			tp = drawPoints.get(i);
			p.lineTo(tp.x, tp.y);
		}
		
		canvas.drawPath(p, paint);
	}

	/**
	 * Add a new point to the Entity.
	 * @param x The X-coordinate.
	 * @param y The Y-coordinate.
	 */
	public void addPoint(float x, float y) {
		FloatPoint newPoint = new FloatPoint(x, y);
		
		drawPoints.add(newPoint);
	}
	
	/**
	 * Add a new FloatPoint to the Entity.
	 * @param p The point to add.
	 */
	public void addPoint(FloatPoint p) {
		addPoint(p.x, p.y);
	}

}
