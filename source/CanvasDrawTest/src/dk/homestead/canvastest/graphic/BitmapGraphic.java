package dk.homestead.canvastest.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import dk.homestead.canvastest.Graphic;

/**
 * BitmapGraphics use a Bitmap object for its display. Simple.
 * @author lindhart
 *
 */
public class BitmapGraphic extends Graphic {

	/**
	 * The original Bitmap image. Used to ensure that resizing happens with
	 * greatest quality retention possible.
	 */
	protected Bitmap originalBitmap;
	
	/**
	 * The active Bitmap image. This is what is displayed on draw calls.
	 */
	protected Bitmap activeBitmap;
	
	public BitmapGraphic(Bitmap src) {
		this.originalBitmap = src.copy(Bitmap.Config.ARGB_8888, true);
	}

	/**
	 * Returns the width of the Bitmap image.
	 */
	@Override
	public float getWidth() {
		return activeBitmap.getWidth();
	}

	/**
	 * Returns the height of the Bitmap image.
	 */
	@Override
	public float getHeight() {
		return activeBitmap.getHeight();
	}

	/**
	 * Sets the Bitmap to a new width. This call is aspect-aware, so the height
	 * will be scaled accordingly.
	 */
	@Override
	public void setWidth(float w) {
		float aspect = getWidth()/w;
		activeBitmap = Bitmap.createScaledBitmap(originalBitmap, (int)w, (int)(aspect/getHeight()), true);
	}

	/**
	 * Sets the Bitmap to a new height. This call is aspect-aware, so the width
	 * will be scaled accordingly.
	 */
	@Override
	public void setHeight(float h) {
		float aspect = getHeight()/h;
		activeBitmap = Bitmap.createScaledBitmap(originalBitmap, (int)(aspect/getWidth()), (int)h, true);
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(activeBitmap, getX(), getY(), null);
	}
}
