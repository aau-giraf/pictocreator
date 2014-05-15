package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.entity.LineEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;

/**
 * The LineHandler class allows drawing straight lines. Simple stuff.
 * @author Croc
 */
public class LineHandler extends ShapeHandler {
	
	@Override
	public PrimitiveEntity updateBuffer() {
		bufferedEntity = new LineEntity(startPoint.x, startPoint.y, endPoint.x, endPoint.y, getFillColor());
		bufferedEntity.setStrokeWidth(getStrokeWidth());
		return bufferedEntity;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}



}
