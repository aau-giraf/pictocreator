package dk.homestead.canvastest;

import java.util.ArrayList;

import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

/**
 * The toolbox entity is a dynamic grid of ActionHandlers with which the user
 * can interact.
 * @author lindhart
 *
 */
public class Toolbox extends Entity {

	protected ArrayList<ActionHandler> handlers = new ArrayList<ActionHandler>();
	
	protected ActionHandler currentHandler;
	
	/**
	 * A cached image of the Toolbox. Used by the draw calls for better
	 * performance.
	 */
	private Bitmap cachedImage;
	
	/**
	 * Creates a new Toolbox with the given dimensions.
	 * @param w Width of the Toolbox.
	 * @param h Height of the Toolbox.
	 */
	public Toolbox(int w, int h) {
		this.setWidth(w);
		this.setHeight(h);
	}

	/**
	 * Adds a new handler to the toolbox.
	 * @param handler The handler to add.
	 */
	public void addHandler(ActionHandler handler) {
		this.handlers.add(handler);
		// For the first one.
		if (handlers.size() == 1) setCurrentHandler(handler);
		generateBitmapCache(); // Update the cache after each membership change.
	}
	
	/**
	 * Removes an existing handler from the toolbox.
	 * @param handler The handler to remove.
	 */
	public void removeHandler(ActionHandler handler) {
		this.handlers.remove(handler);
		generateBitmapCache(); // Update the cache after each membership change.	
	}
	
	/**
	 * Sets the active handler in the Toolbox.
	 * @param handler The new handler. Must already be in the collection. 
	 */
	protected void setCurrentHandler(ActionHandler handler) {
		if (this.handlers.contains(handler)) {
			this.currentHandler = handler;
			generateBitmapCache(); // Update the cache.
		}
		else throw new NotFoundException("The passed handler is not part of the Toolbox.");
	}
	
	public ActionHandler getCurrentHandler() { return this.currentHandler; }
	
	/**
	 * Generates a cached version of the current Toolbox display. Used for
	 * rendering, overlaying it with a highlight for the currently selected
	 * handler.
	 */
	private void generateBitmapCache(){
		this.cachedImage = Bitmap.createBitmap((int)getWidth(), (int)getHeight(), Bitmap.Config.ARGB_8888);
		// Canvas for drawing to the cache.
		Canvas cacheCanvas = new Canvas(this.cachedImage);
		int y = 0;
		Bitmap tmp;
		for (ActionHandler h : handlers){
			tmp = h.getToolboxIcon((int)getWidth());
			
			// Possibly run highlighting on the icon.
			if (h == currentHandler) tmp = drawAddedHighlight(tmp);
			
			cacheCanvas.drawBitmap(tmp, 0, y, null);
			y += getWidth();
		}
	}
	
	private Bitmap drawAddedHighlight(Bitmap toMod) {
		Bitmap toRet = toMod.copy(Config.ARGB_8888, true);
		Canvas modder = new Canvas(toRet);
		Paint highlight = new Paint();
		
		int strokeW = 4;
		int padding = strokeW/2;
		
		highlight.setColor(0xFF00AA00);
		highlight.setStyle(Style.STROKE);
		highlight.setStrokeWidth(strokeW);
		RectF area = new RectF(padding, padding, toRet.getWidth()-padding, toRet.getHeight()-padding);
		modder.drawRect(area, highlight);
		
		return toRet;
	}
	
	@Override
	public void draw(Canvas canvas) {
		Log.i("Toolbox.draw", "Drawing image, dimensions: " + String.valueOf(cachedImage.getWidth()) + "x" + String.valueOf(cachedImage.getHeight()));
		canvas.drawBitmap(cachedImage, 0, 0, null);
	}
	
	/**
	 * Figures out which handler, if any, collides with touch point, and
	 * changes handler.
	 * @param event The full touch event.
	 * @return True if handled (and selected handler), false otherwise.
	 */
	public boolean onTouch(MotionEvent event) {
		Log.i("Toolbox.onTouch", "onTouch invoked.");
		
		int pointerIndex = event.getActionIndex();
		
		if (!collidesWithPoint(event.getX(pointerIndex), event.getY(pointerIndex))) {
			Log.i("Toolbox.onTouch", "Pointer does not collide with Toolbox area. Ignoring.");
			return false;
		}
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN : {
			Log.i("Toolbox.onTouch", "Valid event type. Continuing.");
			
			int handlerIndex = (int)Math.floor(event.getY(pointerIndex)/getWidth());
			
			Log.i("Toolbox.onTouch", "Pointer at handler index " + String.valueOf(handlerIndex) + ".");
			
			if (handlerIndex < handlers.size()) {
				setCurrentHandler(handlers.get(handlerIndex));
				Log.i("Toolbox.onTouch", "New handler: '" + currentHandler.getClass().getName() + "'.");
			}
			return true;
		}
		case MotionEvent.ACTION_MOVE : {
			// Ignore for now. May have reordering/secondary effect later.
			return true;
		}
		default : return false; // Unhandled
		}
	}
}
