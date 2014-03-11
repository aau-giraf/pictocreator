package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;

/**
 * The LineEntity class represents a straight line, point A to point B. For
 * hitbox purposes, the LineEntity is considered a rectangle.
 * @author lindhart
 *
 */
public class LineEntity extends PrimitiveEntity {
	
	/**
	 * The vector of the line.
	 */
	protected FloatPoint lineVector;
    protected FloatPoint endPoint = new FloatPoint();
    private final float JITTER_MAX = 20;

	/**
	 * Creates a new LineEntity going through specific point.s
	 * @param x1 First X-coordinate.
	 * @param y1 First Y-coordinate.
	 * @param x2 Second X-coordinate.
	 * @param y2 Second Y-coordinate.
	 * @param color Colour of the line.
	 */
	public LineEntity(float x1, float y1, float x2, float y2, int color) {
		super(color, color);
		
		setX(x1);
		setY(y1);

        endPoint = new FloatPoint(x2, y2);
		lineVector = new FloatPoint(x2-x1, y2-y1);
	}
	
	@Override
	public void drawWithPaint(Canvas canvas, Paint paint) {
        Log.i("LineEntity.drawWithPaint",
                String.format("Drawing line with starting point (%s,%s) with color %s", getX(), getY(), paint.getColor()));

        canvas.drawLine(0, 0, lineVector.x, lineVector.y, paint);
	}

	@Override
	public float getHeight() {
		return Math.abs(lineVector.y);
	}
	
	@Override
	public float getWidth() {
		return Math.abs(lineVector.x);
	}

	@Override
    public float getHitboxLeft(){
        if (hitboxTopLeft == null)
            return Math.min(getX(),endPoint.x);
        else
            return hitboxTopLeft.x;
    }

    @Override
    public float getHitboxRight(){
        if (hitboxTopLeft == null)
            return Math.min(getX(), endPoint.x) + getWidth();
        else
            return hitboxTopLeft.x + hitboxWidth;
    }

    @Override
    public float getHitboxTop(){
        if (hitboxTopLeft == null)
            return Math.min(getY(), endPoint.y);
        else
            return hitboxTopLeft.y;
    }

    @Override
    public float getHitboxBottom(){
        if (hitboxTopLeft == null)
            return Math.min(getY(),endPoint.y) + getHeight();
        else
            return hitboxTopLeft.y + hitboxHeigth;
    }

    @Override
    public FloatPoint getCenter() {
        return new FloatPoint(getX() + lineVector.x/2, getY() + lineVector.y/2);
        //return new FloatPoint(Math.min(getX(), endPoint.x) + getWidth()/2, Math.min(getY(), endPoint.y) + getHeight()/2);
    }

    @Override
    public void draw(Canvas canvas) {
        int canvasLayers = canvas.getSaveCount();
        canvas.save();

        canvas.translate(getX(), getY());
        canvas.rotate(getAngle(), lineVector.x/2, lineVector.y/2);

        doDraw(canvas);

        canvas.restoreToCount(canvasLayers);
    }

    @Override
    protected void changeHitbox(){
        FloatPoint one;
        FloatPoint two;

        one = rotationMatrix(lineVector.x/2, lineVector.y/2);
        two = rotationMatrix(-lineVector.x/2, -lineVector.y/2);

        hitboxTopLeft = new FloatPoint(Math.min(one.x, two.x), Math.min(one.y, two.y));
        hitboxWidth = (getCenter().x - hitboxTopLeft.x)*2;
        hitboxHeigth = (getCenter().y - hitboxTopLeft.y)*2;
    }

    @Override
    public boolean collidesWithPoint(float x, float y) {
        return distanceFromPointToVector(x, y) < JITTER_MAX &&
                getHitboxTop() - JITTER_MAX < y &&
                getHitboxBottom() + JITTER_MAX > y &&
                getHitboxLeft() - JITTER_MAX < x &&
                getHitboxRight() + JITTER_MAX > x;
    }

    /**
     *
     * @param px
     * @param py
     * @return
     */
    private float distanceFromPointToVector(float px, float py){
        return (float) ((Math.abs(lineVector.y*px - lineVector.x*py - getX()*(getY()+lineVector.y) + (getX()+lineVector.x)*getY()))/
                (Math.sqrt(Math.pow(lineVector.x, 2) + Math.pow(lineVector.y, 2))));

    }
}
