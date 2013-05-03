package dk.aau.cs.giraf.pictocreator.canvas.entity;

import java.util.ArrayList;

import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

public class FreehandEntity extends PrimitiveEntity {

	/**
	 * The base point is the null point for all subsequent points in the path.
	 * When a new point is added, it's offset from the base point is registered
	 * in lieu of its absolute position. This allows for simpler placement of
	 * the LineEntity at a slight computation cost.
	 */
	protected FloatPoint basePoint = new FloatPoint();
	
	protected ArrayList<FloatPoint> drawPoints = new ArrayList<FloatPoint>();

	/**
	 * Hitbox of the path. Recalculated after each new point has been added.
	 */
	protected RectF hitbox = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
	
	/**
	 * Create a new FreehandEntity with a specific stroke color. 
	 * @param strokeColor Hexadecimal color. Alpha channel: higher > more opaque.
	 */
	public FreehandEntity(int strokeColor) {
		super(0, 0, 0, 0, strokeColor, strokeColor);
	}

	@Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		if (drawPoints.size() <= 1) return; // Don't draw trivial.
		
		paint = new Paint(paint); // Copy.
		paint.setStyle(Style.STROKE);
		
		Log.i("FreehandEntity.drawWithPaint", String.format("Drawing with basePoint %s and first point %s", basePoint.toString(), drawPoints.get(0).toString()));
		
		Path p = new Path();
		FloatPoint tp;
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
		
		if (drawPoints.size() == 0) {
			basePoint = newPoint;
			drawPoints.add(new FloatPoint(0,0));
			calculateHitbox();
		} else {
			drawPoints.add(new FloatPoint(x-basePoint.x, y-basePoint.y));
			calculateHitbox();
		}
	}
	
	/**
	 * Add a new FloatPoint to the Entity.
	 * @param p The point to add.
	 */
	public void addPoint(FloatPoint p) {
		addPoint(p.x, p.y);
	}
	
	/**
	 * Resets the hitbox to unreasonable bounds and recalculates it based on
	 * *all* current points. It can get real expensive real fast. Use
	 * sparingly.
	 */
	protected void calculateHitbox() {
		hitbox = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
		
		for (FloatPoint p : drawPoints) {
			updateHitbox(p.x, p.y);
		}
		
		Log.i("FreehandEntity.calculateHitbox", String.format("New hitbox is %s,%s,%s,%s. Centered around %s this results in %s,%s,%s,%s.",
				hitbox.left, hitbox.top, hitbox.right, hitbox.bottom,
				basePoint.toString(),
				hitbox.left+basePoint.x, hitbox.top+basePoint.y, hitbox.right+basePoint.x, hitbox.bottom+basePoint.y));
	}
	
	protected void updateHitbox(float x, float y) {
		hitbox.left = Math.min(hitbox.left, x);
		hitbox.right = Math.max(hitbox.right, x);
		
		hitbox.top = Math.min(hitbox.top, y);
		hitbox.bottom = Math.max(hitbox.bottom, y);
	}
	
	/**
	 * Challenges the current hitbox with some point. The hitbox will be
	 * expanded to encompass the new point if it is outside the current bounds.
	 * @param p The point to challenge with.
	 */
	protected void updateHitbox(FloatPoint p) {
		updateHitbox(p.x, p.y);
	}
	
	@Override
	public float getHitboxLeft() {
		return hitbox.left + getX();
	}
	
	@Override
	public float getHitboxTop() {
		return hitbox.top + getY();
	}
	
	@Override
	public float getHitboxRight() {
		return hitbox.right + getX();
	}
	
	@Override
	public float getHitboxBottom() {
		return hitbox.bottom + getY();
	}
	
	@Override
	public void setX(float x) {
		if (basePoint == null) basePoint = new FloatPoint(x, 0);
		else basePoint.x = x;
	}
	
	@Override
	public void setY(float y) {
		if (basePoint == null) basePoint = new FloatPoint(0, y);
		else basePoint.y = y;
	}
	
	@Override
	public float getX() {
		return basePoint.x;
	}
	
	@Override
	public float getY() {
		return basePoint.y;
	}
	
}
