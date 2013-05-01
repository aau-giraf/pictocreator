package dk.aau.cs.giraf.pictocreator.canvas;

import android.graphics.Canvas;

public abstract class Graphic {

	/**
	 * X-coordinate of the Graphic. Typically inherited from an Entity in some
	 * shape or form.
	 */
	private float x;
	
	/**
	 * Y-coordinate of the Graphic. Typically inherited from an Entity in some
	 * shape or form.
	 */
	private float y;
	
	/**
	 * Width of the Graphic.
	 */
	private float width;
	
	/**
	 * Height of the Graphic.
	 */
	private float height;
	
	public void setX(float x) { this.x = x; }
	
	public void setY(float y) { this.y = y; }
	
	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() { return x; }
	
	public float getY() { return y; }
	
	/**
	 * The Graphic's width. You can typically trust this to cover the area that
	 * is drawn to by the draw call.
	 */
	public float getWidth() { return width; }
	
	/**
	 * The Graphic's height. You can typically trust this to cover the area that
	 * is drawn to by the draw call.
	 */
	public float getHeight() { return height; }
	
	/**
	 * Set a new width for the Graphic. Typically, this will *scale* the object
	 * rather than clip it.
	 * @param w
	 */
	public void setWidth(float w) { width = w; }
	
	/**
	 * Set a new height for the Graphic. Typically, this will *scale the object
	 * and not clip it.
	 * @param h
	 */
	public void setHeight(float h) { height = h; }
	
	/**
	 * Draw the Graphic onto the target Canvas.
	 * @param canvas
	 */
	public abstract void draw(Canvas canvas);
}
