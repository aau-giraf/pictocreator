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
