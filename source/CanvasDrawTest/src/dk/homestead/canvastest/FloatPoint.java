package dk.homestead.canvastest;

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
}
