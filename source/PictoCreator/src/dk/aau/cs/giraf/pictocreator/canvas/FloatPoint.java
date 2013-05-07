package dk.aau.cs.giraf.pictocreator.canvas;

import android.util.Log;

/**
 * Simpler Point implementation that allows floats. Fuckin' odd that they use
 * floats for coordinates but integers for point coordinates.
 * @author lindhart
 *
 */
public class FloatPoint {

	public float x;
	public float y;
	
	public FloatPoint() {
		this.x = this.y = 0;
	}
	
	public FloatPoint(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public float distance(FloatPoint to){
		float result = (float)Math.sqrt(Math.abs(x-to.x)+Math.abs(y-to.y));
		Log.i("FloatPoint.distance", "Distance between "+toString()+" and "+to.toString()+" is "+String.valueOf(result));
		return result;
	}
	
	public String toString(){
		return "("+String.valueOf(x)+","+String.valueOf(y)+")";
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
		
		x = (float)(pivot.x + c * (x - pivot.x) - s * (y - pivot.y));
		y = (float)(pivot.y + s * (x - pivot.x) + c * (y - pivot.y));
		
		// Log.i("FloatPoint.rotate", String.format("New point: %s.", toString()));
		
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
	 * @return
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
