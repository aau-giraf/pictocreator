package dk.aau.cs.giraf.pictocreator.canvas;

/**
 * The AngleRectF is an improvement upon the basic RectF in the standard
 * framework. It uses a more complex method to model the rotated rectangle
 * as two vectors (TL-BL and TL-TR). Since rectangles are otherwise uniform,
 * This is perhaps the most elegant way to represent rectangles since almost
 * every property can be derived from these two properties (and an offset).
 * @author Croc
 *
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

	public void setTopRightVector(float x, float y) {
		this.topRightVector.x = x;
		this.topRightVector.y = y;
	}
	
	/**
	 * @param topRightVector the topRightVector to set
	 */
	public void setTopRightVector(FloatPoint topRightVector) {
		this.topRightVector = topRightVector;
	}

	/**
	 * @return the bottomLeftVector
	 */
	public FloatPoint getBottomLeftVector() {
		return bottomLeftVector;
	}

	public void setBottomLeftVector(float x, float y) {
		this.bottomLeftVector.x = x;
		this.bottomLeftVector.y = y;
	}
	
	/**
	 * @param bottomLeftVector the bottomLeftVector to set
	 */
	public void setBottomLeftVector(FloatPoint bottomLeftVector) {
		this.bottomLeftVector = bottomLeftVector;
	}

	/**
	 * @return the offset
	 */
	public FloatPoint getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(FloatPoint offset) {
		this.offset = offset;
	}
	
	public void setOffset(float x, float y) {
		this.offset.x = x;
		this.offset.y = y;
	}
	
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
			while (angle > 360) angle -= 360;
		
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
	 * 
	 * @param angle
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public float getAngle() {
		return angle;
	}
	
}
