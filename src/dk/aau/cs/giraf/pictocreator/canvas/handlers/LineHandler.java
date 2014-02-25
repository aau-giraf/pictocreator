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
		bufferedEntity = new LineEntity(startPoint.x, startPoint.y, endPoint.x, endPoint.y, getFillColor(), getStrokeColor());
		bufferedEntity.setStrokeWidth(getStrokeWidth());
		return bufferedEntity;
	}
	
	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}
	
	/**
	 * In order to reduce some ambiguity and usability issues, the line will
	 * take the fill color as stroke color.
	 */
	@Override
	public void setFillColor(int color) {
		super.setStrokeColor(color);
		super.setFillColor(color);
	}

}
