package dk.aau.cs.giraf.pictocreator.canvas;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
 * @author Croc
 *
 */
public abstract class Entity implements Parcelable {
	/**
	 * X-coordinate of the Shape, measured by its left edge.
	 */
	private float x = 0;
	
	/**
	 * Y-coordinate of the Shape, measured by its top edge.
	 */
	private float y = 0;
	 
	/**
	 * Retrieve the shape's leftmost X-coordinate.
	 * @return The X-coordinate.
	 */
	public float getX() { return this.x; }
	
	/**
	 * Retrieve the shape's topmost Y-coordinate.
	 * @return The Y-coordinate.
	 */
	public float getY() { return this.y; }
	
	/**
	 * Sets the X-coordinate of this Entity. In most Entity subclasses, this
	 * is the top-left corner of the hitbox.
	 * @param x The new X-coordinate.
	 */
	public void setX(float x) { this.x = x; }
	
	/**
	 * Sets the Y-coordinate of this Entity. In most Entity subclasses, this
	 * is the top-left corner of the hitbox.
	 * @param y
	 */
	public void setY(float y) { this.y = y; }
	
	/**
	 * Width of the Entity's hitbox.
	 * Try Entity.getGraphic().getWidth() for the displayed width.
	 */
	protected float width = 0;
	
	/**
	 * Height of the Entity's hitbox.
	 * Try Entity.getGraphic().getHeight() for the displayed height.
	 */
	protected float height = 0;
	
	/**
	 * Angle of rotation, in degrees.
	 */
	protected float angle;
	
	/**
	 * Retrieves the declared height of the Entity.
	 * @return The height of the Entity.
	 */
	public float getHeight() { return this.height; }
	
	/**
	 * Retrieves the declared width of the Entity.
	 * @return The height of the Entity.
	 */
	public float getWidth() { return this.width; }
		
	/**
	 * Sets a new height for the Entity.
	 * @param height New height.
	 */
	public void setHeight(float height) { this.height = height; }
	
	/**
	 * Sets a new width for the Entity.
	 * @param width New width.
	 */
	public void setWidth(float width) { this.width = width; }
	
	/**
	 * Retrieve the angle of rotation for this Entity.
	 * @return The current angle of rotation.
	 */
	public float getAngle() { return angle; }
	
	/**
	 * Set the angle of rotation for this Entity.
	 * @param angle The new angle.
	 */
	public void setAngle(float angle) { this.angle = angle; }
	
	/**
	 * Rotates the Entity by a relative amount.
	 * @param value The amount to rotate by.
	 */
	public void rotateBy(float value) { this.angle += value; }
	
	/**
	 * Retrieves the center point of th Entity. The default implementation
	 * retrieves this as (x+width/2, y+height/2), but subclasses and rotation
	 * will affect the result. Please see the relevant documentation.
	 * @return The center point of the Entity.
	 */
	public FloatPoint getCenter() { return new FloatPoint(this.x + this.width/2, this.y + this.height/2); }
	
	/**
	 * Sets a new center for the Entity. For consistency, this method should
	 * be overriden in Entity subclasses where the center and rotation concepts
	 * are drastically changed.
	 * @param p New center point.
	 */
	public void setCenter(FloatPoint p) {
		setCenter(p.x, p.y);
	}
	
	/**
	 * Sets a new center for the Entity. For consistency, this method should
	 * be overriden in Entity subclasses where the center and rotation concepts
	 * are drastically changed.
	 * @param x New X-coordinate for the center.
	 * @param y New Y-coordinate for the center.
	 */
	public void setCenter(float x, float y) {
		setX(x-getWidth()/2);
		setY(y-getHeight()/2);
	}
	
	/**
	 * All Shape subtypes must override and implement the onDraw method. They
	 * are responsible for drawing themselves to the passed canvas.
	 * @param canvas
	 */
	public abstract void draw(Canvas canvas);
	
	/**
	 * Simple rectangular non-rotated collision detection at a specific point.
	 * @param x The X-coordinate of the point to check.
	 * @param y The Y-coordinate of the point to check.
	 * @return True if the point is within the hitbox, false otherwise.
	 */
	public boolean collidesWithPoint(float x, float y) {
		if (getAngle() != 0) { // Unrotate the point and compare it to a bare rect.
			double c = Math.cos(-getAngle());
			double s = Math.sin(-getAngle());
			
			Log.i("Entity.collidesWithPoint",
					String.format("Unrotating %s by %s degrees.", new FloatPoint(x, y).toString(), String.valueOf(-getAngle())));
			x = (float)(getX() + c * (x - getX()) - s * (y - getY()));
			y = (float)(getY() + s * (x - getX()) + c * (y - getY()));
			Log.i("Entity.collidesWithPoint",
					String.format("New points is %s.", new FloatPoint(x, y).toString()));
		}
		
		return (getHitboxLeft() <= x && x <= getHitboxRight()) &&
				(getHitboxTop() <= y && y <= getHitboxBottom());
	}
	
	/**
	 * Simple rectangular non-rotated collision detection at a specific point.
	 * @param p The point to check.
	 * @return True if the point is within the hitbox, false otherwise.
	 */
	public boolean collidesWithPoint(FloatPoint p) {
		return collidesWithPoint(p.x, p.y);
	}
	
	/**
	 * Retrieve the coordinates for the leftmost point in the hitbox.
	 * @return The X-coordinate of the leftmost hitbox edge.
	 */
	public float getHitboxLeft() {
		return getX();
	}
	
	/**
	 * Retrieve the coordinates for the rightmost point in the hitbox.
	 * @return The X-coordinate of the rightmost hitbox edge.
	 */
	public float getHitboxRight() {
		return getX()+getWidth();
	}
	
	/**
	 * Retrieve the coordinates for the topmost point in the hitbox.
	 * @return The Y-coordinate of the topmost hitbox edge.
	 */
	public float getHitboxTop() {
		return getY();
	}
	
	/**
	 * Retrieve the coordinates for the bottommost point in the hitbox.
	 * @return The Y-coordinate of the bottommost hitbox edge.
	 */
	public float getHitboxBottom() {
		return getY()+getHeight();
	}
	
	/**
	 * Creates a new Entity at (0,0) with no dimensions.
	 */
	public Entity() {
	}
	
	/**
	 * Attempts to unpack a basic Entity from a parcel. Expects the Parcel to
	 * have been packaged by Entity itself in a previous execution, or the
	 * Parcel to contain data in this order:
	 * <ul>
	 * <li>float:x-coordinate</li>
	 * <li>float:y-coordinate</li>
	 * <li>float:width</li>
	 * <li>float:height</li>
	 * <li>float:angle</li>
	 * </ul>
	 * @param in The parcel to unpack from.
	 */
	public Entity(Parcel in) {
		float[] vals = new float[5];
		in.readFloatArray(vals);
		setX(vals[0]);
		setY(vals[1]);
		setWidth(vals[2]);
		setHeight(vals[3]);
		setAngle(vals[4]);
	}
	
	/**
	 * Distance from the Entity's top-left corner to a specified point.
	 * @param x X-coordinate of the point to check against.
	 * @param y Y-coordinate of the point to check against.
	 * @return The distance between the two points.
	 */
	public float distanceToPoint(float x, float y) {
		return distanceBetweenPoints(getX(), getY(), x, y);
	}
	
	/**
	 * Distance from the Entity's top-left corner to a specified point.
	 * @param p Point to calculate against.
	 * @return The distance between the two points.
	 */
	public float distanceToPoint(FloatPoint p) {
		return distanceToPoint(p.x, p.y);
	}
	
	/**
	 * Calculates the distance between two Entity objects.
	 * <i><b>Warning:<b> it cannot be guaranteed that the results are
	 * consistent across Entity subtypes.</i>
	 * @param e The Entity to measure to.
	 * @return The distance between the Entity objects, measured by their
	 * top-left corners. 
	 */
	public float distanceToEntity(Entity e) {
		return distanceBetweenPoints(e.x, e.y, getX(), getY());
	}
	
	/**
	 * Support method. Calculate the distance between two specific points.
	 * @param p1 The first point.
	 * @param p2 The second point.
	 * @return The distance between the two points.
	 */
	public float distanceBetweenPoints(FloatPoint p1, FloatPoint p2) {
		return distanceBetweenPoints(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Support method. Calculate the distance between two specific points.
	 * @param x1 X-coordinate of the first point.
	 * @param y1 Y-coordinate of the first point.
	 * @param x2 X-coordinate of the second point.
	 * @param y2 Y-coordinate of the second point.
	 * @return The distance between the two points.
	 */
	public float distanceBetweenPoints(float x1, float y1, float x2, float y2) {
		return (float)Math.sqrt(Math.abs(x1-x2) + Math.abs(y1-y2));
	}
	
	/**
	 * Default parcel write for Entity. Stores location (x/y), size (width/height) and rotation (angle).
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloatArray(new float[]{getX(), getY(), getWidth(), getHeight(), getAngle()});
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	/**
	 * Calculates and returns the complete hitbox. Basically made of calls to
	 * the various getHitbox???? methods.
	 * @return A RectF of the Entity's hitbox bounds.
	 */
	public RectF getHitbox() {
		return new RectF(getHitboxLeft(), getHitboxTop(), getHitboxRight(), getHitboxBottom());
	}
	
}
