package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;

import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;

/**
 * Simple Entity. It is basically a visible rect.
 * @author Croc
 *
 */
public class RectEntity extends PrimitiveEntity {
	
	public RectEntity(float left, float top, float right, float bottom, int fillColor, int strokeColor) {
		super(left, top, right-left, bottom-top, fillColor, strokeColor);
	}

	@Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
		RectShape rs = new RectShape();
		rs.resize(getWidth(), getHeight());

		rs.draw(canvas, paint);
	}

    /**
     * The four points are calculated using a rotation matrix, since they are different when the rectangle is rotated.
     * The clicked point is then compared to the four sides, to check whether it is a right rotation of them.
     * @param x The X-coordinate of the point to check.
     * @param y The Y-coordinate of the point to check.
     * @return
     */
    @Override
    public boolean collidesWithPoint(float x, float y){
        float tempheight = getHeight() + getStrokeWidth() * 2;
        float tempwidth = getWidth() + getStrokeWidth() * 2;
        FloatPoint P = new FloatPoint(x,y);
        FloatPoint A = rotationMatrix( -(tempwidth/2), -(tempheight/2));
        FloatPoint B = rotationMatrix((tempwidth/2), -(tempheight/2));
        FloatPoint C = rotationMatrix((tempwidth/2), (tempheight/2));
        FloatPoint D = rotationMatrix( -(tempwidth/2), (tempheight/2));

        return (rightOrientation(A, B, P) && rightOrientation(B, C, P) && rightOrientation(C, D, P) && rightOrientation(D, A, P));
    }

    /**
     * This function is used to check whether a point is a right rotation of a side.
     * If the clicked point is a right rotation of all the sides in the rectangle it means it is inside of it.
     * This was used to improve the collision detection for rotated rectangles.
     * @param a the start point of a vector
     * @param b the endpoint of a vector
     * @param p the clicked point
     * @return
     */
    private Boolean rightOrientation(FloatPoint a, FloatPoint b, FloatPoint p){
        return (b.y-a.y)*(p.x-b.x)<=(p.y-b.y)*(b.x-a.x);
    }

    /**
     * Set the angle of rotation for this Entity.
     * If the angle is between 90 and 270 degrees, we reset the position of the entity to be in the upper left corner.
     * This was done to prevent weird behaviour when the entity is resized after being rotated.
     * @param angle The new angle.
     */
    @Override
    public void setAngle(float angle) {
        if(angle>=90 && angle <= 270){
            setX(getCenter().x - getHeight()/2);
            setY(getCenter().y - getWidth()/2);
            float temp = getHeight();
            setHeight(getWidth());
            setWidth(temp);
            this.angle = Math.abs(90-angle%180);
        }
        else
            this.angle = (angle + 360) % 360;
    }
}
