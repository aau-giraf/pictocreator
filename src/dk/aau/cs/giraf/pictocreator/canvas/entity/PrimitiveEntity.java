package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import java.io.Serializable;

import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.SerializeClasses.SerializePaint;


public abstract class PrimitiveEntity extends Entity implements Serializable {

    private int fillPaint;
    private int strokePaint;
    private float strokeWidth;

	public void setFillColor(int color) { fillPaint = color; }
	public void setStrokeColor(int color) { strokePaint = color; }
	public void setStrokeWidth(float width) { strokeWidth = width; }

	public int getFillColor() { return fillPaint; }
	public int getStrokeColor() { return strokePaint; }
	public float getStrokeWidth() { return strokeWidth; }
	
	/**
	 * Creates a new PrimitiveEntity at a specific location and paint styles.
	 * @param x X-coordinate of the Entity.
	 * @param y Y-coordinate of the Entity.
	 * @param fillColor Pain used for the filling part of the primitive. It will be set to FILL style automatically.
	 * @param strokeColor
	 */
	public PrimitiveEntity(float x, float y, float w, float h, int fillColor, int strokeColor){
		this(fillColor, strokeColor);
        setX(x);
		setY(y);
		setWidth(w);
        setHeight(h);
	}
	
	public PrimitiveEntity(int fillColor, int strokeColor) {
        this.fillPaint = fillColor;
        this.strokePaint = strokeColor;
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
	public abstract void drawWithPaint(Canvas canvas, SerializePaint paint);
	
	@Override
	public void doDraw(Canvas canvas) {
        SerializePaint tempFill = new SerializePaint();
        tempFill.setStyle(Style.FILL);
        tempFill.setColor(fillPaint);

        SerializePaint tempStroke = new SerializePaint();
        tempStroke.setStyle(Style.STROKE);
        tempStroke.setStrokeWidth(strokeWidth);
        tempStroke.setColor(strokePaint);

        drawWithPaint(canvas, tempFill); // Fill
        drawWithPaint(canvas, tempStroke); // Stroke
	}

}
