package dk.homestead.canvastest.entity;

import java.util.ArrayList;

import dk.homestead.canvastest.FloatPoint;

import android.graphics.Canvas;
import android.graphics.Paint;

public class FreehandEntity extends PrimitiveEntity {
	
	protected ArrayList<FloatPoint> drawPoints = new ArrayList<FloatPoint>();
	
	/**
	 * Size of each drawn dot.
	 * TODO: Consider using dynamic radius when pressure is available.
	 */
	protected float radius;
	
	/**
	 * These points reference the extremes of the points added so far.
	 */
	protected FloatPoint leftPoint, topPoint, rightPoint, bottomPoint;
	
	public void setRadius(float r) { radius = r; }
	public float getRadius() { return radius; }
	
	/**
	 * Create a new FreehandEntity with a specific stroke color. 
	 * @param strokeColor Hexadecimal color. Alpha channel: higher > more opaque.
	 */
	public FreehandEntity(int strokeColor) {
		super(0, 0, 0, 0, strokeColor, strokeColor);
		setRadius(10);
	}

	@Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		for (FloatPoint p : drawPoints) {
			canvas.drawCircle(p.x, p.y, getRadius(), paint);
		}
	}

	/**
	 * Add a new point to the Entity.
	 * @param x The X-coordinate.
	 * @param y The Y-coordinate.
	 */
	public void addPoint(float x, float y) {
		FloatPoint newPoint = new FloatPoint(x, y);
		
		drawPoints.add(newPoint);
		
		// Special case for the first point.
		if (drawPoints.size() == 1) {
			leftPoint = rightPoint = topPoint = bottomPoint = newPoint;
		}
		else {
			// Possibly these can be chained instead, saving up to two calls.
			if (x < getHitboxLeft()) setX(x);
			if (x > getHitboxRight()) setWidth(x - getHitboxLeft());
			if (y < getHitboxTop()) setY(y);
			if (y > getHitboxBottom()) setHeight(y - getHitboxTop());
			
			/* Too expensive, methinks.
			if (x < leftPoint.x) leftPoint = newPoint;
			if (x > rightPoint.x) rightPoint = newPoint;
			if (y < topPoint.y) topPoint = newPoint;
			if (y > bottomPoint.y) bottomPoint = newPoint;
			*/
		}
	}
	
	/**
	 * Add a new FloatPoint to the Entity.
	 * @param p The point to add.
	 */
	public void addPoint(FloatPoint p) {
		addPoint(p.x, p.y);
	}

}
