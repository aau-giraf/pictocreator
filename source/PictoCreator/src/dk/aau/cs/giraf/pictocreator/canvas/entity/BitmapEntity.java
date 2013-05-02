package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import dk.aau.cs.giraf.pictocreator.canvas.Entity;;

public class BitmapEntity extends Entity {

	protected Bitmap internalBitmap;
	
	public BitmapEntity(Bitmap src) {
		internalBitmap = Bitmap.createBitmap(src);
		
		setHeight(internalBitmap.getHeight());
		setWidth(internalBitmap.getWidth());
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		
		canvas.translate(getX(), getY());
		canvas.rotate(getAngle(), getWidth()/2, getHeight()/2);
		canvas.drawBitmap(internalBitmap, 0, 0, null);
		
		canvas.restore();
	}

}
