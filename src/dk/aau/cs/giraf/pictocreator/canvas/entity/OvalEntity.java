package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.drawable.shapes.OvalShape;

import dk.aau.cs.giraf.pictocreator.canvas.SerializeClasses.SerializePaint;

/**
 * Simple Entity subclass representing an oval shape.
 * @author Croc
 */
public class OvalEntity extends PrimitiveEntity {
	
	public OvalEntity(float left, float top, float right, float bottom, int fillColor, int strokeColor) {
		super(left, top, right-left, bottom-top, fillColor, strokeColor);
	}

	@Override		
	public void drawWithPaint(Canvas canvas, SerializePaint paint) {
		OvalShape rs = new OvalShape();
		rs.resize(getWidth(), getHeight());
		 
		rs.draw(canvas, paint);
	}

    /**
     * Gives an accurate collision detection with ellipses.
     * Formula found from stackoverflow: http://stackoverflow.com/questions/7946187/point-and-ellipse-rotated-position-test-algorithm
     * @param x The X-coordinate of the point to check.
     * @param y The Y-coordinate of the point to check.
     * @return
     */
    @Override
    public boolean collidesWithPoint(float x, float y) {
        float radAngle = (float)Math.toRadians(getAngle());
        float tempwidth = getWidth() + getStrokeWidth() * 2;
        float tempheight = getHeight() + getStrokeWidth() * 2;
        return ((Math.pow(Math.cos(radAngle)*(x-getCenter().x)+Math.sin(radAngle)*(y-getCenter().y),2)/Math.pow(tempwidth/2,2))+
                (Math.pow(Math.sin(radAngle)*(x-getCenter().x)-Math.cos(radAngle)*(y-getCenter().y),2)/Math.pow(tempheight/2,2))) <= 1;
    }

    /**
     * Set the angle of rotation for this Entity.
     * If the angle is between 90 and 270 degrees, we reset the position of the entity to be in the upper left corner.
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
        else
            this.angle = (angle + 360) % 360;
    }

}
