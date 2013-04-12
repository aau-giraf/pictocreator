package dk.homestead.canvastest.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera.Size;

public abstract class Shape {
	
	/**
	 * Stroke and colouring information is stored in this Paint object.
	 * Use the external interface to modify the important traits.
	 */
	protected Paint paint;
	
	/**
	 * X-coordinate of the Shape, measured by its left edge.
	 */
	protected float x;
	
	/**
	 * Y-coordinate of the Shape, measured by its top edge.
	 */
	protected float y;
	 
	/**
	 * Retrieve the shape's leftmost X-coordinate.
	 * @return
	 */
	public float getX() { return this.x; }
	
	/**
	 * Retrieve the shape's topmost Y-coordinate.
	 * @return
	 */
	public float getY() { return this.y; }
	
	protected float width;
	protected float height;
	
	public float getHeight() { return this.height; }
	public float getWidth() { return this.width; }
		
	public void setHeight(float height) { this.height = height; }
	public void setWidth(float width) { this.width = width; }

	/**
	 * Sets the colour of the Shape.
	 * @param a Alpha. 0 = transparent, 255 = opaque. No clue why this is never documented.
	 * @param r Red colour channel. 0 = no red, 255 = all red.
	 * @param g Green colour channel. 0 = no green, 255 = all green.
	 * @param b Blue colour channel. 0 = no blue, 255 = all blue.
	 */
	public void setARGB(int a, int r, int g, int b)
	{
		this.paint.setARGB(a, r, g, b);
	}
	
	public FloatPoint getCenter() { return new FloatPoint(this.x + this.width/2, this.y + this.height/2); }
	
	/**
	 * All Shape subtypes must override and implement the onDraw method. They
	 * are responsible for drawing themselves to the passed canvas.
	 * @param canvas
	 */
	public abstract void onDraw(Canvas canvas);
}
