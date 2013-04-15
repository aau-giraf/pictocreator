package dk.homestead.canvastest;

import android.graphics.Canvas;

public abstract class Graphic {

	/**
	 * The Graphic's width. You can typically trust this to cover the area that
	 * is drawn to by the draw call.
	 */
	public abstract float getWidth();
	
	/**
	 * The Graphic's height. You can typically trust this to cover the area that
	 * is drawn to by the draw call.
	 */
	public abstract float getHeight();
	
	/**
	 * Set a new width for the Graphic. Typically, this will *scale* the object
	 * rather than clip it.
	 * @param w
	 */
	public abstract void setWidth(float w);
	
	/**
	 * Set a new height for the Graphic. Typically, this will *scale the object
	 * and not clip it.
	 * @param h
	 */
	public abstract void setHeight(float h);
	
	public abstract void draw(Canvas canvas);
}
