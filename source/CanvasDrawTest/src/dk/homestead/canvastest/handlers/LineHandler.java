package dk.homestead.canvastest.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import dk.homestead.canvastest.EntityGroup;
import dk.homestead.canvastest.entity.LineEntity;
import dk.homestead.canvastest.entity.PrimitiveEntity;

/**
 * The LineHandler class allows drawing straight lines. Simple stuff.
 * @author lindhart
 */
public class LineHandler extends ShapeHandler {

	public LineHandler(Bitmap buffersrc) {
		super(buffersrc);
		// Set up generic paint.
		strokeColor = 0xFF000000;
	}

	@Override
	public Bitmap getToolboxIcon(int size) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PrimitiveEntity updateBuffer() {
		bufferedEntity = new LineEntity(startPoint.x, startPoint.y, endPoint.x, endPoint.y, strokeColor);
		return bufferedEntity;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(new LineEntity(startPoint.x, startPoint.y, endPoint.x, endPoint.y, strokeColor));
	}

}
