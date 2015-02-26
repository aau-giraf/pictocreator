package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses.SerializablePaint;

/**
 * The LineEntity class represents a straight line, point A to point B. For
 * hitbox purposes, the LineEntity is considered a rectangle.
 * @author lindhart
 *
 */
public class LineEntity extends PrimitiveEntity {
	
	/**
	 * The vector of the line.
     * It is never changed but is used when the line is rotated and the centrum has to be found.
     * (Can be deleted, as its values can be assigned to the height and width).
	 */
	protected FloatPoint lineVector;

    /**
     * This defines how close the user has to click on the line to select it.
     * The lenght can be said to decide the "thickness" of the line that is clickable.
     */
    private final float JITTER_MAX = 20;

	/**
	 * Creates a new LineEntity going through specific point.s
	 * @param x1 First X-coordinate.
	 * @param y1 First Y-coordinate.
	 * @param x2 Second X-coordinate.
	 * @param y2 Second Y-coordinate.
	 * @param color Colour of the line.
	 */
	public LineEntity(float x1, float y1, float x2, float y2, int color) {
		super(color, color);
        lineVector = new FloatPoint(x2-x1, y2-y1);
		setX(x1);
		setY(y1);
	}

	@Override
	public void drawWithPaint(Canvas canvas, SerializablePaint paint) {
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        Path p = new Path();
        p.lineTo(0,0);
        p.lineTo(lineVector.x, lineVector.y);

        //drawLine ignores the style of the paint, which means the edges of the line are not rounded, so drawPath is used instead.
        canvas.drawPath(p, paint);
	}

    /**
     * We allow the height and width to be negative here since they are used to rotate around the center of an entity
     * together with the start point.
     * If we forced these to be positive, the center will be off when the startpoint has a larger y or x than
     * the endpoint.
     */
	@Override
	public float getHeight() { return lineVector.y;	}

	@Override
	public float getWidth() { return lineVector.x; }

    /**
     * Finds the leftmost point of the two endpoints in the line.
     * @return
     */
	@Override
    public float getHitboxLeft(){
        if (hitboxTopLeft == null)
            return Math.min(getX(), getX() + lineVector.x);
        else
            return hitboxTopLeft.x;
    }

    /**
     * Finds the rightmost point of the two endpoints in the line.
     * @return
     */
    @Override
    public float getHitboxRight(){
        if (hitboxTopLeft == null)
            return Math.min(getX(), getX() + lineVector.x) + Math.abs(getWidth());
        else
            return hitboxTopLeft.x + hitboxWidth;
    }

    /**
     * Finds the top point of the two endpoints in the line.
     * @return
     */
    @Override
    public float getHitboxTop(){
        if (hitboxTopLeft == null)
            return Math.min(getY(), getY() + lineVector.y);
        else
            return hitboxTopLeft.y;
    }

    /**
     * Finds the bottom point of the two endpoints in the line.
     * @return
     */
    @Override
    public float getHitboxBottom(){
        if (hitboxTopLeft == null)
            return Math.min(getY(),getY() + lineVector.y) + Math.abs(getHeight());
        else
            return hitboxTopLeft.y + hitboxHeight;
    }


    /**
     * Changes the hitbox so it tightly surrounds the line.
     * RotationMatrix is used to find the rotated start and endpoint, the smallest x and y of these points
     * defines the topleft corner of the hitbox.
     */
    @Override
    protected void updateHitbox(){
        FloatPoint pointA, pointB;

        pointA = rotationMatrix(lineVector.x/2.0f, lineVector.y/2.0f);
        pointB = rotationMatrix(-lineVector.x/2.0f, -lineVector.y/2.0f);

        hitboxTopLeft = new FloatPoint(Math.min(pointA.x, pointB.x), Math.min(pointA.y, pointB.y));
        hitboxWidth = (getCenter().x - hitboxTopLeft.x)*2.0f;
        hitboxHeight = (getCenter().y - hitboxTopLeft.y)*2.0f;
    }

    /**
     * Checks the distance from a point to the line and if the click is inside the hitbox.
     * The function distanceFromPointToVector finds the distance from a point to a line that goes through two points
     * therefore, the hitbox is used to limit this line as it would otherwise go to infinity.
     * @param x The X-coordinate of the point to check.
     * @param y The Y-coordinate of the point to check.
     * @return true if it's close to the line (proximity defined by the JITTER_MAX), false if it's not.
     */
    @Override
    public boolean collidesWithPoint(float x, float y) {
        return distanceFromPointToVector(x, y) < JITTER_MAX &&
                getHitboxTop() - JITTER_MAX < y &&
                getHitboxBottom() + JITTER_MAX > y &&
                getHitboxLeft() - JITTER_MAX < x &&
                getHitboxRight() + JITTER_MAX > x;
    }

    /**
     * Finds the distance from a point to a line that goes through two points.
     * A new linevector is calculated incase the line is rotated, and the new start and end point is calculated using this linevector and the centrum of the object.
     * This is done since we do not update the startpoint of the line or the initial linevector after a rotation.
     * Implementing an update to startpoint and the linevector could be very useful.
     * @param px the x-coordinate of the clicked point.
     * @param py the y-coordinate of the clicked point.
     * @return the distance from the line.
     */
    private float distanceFromPointToVector(float px, float py){
        FloatPoint newLineVector = new FloatPoint();
        newLineVector.x = (float)(lineVector.x*Math.cos(getRadiansAngle()) - lineVector.y*Math.sin(getRadiansAngle()));
        newLineVector.y = (float)(lineVector.x*Math.sin(getRadiansAngle()) + lineVector.y*Math.cos(getRadiansAngle()));

        FloatPoint start = new FloatPoint(getCenter().x - (newLineVector.x/2), getCenter().y - (newLineVector.y/2));
        FloatPoint end = new FloatPoint(getCenter().x + (newLineVector.x/2), getCenter().y + (newLineVector.y/2));

        return (float) ((Math.abs(newLineVector.y*px - newLineVector.x*py - start.x *end.y + end.x*start.y))/
                (Math.sqrt(Math.pow(newLineVector.x, 2) + Math.pow(newLineVector.y, 2))));
    }
}
