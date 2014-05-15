package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.RectEntity;

/**
 * The RectHandler class handles drawing rectangles on the drawing surface.
 * Drawing is started when the user places their finger on the board, marking
 * a corner of the rect. Further movement resizes and places the opposing
 * corner of the rectangle. Lifting the finger saves the rectangle on the
 * stack.
 * @author Croc
 *
 */
public class RectHandler extends ShapeHandler {

	@Override
	public PrimitiveEntity updateBuffer() {
		calcRectBounds();
		this.bufferedEntity = new RectEntity(left, top, right, bottom, getFillColor(), getStrokeColor());
		bufferedEntity.setStrokeWidth(getStrokeWidth());
		return bufferedEntity;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}
	
}
