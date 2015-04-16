package dk.aau.cs.giraf.pictocreator.canvas.entity;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;

import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses.SerializablePaint;
import dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses.SerializableRectF;

/**
 * An Entity class representing a freehand drawing sequence. Freehand drawings
 * are registered with the available data points from touch events, typically.
 * However, since touch events are very scarce, we interpolate touch points
 * with simple lines. This gives the impression of freehand drawing in all but
 * the most minimal of TEpS (Touch Events per Second).
 * @author lindhart
 */
public class EraserEntity extends PrimitiveEntity {

    /**
     * The base point is the null point for all subsequent points in the path.
     * When a new point is added, it's offset from the base point is registered
     * in lieu of its absolute position. This allows for simpler placement of
     * the LineEntity at a slight computation cost.
     */
    protected FloatPoint basePoint = new FloatPoint();

    /**
     * List of all drawPoints. Consider them ordered, so the draw sequence is
     * intact.
     */
    protected ArrayList<FloatPoint> drawPoints = new ArrayList<FloatPoint>();

    public boolean isErasing = true;

    private Bitmap eraserBitmap;

    @Override
    public void setX(float x) {
        if (basePoint == null) basePoint = new FloatPoint(x, 0);
        else basePoint.x = x;
    }

    @Override
    public void setY(float y) {
        if (basePoint == null) basePoint = new FloatPoint(0, y);
        else basePoint.y = y;
    }

    @Override
    public float getX() {
        return basePoint.x;
    }

    @Override
    public float getY() {
        return basePoint.y;
    }

    @Override
    public float getWidth() {
        if(hitboxTopLeft != null){
            return (getCenter().x - basePoint.x)*2;
        }
        else{
            return super.getWidth();
        }
    }

    @Override
    public float getHeight() {
        if(hitboxTopLeft != null){
            return (getCenter().y - basePoint.y)*2;
        }
        else{
            return super.getHeight();
        }
    }

    /**
     * Create a new FreehandEntity with a specific stroke color.
     * @param color Hexadecimal color. Alpha channel: higher > more opaque.
     */
    public EraserEntity(int color, Bitmap eraserBitmap) {
        super(color, color);
        setX(0);
        setY(0);
        this.eraserBitmap = eraserBitmap;
    }

    @Override
    public void drawWithPaint(Canvas canvas, SerializablePaint paint) {
        if (drawPoints.size() <= 1) return; // Don't draw trivial.

        paint = new SerializablePaint(paint); // Copy.

        Path p = new Path();
        for (FloatPoint floatPoint : drawPoints){
            p.lineTo(floatPoint.x, floatPoint.y);
        }

        paint.setStyle(Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        if (isErasing)
            canvas.drawBitmap(eraserBitmap, drawPoints.get(drawPoints.size() - 1).x, drawPoints.get(drawPoints.size() - 1).y, null);

        canvas.drawPath(p, paint);
    }

    /**
     * Add a new point to the Entity.
     * @param x The X-coordinate.
     * @param y The Y-coordinate.
     */
    public void addPoint(float x, float y) {
        FloatPoint newPoint = new FloatPoint(x, y);

        if (drawPoints.size() == 0) {
            basePoint = newPoint;
            drawPoints.add(new FloatPoint(0,0));
        } else {
            drawPoints.add(new FloatPoint(x-basePoint.x, y-basePoint.y));
        }
    }

    /**
     * Add a new FloatPoint to the Entity.
     * @param p The point to add.
     */
    public void addPoint(FloatPoint p) {
        addPoint(p.x, p.y);
    }

    /**
     * Checks whether a clicked point collides with this entity.
     * Same idea is used as in the line entity collision detection.
     * @param x The X-coordinate of the point to check.
     * @param y The Y-coordinate of the point to check.
     * @return true if the clicked point is within the freehand entity, false if not.
     */
    @Override
    public boolean collidesWithPoint(float x, float y) {
        return false;
    }
}
