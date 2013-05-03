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
 * @author lindhart
 */
public class LineHandler extends ShapeHandler {

	@Override
	public Bitmap getToolboxIcon(int size) {
		Bitmap ret = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(ret);
		
		Paint p = new Paint();
		p.setColor(0xFF000000);
		p.setStyle(Style.STROKE);
		c.drawLine(4, 8, size-8, size-4, p);
		return ret;
	}
	
	@Override
	public PrimitiveEntity updateBuffer() {
		bufferedEntity = new LineEntity(startPoint.x, startPoint.y, endPoint.x, endPoint.y, strokeColor);
		return bufferedEntity;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}

}
