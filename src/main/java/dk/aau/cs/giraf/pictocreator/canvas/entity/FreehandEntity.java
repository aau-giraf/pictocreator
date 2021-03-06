package dk.aau.cs.giraf.pictocreator.canvas.entity;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;

import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses.SerializablePaint;
import dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses.SerializableRectF;

/**
 * An Entity class representing a freehand drawing sequence. Freehand drawings
 * are registered with the available data points from touch events, typically.
 * However, since touch events are very scarce, we interpolate touch points
 * with simple lines. This gives the impression of freehand drawing in all but
 * the most minimal of TEpS (Touch Events per Second).
 * @author lindhart
 */
public class FreehandEntity extends PrimitiveEntity {

	/**
	 * The base point is the null point for all subsequent points in the path.
	 * When a new point is added, it's offset from the base point is registered
	 * in lieu of its absolute position. This allows for simpler placement of
	 * the LineEntity at a slight computation cost.
	 */
	protected FloatPoint basePoint = new FloatPoint();
	
	/**
	 * List of all drawPoints. Consider them ordered, so the draw sequence is
	 * intact.
	 */
	protected ArrayList<FloatPoint> drawPoints = new ArrayList<FloatPoint>();

    /**
     * This defines how close the user has to click on the line to select it.
     * The lenght can be said to decide the "thickness" of the line that is clickable.
     */
    private final float JITTER_MAX = 20;

	/**
	 * Hitbox of the path. Recalculated after each new point has been added.
	 */
	protected SerializableRectF hitbox = new SerializableRectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

    @Override
    public void setX(float x) {
        if (basePoint == null) basePoint = new FloatPoint(x, 0);
        else basePoint.x = x;
    }

    @Override
    public void setY(float y) {
        if (basePoint == null) basePoint = new FloatPoint(0, y);
        else basePoint.y = y;
    }

    @Override
    public float getX() {
        return basePoint.x;
    }

    @Override
    public float getY() {
        return basePoint.y;
    }
    
     /**
     * These getters find the distance from the basepoint to the center of the entity.
     * A not very intuitive fix that handles the rotation of freehand lines.
     * We multiply by two since entity rotation find the center of rotation by dividing
     * width and height by two.
     */
    @Override
    public float getWidth() {
        if(hitboxTopLeft != null){
            return (getCenter().x - basePoint.x)*2;
        }
        else{
            return super.getWidth();
        }
    }

    @Override
    public float getHeight() {
        if(hitboxTopLeft != null){
            return (getCenter().y - basePoint.y)*2;
        }
        else{
            return super.getHeight();
        }
    }

    /**
	 * Create a new FreehandEntity with a specific stroke color. 
	 * @param color Hexadecimal color. Alpha channel: higher > more opaque.
	 */
	public FreehandEntity(int color) {
        super(color, color);
        setX(0);
        setY(0);
    }

    @Override
    public void drawWithPaint(Canvas canvas, SerializablePaint paint) {
        if (drawPoints.size() <= 1) return; // Don't draw trivial.

        paint = new SerializablePaint(paint); // Copy.
        paint.setStyle(Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        Path p = new Path();
        for (FloatPoint floatPoint : drawPoints){
            p.lineTo(floatPoint.x, floatPoint.y);
        }

        canvas.drawPath(p, paint);
    }

	/**
	 * Add a new point to the Entity.
	 * @param x The X-coordinate.
	 * @param y The Y-coordinate.
	 */
	public void addPoint(float x, float y) {
		FloatPoint newPoint = new FloatPoint(x, y);
		
		if (drawPoints.size() == 0) {
			basePoint = newPoint;
			drawPoints.add(new FloatPoint(0,0));
			calculateHitbox();
		} else {
			drawPoints.add(new FloatPoint(x-basePoint.x, y-basePoint.y));
			calculateHitbox();
		}
	}
	
	/**
	 * Add a new FloatPoint to the Entity.
	 * @param p The point to add.
	 */
	public void addPoint(FloatPoint p) {
		addPoint(p.x, p.y);
	}
	
	/**
	 * Resets the hitbox to unreasonable bounds and recalculates it based on
	 * *all* current points. It can get real expensive real fast. Use
	 * sparingly.
	 */
	protected void calculateHitbox() {
		hitbox = new SerializableRectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
		
		for (FloatPoint p : drawPoints) {
			updateHitbox(p.x, p.y);
		}
	}
	
	/**
	 * Updates the current hitbox by comparing it with a specific point. This
	 * is used when a new point is added in lieu of recalculating the entire
	 * point collection. If the point is an outlier compared to the current
	 * hitbox, the hitbox is expanded accordingly.
	 * @param x The X-coordinate of the new point.
	 * @param y The Y-coordinate of the new point.
	 */
	protected void updateHitbox(float x, float y) {
		hitbox.left = Math.min(hitbox.left, x);
		hitbox.right = Math.max(hitbox.right, x);
		
		hitbox.top = Math.min(hitbox.top, y);
		hitbox.bottom = Math.max(hitbox.bottom, y);

        hitboxTopLeft = new FloatPoint(hitbox.left + basePoint.x, hitbox.top + basePoint.y);

        setWidth((getCenter().x - basePoint.x)*2);
        setHeight((getCenter().y- basePoint.y)*2);
	}
	
	/**
	 * Updates the current hitbox by comparing it with a specific point. This
	 * is used when a new point is added in lieu of recalculating the entire
	 * point collection. If the point is an outlier compared to the current
	 * hitbox, the hitbox is expanded accordingly.
	 * @param p The point to compare with.
	 */
	protected void updateHitbox(FloatPoint p) {
		updateHitbox(p.x, p.y);
	}

	@Override
	public float getHitboxLeft() {
        float returnVal = Float.MAX_VALUE;
        for(int i = 0; i < drawPoints.size(); i++){
            float temp = rotationMatrix(drawPoints.get(i).x + basePoint.x - getCenter().x,drawPoints.get(i).y + basePoint.y - getCenter().y).x;
            if(temp < returnVal)
                returnVal = temp;
        }

        return returnVal;
	}
	
	@Override
	public float getHitboxTop() {
        float returnVal = Float.MAX_VALUE;
        for(int i = 0; i < drawPoints.size(); i++){
            float temp = rotationMatrix(drawPoints.get(i).x + basePoint.x - getCenter().x,drawPoints.get(i).y + basePoint.y - getCenter().y).y;
            if(temp < returnVal)
                returnVal = temp;
        }

        return returnVal;
	}
	
	@Override
	public float getHitboxRight() {
        float returnVal = 0;
        for(int i = 0; i < drawPoints.size(); i++){
            float temp = rotationMatrix(drawPoints.get(i).x + basePoint.x - getCenter().x,drawPoints.get(i).y + basePoint.y - getCenter().y).x;
            if(temp > returnVal)
                returnVal = temp;
        }

        return returnVal;
	}
	
	@Override
	public float getHitboxBottom() {
        float returnVal = 0;
        for(int i = 0; i < drawPoints.size(); i++){
            float temp = rotationMatrix(drawPoints.get(i).x + basePoint.x - getCenter().x,drawPoints.get(i).y + basePoint.y - getCenter().y).y;
            if(temp > returnVal)
                returnVal = temp;
        }

        return returnVal;
	}

    @Override
    public SerializableRectF getHitbox() {
        calculateHitbox();
        return new SerializableRectF(getHitboxLeft(), getHitboxTop(), getHitboxRight(), getHitboxBottom());
    }

    @Override
    public FloatPoint getCenter() {
        return new FloatPoint(hitboxTopLeft.x + (hitbox.width()/2), hitboxTopLeft.y + (hitbox.height()/2));
    }

    /**
     * Checks whether a clicked point collides with this entity.
     * Same idea is used as in the line entity collision detection.
     * @param x The X-coordinate of the point to check.
     * @param y The Y-coordinate of the point to check.
     * @return true if the clicked point is within the freehand entity, false if not.
     */
    @Override
    public boolean collidesWithPoint(float x, float y) {
        for (int i = 0; i < drawPoints.size() - 1; i++){
            FloatPoint tempStart = new FloatPoint(drawPoints.get(i).x + getX() - getCenter().x, drawPoints.get(i).y + getY() - getCenter().y);
            FloatPoint tempEnd = new FloatPoint(drawPoints.get(i+1).x + getX() - getCenter().x, drawPoints.get(i+1).y + getY() - getCenter().y);
            tempStart = rotationMatrix(tempStart.x, tempStart.y);
            tempEnd = rotationMatrix(tempEnd.x, tempEnd.y);

            float top = Math.min(tempEnd.y, tempStart.y);
            float bottom = Math.max(tempEnd.y, tempStart.y);
            float left = Math.min(tempEnd.x,tempStart.x);
            float right = Math.max(tempEnd.x,tempStart.x);

            if(distanceFromPointToVector(x, y, tempStart, tempEnd) < JITTER_MAX &&
                top - JITTER_MAX < y &&
                bottom + JITTER_MAX > y &&
                left - JITTER_MAX < x &&
                right + JITTER_MAX > x)
                return true;
        }
        return false;
    }

    /**
     * Finds the distance from a point to a vector.
     * @param px x-coordinate of a point
     * @param py y-coordinate of a point
     * @param start start point of a vector
     * @param end end point of a vector
     * @return distance from point to vector
     */
    private float distanceFromPointToVector(float px, float py, FloatPoint start, FloatPoint end){
        FloatPoint lineVector = new FloatPoint(end.x - start.x, end.y - start.y);

        return (float) ((Math.abs(lineVector.y*px - lineVector.x*py - start.x *end.y + end.x*start.y))/
                (Math.sqrt(Math.pow(lineVector.x, 2) + Math.pow(lineVector.y, 2))));
    }
}
