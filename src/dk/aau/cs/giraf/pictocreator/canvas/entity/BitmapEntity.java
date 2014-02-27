package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import dk.aau.cs.giraf.pictocreator.canvas.Entity;

/**
 * The Bitmap entity quite simply displays a Bitmap at a specified location.
 * It does miss caching functionality, so having several entities in play at
 * once will very quickly cause Out-Of-Memory errors.
 * @author lindhart
 */
public class BitmapEntity extends Entity {

	/**
	 * The actual Bitmap to be displayed. It is never accessible from outside,
	 * always being cloned when placed. 
	 */
	protected Bitmap internalBitmap;
	
	/**
	 * Creates a new BitmapEntity object, copying a source Bitmap as its
	 * content.
	 * @param src The Bitmap to copy and use.
	 */
	public BitmapEntity(Bitmap src) {
		internalBitmap = Bitmap.createBitmap(src);
		
		setHeight(internalBitmap.getHeight());
		setWidth(internalBitmap.getWidth());
	}

    /**
     * Creates a new BitmapEntity object, copying a source Bitmap as its
     * content.
     * @param src The Bitmap to copy and use.
     * @param size The size of the bitmap.
     */
    public BitmapEntity(Bitmap src, int size) {
        internalBitmap = Bitmap.createScaledBitmap(src, src.getWidth() * size / 100, src.getHeight() * size / 100, true);

        setHeight(internalBitmap.getHeight());
        setWidth(internalBitmap.getWidth());
    }

	@Override
	public void doDraw(Canvas canvas) {
		canvas.drawBitmap(internalBitmap, 0, 0, null);
	}

}
