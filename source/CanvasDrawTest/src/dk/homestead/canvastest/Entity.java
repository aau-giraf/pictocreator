package dk.homestead.canvastest;

import android.graphics.Canvas;

/**
 * Custom-rolled Entity class for Drawables. One major drawback I find with the
 * Android Framework implementation of Canvas and Drawables is the way you 
 * handle generic drawing. Canvas is very much like a camera, or frame, you move
 * over the underlying Bitmap, deciding where to draw. I much prefer the
 * metaphor that the canvas is a direct representation of the bitmap, and
 * objects are drawn to it at specific locations. We emulate this behaviour
 * with Entity. It wraps a Drawable (its Graphic) and performs the necessary
 * transformations to a canvas before passing the draw call on to the Graphic.
 * If you hadn't noticed, the namin convention and general design is inspired by
 * ChevyRay's Flashpunk.
 * @author lindhart
 *
 */
public abstract class Entity {
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
	
	/**
	 * Width of the Entity's hitbox.
	 * Try Entity.getGraphic().getWidth() for the displayed width.
	 */
	protected float width;
	
	/**
	 * Height of the Entity's hitbox.
	 * Try Entity.getGraphic().getHeight() for the displayed height.
	 */
	protected float height;
	
	public float getHeight() { return this.height; }
	public float getWidth() { return this.width; }
		
	public void setHeight(float height) { this.height = height; }
	public void setWidth(float width) { this.width = width; }
	
	public FloatPoint getCenter() { return new FloatPoint(this.x + this.width/2, this.y + this.height/2); }
	
	/**
	 * The Graphic object that is the visible element of the Entity. Should be
	 * one of DrawableGraphic (supporting Drawables) or ShapeGraphic
	 * (supporting Android Shape objects) - or your own! See the Graphic
	 * superclass for more details.
	 */
	protected Graphic graphic;
	
	/**
	 * Retrieve the current Graphic object (if any).
	 * @return The Entity's current Graphic object.
	 */
	public Graphic getGraphic() { return graphic; }
	
	/**
	 * Sets the Entity's Graphic object.
	 * @param graphic The new Graphic.
	 */
	public void setGraphic(Graphic graphic) { this.graphic = graphic; }
	
	/**
	 * All Shape subtypes must override and implement the onDraw method. They
	 * are responsible for drawing themselves to the passed canvas.
	 * @param canvas
	 */
	public void draw(Canvas canvas){
		// Store the current number of saves. In case the graphic does not
		// clean up after itself, we can restore the correct number of times.
		int layer = canvas.getSaveCount();
		canvas.save(); // Save a layer so we somewhat avoid the graphic messing with translations.
		// The graphic has inherited location/size data from the Entity (where relevant), so we
		// simply instruct it to draw.
		graphic.draw(canvas);
		// Restore the canvas to its original setting.
		canvas.restoreToCount(layer);
	}
}
