package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.entity.OvalEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;

/**
 * The RectHandler class handles drawing rectangles on the drawing surface.
 * Drawing is started when the user places their finger on the board, marking
 * a corner of the rect. Further movement resizes and places the opposing
 * corner of the rectangle. Lifting the finger saves the rectangle on the
 * stack.
 * @author Croc
 *
 */
public class OvalHandler extends ShapeHandler {

    @Override
    public Bitmap getToolboxIcon(int size) {
        Bitmap returnValue = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnValue);

        canvas.scale(0.5f, 0.5f, size / 2, size / 2);

        Paint paint = new Paint();
        RectF area = new RectF(0.0f, 0.0f, size, size);

        paint.setColor(0xFFCC0000);
        paint.setStyle(Style.FILL);

        canvas.drawArc(area, 0, 360, true, paint);

        paint.setColor(0xFF000000);
        paint.setStyle(Style.STROKE);

        canvas.drawArc(area, 0, 360, true, paint);

        return returnValue;
    }

	@Override
	public PrimitiveEntity updateBuffer() {
		calcRectBounds();
		this.bufferedEntity = new OvalEntity(left, top, right, bottom, getFillColor(), getStrokeColor());
		bufferedEntity.setStrokeWidth(getStrokeWidth());
		return bufferedEntity;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}
}
