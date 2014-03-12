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
    private Bitmap theRealBitmap;
	
	/**
	 * Creates a new BitmapEntity object, copying a source Bitmap as its
	 * content.
	 * @param src The Bitmap to copy and use.
	 */
	public BitmapEntity(Bitmap src) {
		theRealBitmap = src;
        createPicture(src.getHeight(),src.getWidth());

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
        theRealBitmap = src;
        createPicture(src.getWidth() * size / 100,src.getHeight() * size / 100);
        setHeight(internalBitmap.getHeight());
        setWidth(internalBitmap.getWidth());
    }

	@Override
	public void doDraw(Canvas canvas) {
		canvas.drawBitmap(internalBitmap, 0, 0, null);
	}


    /**
     * Sets a new height for the Entity.
     * @param height New height.
     */
    @Override
    public void setHeight(float height) {
        this.height = height;
        createPicture(this.width,this.height);
    }

    /**
     * Sets a new width for the Entity.
     * @param width New width.
     */
    @Override
    public void setWidth(float width) {
        this.width = width;
        createPicture(this.width,this.height);
    }

    /**
     * This function creates the picture. Furthermore it makes it able to scale the picture.
     *
     * @param width is the width of the picture
     * @param height is the height of the picture
     */
    private void createPicture(float width, float height){
        if(width != 0 && height != 0 && theRealBitmap != null)
            internalBitmap = Bitmap.createScaledBitmap(theRealBitmap, (int)width, (int)height, true);
    }
}
