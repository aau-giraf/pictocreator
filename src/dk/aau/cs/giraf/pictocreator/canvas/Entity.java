package dk.aau.cs.giraf.pictocreator.canvas;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

import dk.aau.cs.giraf.pictocreator.canvas.SerializeClasses.SerializePaint;
import dk.aau.cs.giraf.pictocreator.canvas.SerializeClasses.SerializeRectF;

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
public abstract class Entity implements Parcelable, Serializable {
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

    protected FloatPoint hitboxTopLeft = null;
    protected float hitboxWidth = 0;
    protected float hitboxHeigth = 0;
	
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
	public void setHeight(float height) {
        if(height < 0)
            this.height = 0;
        else
            this.height = height; }
	
	/**
	 * Sets a new width for the Entity.
	 * @param width New width.
	 */
	public void setWidth(float width) {
        if (width < 0)
            this.width = 0;
        else
            this.width = width; }
	
	/**
	 * Retrieve the angle of rotation for this Entity.
	 * @return The current angle of rotation.
	 */
	public float getAngle() { return angle; }

    /**
     * Retrieve the angle of rotation for this Entity in radians.
     * @return The current angle of rotation in radians.
     */
    public float getRadiansAngle(){ return (float)Math.toRadians(getAngle()); }
	
	/**
	 * Set the angle of rotation for this Entity.
	 * @param angle The new angle.
	 */
	public void setAngle(float angle) {
        this.angle = (angle + 360) % 360;
    }

	/**
	 * Rotates the Entity by a relative amount.
	 * @param value The amount to rotate by.
	 */
	public void rotateBy(float value) {
        setAngle(this.angle + value);
    }
	
	/**
	 * Retrieves the center point of th Entity. The default implementation
	 * retrieves this as (x+width/2, y+height/2), but subclasses and rotation
	 * will affect the result. Please see the relevant documentation.
	 * @return The center point of the Entity.
	 */
	public FloatPoint getCenter() { return new FloatPoint(getX() + getWidth()/2, getY() + getHeight()/2); }
	
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
	 * The basic drawing method encompasses overriding doDraw with
	 * non-translating canvas actions. You are free, however, to override
	 * this draw method itself - in this case, you are responsible for
	 * correctly placing Canvas prior to usage.
	 * @param canvas The Canvas object to draw onto.
	 */
	public void draw(Canvas canvas) {
		int canvasLayers = canvas.getSaveCount();
		canvas.save();
		
		canvas.translate(getX(), getY());
		canvas.rotate(getAngle(), getWidth()/2, getHeight()/2);
		
		doDraw(canvas);
		
		canvas.restoreToCount(canvasLayers);
	}
	
	/**
	 * Method stub for post-translation drawing on the canvas. It is
	 * recommended that new Entity subclasses override this and simply draw
	 * to the canvas, allowing the primary Entity draw implementation to
	 * handle translating and rotating the canvas correctly.
	 * @param canvas The Canvas to draw upon.
	 */
	protected void doDraw(Canvas canvas) {}
	
	/**
	 * Simple rectangular non-rotated collision detection at a specific point.
	 * @param x The X-coordinate of the point to check.
	 * @param y The Y-coordinate of the point to check.
	 * @return True if the point is within the hitbox, false otherwise.
	 */
	public boolean collidesWithPoint(float x, float y) {
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
	public float getHitboxLeft(){
        if (hitboxTopLeft == null)
            return getX();
        else
            return hitboxTopLeft.x;
    }

	/**
	 * Retrieve the coordinates for the rightmost point in the hitbox.
	 * @return The X-coordinate of the rightmost hitbox edge.
	 */
	public float getHitboxRight(){
        if (hitboxTopLeft == null)
            return getX()+getWidth();
        else
            return hitboxTopLeft.x + hitboxWidth;
    }

	/**
	 * Retrieve the coordinates for the topmost point in the hitbox.
	 * @return The Y-coordinate of the topmost hitbox edge.
	 */
	public float getHitboxTop(){
        if (hitboxTopLeft == null)
            return getY();
        else
            return hitboxTopLeft.y;
    }

	/**
	 * Retrieve the coordinates for the bottommost point in the hitbox.
	 * @return The Y-coordinate of the bottommost hitbox edge.
	 */
	public float getHitboxBottom(){
        if (hitboxTopLeft == null)
            return getY()+getHeight();
        else
            return hitboxTopLeft.y + hitboxHeigth;
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
	public SerializeRectF getHitbox() {
        changeHitbox();
		return new SerializeRectF(getHitboxLeft(), getHitboxTop(), getHitboxRight(), getHitboxBottom());
	}

    /**
     * Changes the hitbox of the entity by using calculations of a rotation matrix.
     * It finds the top left corner of the hitbox and the width and height so it can calculate the rest of the corners
     */
    protected void changeHitbox(){
        FloatPoint one = rotationMatrix( -(getWidth()/2), -(getHeight()/2));
        FloatPoint two = rotationMatrix((getWidth()/2), -(getHeight()/2));
        FloatPoint three = rotationMatrix((getWidth()/2), (getHeight()/2));
        FloatPoint four = rotationMatrix( -(getWidth()/2), (getHeight()/2));

        hitboxTopLeft = new FloatPoint(findMin(one.x, two.x, three.x, four.x), findMin(one.y, two.y, three.y, four.y));
        hitboxWidth = (getCenter().x - hitboxTopLeft.x)*2;
        hitboxHeigth = (getCenter().y - hitboxTopLeft.y)*2;
    }

    /**
     * Finds the minimum value of the 4 parameters.
     * This could be done differently with 3 Math.min calls, but a function is written for readability.
     * @param f1
     * @param f2
     * @param f3
     * @param f4
     * @return the minimum value
     */
    private float findMin(float f1, float f2, float f3, float f4){
        float minimumValue = f1;

        if(f2 < minimumValue)
            minimumValue = f2;
        if(f3 < minimumValue)
            minimumValue = f3;
        if(f4 < minimumValue)
            minimumValue = f4;

        return minimumValue;
    }

    /**
     * Rotation matric formula used to rotate the corners of the entity and draw the hitbox around it.
     * @param x the x value of the corner.
     * @param y the y value of the corner.
     * @return a rotated point.
     */
    protected FloatPoint rotationMatrix(float x, float y){
        return new FloatPoint(
                (float)(x*Math.cos(getRadiansAngle()) - y*Math.sin(getRadiansAngle())) + getCenter().x,
                (float)(x*Math.sin(getRadiansAngle()) + y*Math.cos(getRadiansAngle())) + getCenter().y);

    }
}
