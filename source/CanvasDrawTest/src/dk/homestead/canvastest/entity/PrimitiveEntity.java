package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.shapes.RectShape;
import dk.homestead.canvastest.Entity;
import dk.homestead.canvastest.FloatPoint;

public abstract class PrimitiveEntity extends Entity {

	private Paint fillPaint = new Paint();
	private Paint strokePaint = new Paint();
	
	/**
	 * Creates a new PrimitiveEntity at a specific location and paint styles.
	 * @param x X-coordinate of the Entity.
	 * @param y Y-coordinate of the Entity.
	 * @param fillColor Pain used for the filling part of the primitive. It will be set to FILL style automatically.
	 * @param strokeColor
	 */
	public PrimitiveEntity(float x, float y, int fillColor, int strokeColor) {
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setColor(strokeColor);
		fillPaint.setStyle(Style.FILL);
		fillPaint.setColor(fillColor);
		
		setX(x);
		setY(y);
	}
	
	/**
	 * All ShapeEntity subclasses must implement drawWithPaint. ShapeEntity's
	 * own base draw method will call drawWithPaint twice; once for the fill
	 * Paint and once for the stroke Paint. The ShapeEntity subclass should
	 * merely draw itself with the passed paint as if it had otherwise been a
	 * regular draw call.
	 * @param canvas The Canvas to draw to. 
	 * @param paint The specific Paint that must be used for the draw call.
	 */
	public abstract void drawWithPaint(Canvas canvas, Paint paint);
	
	@Override
	public void draw(Canvas canvas) {
		drawWithPaint(canvas, fillPaint); // Fill
		drawWithPaint(canvas, strokePaint); // Stroke
	}
	
	public void setStroke(boolean enable){
		if (enable){
			
		}
	}
	
	/**
	 * Similar to draw
	 * @param canvas
	 */
	public void drawHighlighted(Canvas canvas, Paint hlPaint) {
		draw(canvas); // Perform normal draw operation.
		
		if (hlPaint == null) {
			hlPaint = new Paint(); // Special paint we use on a third drawing pass.
			hlPaint.setStyle(Style.STROKE);
			hlPaint.setPathEffect(new DashPathEffect(new float[]{10.0f,10.0f}, 0));
			hlPaint.setColor(0xFF000000);
			hlPaint.setStrokeWidth(2);
		}
		
		// Now highlight.
		RectShape rs = new RectShape();
		rs.resize(getWidth(), getHeight());
		canvas.translate(getX(), getY());
		canvas.rotate(getAngle());
		rs.draw(canvas, hlPaint);
		
		// TODO: Support rotated Entity.
		// canvas.drawRect(getHitboxLeft(), getHitboxTop(), getHitboxRight(), getHitboxBottom(), hlPaint);
	}
}
