package dk.aau.cs.giraf.pictocreator.canvas;

/**
 * The AngleRectF is an improvement upon the basic RectF in the standard
 * framework. It uses a more complex method to model the rotated rectangle
 * as two vectors (TL-BL and TL-TR). Since rectangles are otherwise uniform,
 * This is perhaps the most elegant way to represent rectangles since almost
 * every property can be derived from these two properties (and an offset).
 * @author Croc
 *
 * This class is not used, but could be implemented instead of RectF. Was discovered late in our semester so we did not implement it. - SW608F14
 */
public class AngleRectF {

	/**
	 * Vector going from the top-left corner to the top-right corner.
	 */
	private FloatPoint topRightVector = new FloatPoint();
	
	/**
	 * Vector going from the top-left corner to the bottom-left corner.
	 */
	private FloatPoint bottomLeftVector = new FloatPoint();
	
	/**
	 * The offset of the rectangle. Use this to actually place it in the plane,
	 * rather than having dimensions but no location.
	 * Defaults to (0,0).
	 */
	private FloatPoint offset = new FloatPoint();
	
	/* Disabled until further notice. I don't want to mess my stuff with it.
	public enum PivotPoint {TOP_LEFT, CENTER};
	
	private PivotPoint pivotPoint = PivotPoint.TOP_LEFT;
	*/
	
	/**
	 * Angle of the rectangle. While not losing all of its meaning
	 */
	private float angle;

	/**
	 * @return the topRightVector
	 */
	public FloatPoint getTopRightVector() {
		return topRightVector;
	}

	/**
	 * Sets the top-right vector of the rectangle. That is, the vector going
	 * from the top-left corner to the top-right corner.
	 * @param x New X-coordinate.
	 * @param y New Y-coordinate.
	 */
	public void setTopRightVector(float x, float y) {
		this.topRightVector.x = x;
		this.topRightVector.y = y;
	}
	
	/**
	 * Sets the top-right vector of the rectangle. That is, the vector going
	 * from the top-left corner to the top-right corner.
	 * @param topRightVector The new point of the vector.
	 */
	public void setTopRightVector(FloatPoint topRightVector) {
		this.topRightVector = topRightVector;
	}

	/**
	 * @return the bottomLeftVector. That is, the vector going from the
	 * top-left corner to the bottom-left corner.
	 */
	public FloatPoint getBottomLeftVector() {
		return bottomLeftVector;
	}

	/**
	 * Sets a new bottom-left corner. That is, the vector going from the
	 * top-left to the bottom-left corner.
	 * @param x X-coordinate of the new point.
	 * @param y Y-coordinate of the new point.
	 */
	public void setBottomLeftVector(float x, float y) {
		this.bottomLeftVector.x = x;
		this.bottomLeftVector.y = y;
	}
	
	/**
	 * Sets a new bottom-left corner. That is, the vector going from the
	 * top-left to the bottom-left corner.
	 * @param bottomLeftVector New point of the vector.
	 */
	public void setBottomLeftVector(FloatPoint bottomLeftVector) {
		this.bottomLeftVector = bottomLeftVector;
	}

	/**
	 * Retrieves the offset of the rectangle. This is the starting point of the
	 * top-right and bottom-left vectors.
	 * @return The offset itself.
	 */
	public FloatPoint getOffset() {
		return offset;
	}

	/**
	 * Sets the offset of the rectangle. This is the starting point of the
	 * top-right and bottom-left rectangles.
	 * @param offset The new offset point.
	 */
	public void setOffset(FloatPoint offset) {
		this.offset = offset;
	}
	
	/**
	 * Sets the offset of the rectangle. This is the starting point of the
	 * top-right and bottom-left rectangles.
	 * @param x X-coordinate of the new offset.
	 * @param y Y-coordinate of the new offset.
	 */
	public void setOffset(float x, float y) {
		this.offset.x = x;
		this.offset.y = y;
	}
	
	/**
	 * Creates a new AngleRectF as a point in (0,0) with vectors (0,0) and
	 * (0,0).
	 */
	public AngleRectF() {
	}
	
	/**
	 * Retrieves the center point of the rectangle, either relatively (from
	 * (0,0)) or absolutely (from the offset).
	 * @param absolute If true, the absolute center position is returned. If
	 * false, the offset from the topleft corner is used.
	 * @return A new FloatPoint object pointing to the center of the rectangle.
	 */
	public FloatPoint getAbsoluteCenter(boolean absolute) {
		return new FloatPoint(
				(topRightVector.x + bottomLeftVector.x) / 2 + (absolute ? offset.x : 0),
				(topRightVector.y + bottomLeftVector.y) / 2 + (absolute ? offset.y : 0));
	}
	
	/**
	 * Rotates the 
	 * @param amount
	 */
	public void rotate(float amount) {
        this.angle += amount;
        this.angle %= 360;

        rotatePoint(this.topRightVector, amount, getOffset());
        rotatePoint(this.bottomLeftVector, amount, getOffset());
	}
	
	private static FloatPoint rotatePoint(FloatPoint p, float amount, FloatPoint pivot) {
		double c = Math.cos(amount);
		double s = Math.sin(amount);
		
		return new FloatPoint(
				(float)(pivot.x + c * (p.x - pivot.x) - s * (p.y - pivot.y)),
				(float)(pivot.y + s * (p.x - pivot.x) + c * (p.y - pivot.y))
				);
	}
	
	/**
	 * Set a new absolute angle of rotation for the rectangle.
	 * @param angle The new angle, in degrees.
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	/**
	 * Retrieves the current angle of the rectangle, in degrees.
	 * @return The current angle, in degrees.
	 */
	public float getAngle() {
		return angle;
	}
	
}
