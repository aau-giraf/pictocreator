package dk.aau.cs.giraf.pictocreator.canvas;

import android.util.Log;

/**
 * Simpler Point implementation that allows floats. Fuckin' odd that they use
 * floats for coordinates but integers for point coordinates.
 * @author Croc
 *
 */
public class FloatPoint {

	/**
	 * X-coordinate of the point.
	 */
	public float x;
	
	/**
	 * Y-coordinate of the point.
	 */
	public float y;
	
	/**
	 * Creates a new FloatPoint at (0,0).
	 */
	public FloatPoint() {
		this.x = this.y = 0;
	}
	
	/**
	 * Creats a new FloatPoint with specific coordinates.
	 * @param x X-coordinate.
	 * @param y Y-coordinate.
	 */
	public FloatPoint(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a new FloatPoint at the same coordinates as another FloatPoint.
	 * @param src The source to copy coordinates from.
	 */
	public FloatPoint(FloatPoint src) {
		x = src.x;
		y = src.y;
	}

	/**
	 * Calculates the distance from this FloatPoint to another FloatPoint.
	 * @param to The other FloatPoint.
	 * @return Distance between the points.
	 */
	public float distance(FloatPoint to){
		float result = (float)Math.sqrt(Math.abs(x-to.x)+Math.abs(y-to.y));
		Log.i("FloatPoint.distance", "Distance between "+toString()+" and "+to.toString()+" is "+String.valueOf(result));
		return result;
	}
	
	/**
	 * Returns a human-readable coordinate in the form "(x,y)".
	 */
	public String toString(){
		// return "("+String.valueOf(x)+","+String.valueOf(y)+")";
		return String.format("(%s,%s)", x, y);
	}
	
	/**
	 * Rotate the point around a pivot.
	 * @param amount The amount of degrees to rotate.
	 * @param pivot The pivot point.
	 * @return Returns self - good for chaining.
	 */
	public FloatPoint rotate(float amount, FloatPoint pivot) {
		if (amount % 360 == 0) return this; // Save precious calc time if we really don't need to rotate.
		
		double c = Math.cos(Math.toRadians(amount));
		double s = Math.sin(Math.toRadians(amount));
		
		// Log.i("FloatPoint.rotate", String.format("Rotating %s by %s around %s.", toString(), amount, pivot.toString()));
		FloatPoint p = new FloatPoint(pivot);
		p.x -= x;
		p.y -= y;
		
		double xn,yn;
		
		xn = p.x * c - p.y * s;
		yn = p.x * s + p.y * c;
		
		p.x = (float)(xn + x);
		p.y = (float)(yn + y);
		
		x = p.x;
		y = p.y;
		
		return this;
	}
	
	/**
	 * Rotate the point around (0,0).
	 * @param amount The amount of degrees to rotate by.
	 * @return Returns self - good for chaining.
	 */
	public FloatPoint rotate(float amount) {
		rotate(amount, new FloatPoint(0,0));
		
		return this;
	}
	
	/**
	 * Normalise the point (... vector... shit).
	 * @return Returns self - good for chaining.
	 */
	public FloatPoint normalise() {
		double l = getLength();
		x /= l;
		y /= l;
		
		return this;
	}
	
	/**
	 * Returns the magnitude of the point (VECTOR! MERDE!)
	 * @return The magnitude ("length") of the vector.
	 */
	public double getLength() {
		return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
	}
	
	/**
	 * Sets a new magnitude for the vector-point-thingy.
	 * @param length The new desired length (absolute).
	 * @return The FloatPoint itself. Good for chaining.
	 */
	public FloatPoint setLength(double length) {
		normalise();
		x *= length;
		y *= length;
		return this;
	}
}
