package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import dk.aau.cs.giraf.pictocreator.canvas.Entity;;

public abstract class PrimitiveEntity extends Entity {

	private Paint fillPaint = new Paint();
	private Paint strokePaint = new Paint();
	
	public void setFillColor(int color) { fillPaint.setColor(color); }
	public void setStrokeColor(int color) { strokePaint.setColor(color); }
	
	/**
	 * Creates a new PrimitiveEntity at a specific location and paint styles.
	 * @param x X-coordinate of the Entity.
	 * @param y Y-coordinate of the Entity.
	 * @param fillColor Pain used for the filling part of the primitive. It will be set to FILL style automatically.
	 * @param strokeColor
	 */
	public PrimitiveEntity(float x, float y, float w, float h, int fillColor, int strokeColor) {
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setColor(strokeColor);
		fillPaint.setStyle(Style.FILL);
		fillPaint.setColor(fillColor);
		
		setX(x);
		setY(y);
		setWidth(w);
		setHeight(h);
	}
	
	public PrimitiveEntity(int fillColor, int strokeColor) {
		strokePaint.setStyle(Style.STROKE);
		fillPaint.setStyle(Style.FILL);
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
		int canvasLayers = canvas.getSaveCount();
		canvas.save();
		
		canvas.translate(getX(), getY());
		canvas.rotate(getAngle(), getWidth()/2, getHeight()/2);
		
		drawWithPaint(canvas, fillPaint); // Fill
		drawWithPaint(canvas, strokePaint); // Stroke
		
		canvas.restoreToCount(canvasLayers);
	}

}
