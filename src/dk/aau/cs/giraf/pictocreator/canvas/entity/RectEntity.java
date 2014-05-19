package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.drawable.shapes.RectShape;

import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses.SerializablePaint;

/**
 * Simple Entity. It is basically a visible rect.
 * @author Croc
 */
public class RectEntity extends PrimitiveEntity {
	
	public RectEntity(float left, float top, float right, float bottom, int fillColor, int strokeColor) {
		super(left, top, right-left, bottom-top, fillColor, strokeColor);
	}

	@Override
	public void drawWithPaint(Canvas canvas, SerializablePaint paint) {
		RectShape rectShape = new RectShape();
        rectShape.resize(getWidth(), getHeight());

        rectShape.draw(canvas, paint);
	}

    /**
     * The four points are calculated using a rotation matrix, since they are different when the rectangle is rotated.
     * The clicked point is then compared to the four sides, to check whether it is a right rotation of them.
     * @param x The X-coordinate of the point to check.
     * @param y The Y-coordinate of the point to check.
     * @return if the clicked point is inside of the entity.
     */
    @Override
    public boolean collidesWithPoint(float x, float y){
        float tempheight = getHeight() + getStrokeWidth() * 2;
        float tempwidth = getWidth() + getStrokeWidth() * 2;
        //p is the clicked point
        FloatPoint P = new FloatPoint(x,y);
        //these variables are the sides p will be compared to
        FloatPoint A = rotationMatrix( -(tempwidth/2), -(tempheight/2));
        FloatPoint B = rotationMatrix((tempwidth/2), -(tempheight/2));
        FloatPoint C = rotationMatrix((tempwidth/2), (tempheight/2));
        FloatPoint D = rotationMatrix( -(tempwidth/2), (tempheight/2));

        return (isRightRotation(A, B, P) && isRightRotation(B, C, P) && isRightRotation(C, D, P) && isRightRotation(D, A, P));
    }

    /**
     * Set the angle of rotation for this Entity.
     * If the angle is between 315 and 45 degrees (a range of 90), we reset the position of the entity to be in the upper left corner,
     * switches its dimensions, and rotates accordingly.
     * This was done to prevent weird behaviour when the entity is resized after being rotated.
     * @param angle The new angle.
     */
    @Override
    public void setAngle(float angle) {
        if(angle> 44 && angle < 314){
            setX(getCenter().x - getHeight()/2);
            setY(getCenter().y - getWidth()/2);
            float temp = getHeight();
            setHeight(getWidth());
            setWidth(temp);
            this.angle = ((angle%180) + 270)%360;
        }
        else{
            this.angle = (angle + 360) % 360;
        }
    }
}
