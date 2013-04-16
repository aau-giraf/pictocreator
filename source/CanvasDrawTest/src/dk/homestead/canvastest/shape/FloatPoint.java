package dk.homestead.canvastest.shape;

/**
 * Simpler Point implementation that allows floats. Fuckin' odd that they use
 * floats for coordinates but integers for point coordinates.
 * @author lindhart
 *
 */
public class FloatPoint {

	float x;
	float y;
	
	public FloatPoint() {
		// TODO Auto-generated constructor stub
		this.x = this.y = 0;
	}
	
	public FloatPoint(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

}
