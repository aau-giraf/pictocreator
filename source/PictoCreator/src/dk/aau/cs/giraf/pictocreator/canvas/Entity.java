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
 * @author lindhart
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
	 * @return
	 */
	public float getX() { return this.x; }
	
	/**
	 * Retrieve the shape's topmost Y-coordinate.
	 * @return
	 */
	public float getY() { return this.y; }
	
	public void setX(float x) { this.x = x; }
	
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
	
	public float getHeight() { return this.height; }
	public float getWidth() { return this.width; }
		
	public void setHeight(float height) { this.height = height; }
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
	
	public FloatPoint getCenter() { return new FloatPoint(this.x + this.width/2, this.y + this.height/2); }
	
	public void setCenter(FloatPoint p) {
		setCenter(p.x, p.y);
	}
	
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
	 * @return
	 */
	public float getHitboxLeft() {
		return getX();
	}
	
	/**
	 * Retrieve the coordinates for the rightmost point in the hitbox.
	 * @return
	 */
	public float getHitboxRight() {
		return getX()+getWidth();
	}
	
	/**
	 * Retrieve the coordinates for the topmost point in the hitbox.
	 * @return
	 */
	public float getHitboxTop() {
		return getY();
	}
	
	/**
	 * Retrieve the coordinates for the bottommost point in the hitbox.
	 * @return
	 */
	public float getHitboxBottom() {
		return getY()+getHeight();
	}
	
	public Entity() {
	}
	
	public Entity(Parcel in) {
		float[] vals = new float[5];
		in.readFloatArray(vals);
		setX(vals[0]);
		setY(vals[1]);
		setWidth(vals[2]);
		setHeight(vals[3]);
		setAngle(vals[4]);
	}
	
	public float distanceToPoint(float x, float y) {
		return distanceBetweenPoints(getX(), getY(), x, y);
	}
	
	public float distanceToPoint(FloatPoint p) {
		return distanceToPoint(p.x, p.y);
	}
	
	public float distanceToEntity(Entity e) {
		return distanceBetweenPoints(e.x, e.y, getX(), getY());
	}
	
	public float distanceBetweenPoints(FloatPoint p1, FloatPoint p2) {
		return distanceBetweenPoints(p1.x, p1.y, p2.x, p2.y);
	}

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
