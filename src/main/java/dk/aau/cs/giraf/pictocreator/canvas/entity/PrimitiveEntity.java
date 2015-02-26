package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.Paint.Style;

import java.io.Serializable;

import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses.SerializablePaint;


public abstract class PrimitiveEntity extends Entity implements Serializable {

    //fields
    /**
     * Colour are ints on purpose since Android Paint class is not serializable,
     * if you change these to Paint the colour cannot be saved in the drawStack.
     */
    private int fillPaint;
    private int strokePaint;
    private float strokeWidth;

    //setters
	public void setFillColor(int color) { fillPaint = color; }
	public void setStrokeColor(int color) { strokePaint = color; }
	public void setStrokeWidth(float width) { strokeWidth = width; }

    //getters
	public int getFillColor() { return fillPaint; }
	public int getStrokeColor() { return strokePaint; }
	public float getStrokeWidth() { return strokeWidth; }
	
	/**
	 * Creates a new PrimitiveEntity at a specific location, dimensions, and colours.
	 * @param x X-coordinate of the Entity.
	 * @param y Y-coordinate of the Entity.
	 * @param fillColor Paint used for the filling part of the primitive.
	 * @param strokeColor Paint used for the edges of an entity.
	 */
	public PrimitiveEntity(float x, float y, float w, float h, int fillColor, int strokeColor){
        //calls the other constructor
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
	 * All PrimitiveEntity subclasses must implement drawWithPaint. PrimitiveEntity's
	 * own base draw method will call drawWithPaint twice; once for the fill
	 * Paint and once for the stroke Paint. The ShapeEntity subclass should
	 * merely draw itself with the passed paint as if it had otherwise been a
	 * regular draw call.
	 * @param canvas The Canvas to draw to. 
	 * @param paint The specific Paint that must be used for the draw call.
	 */
	public abstract void drawWithPaint(Canvas canvas, SerializablePaint paint);


    /**
     * This method calls drawWithPaint to draw the actual entities with their colour.
     * Since we cannot save the colour as Paint, we create the SerializablePaint with
     * its colour and style. This is a dirty fix so we can load the drawStack from the database.
     * @param canvas The Canvas to draw upon.
     */
	@Override
	public void doDraw(Canvas canvas) {
        SerializablePaint tempFill = new SerializablePaint();
        tempFill.setStyle(Style.FILL);
        tempFill.setColor(fillPaint);

        SerializablePaint tempStroke = new SerializablePaint();
        tempStroke.setStyle(Style.STROKE);
        tempStroke.setStrokeWidth(strokeWidth);
        tempStroke.setColor(strokePaint);

        drawWithPaint(canvas, tempFill); // Fill
        drawWithPaint(canvas, tempStroke); // Stroke
	}
}
