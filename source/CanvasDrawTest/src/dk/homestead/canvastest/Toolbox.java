package dk.homestead.canvastest;

import java.util.ArrayList;

import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * The toolbox entity 
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
	public void addHandler(ActionHandler handler) { this.handlers.add(handler); }
	
	/**
	 * Removes an existing handler from the toolbox.
	 * @param handler The handler to remove.
	 */
	public void removeHandler(ActionHandler handler) { this.handlers.remove(handler); }
	
	/**
	 * Sets the active handler in the Toolbox.
	 * @param handler The new handler. Must already be in the collection. 
	 */
	protected void setCurrentHandler(ActionHandler handler) {
		if (this.handlers.contains(handler)) this.currentHandler = handler;
		else throw new NotFoundException("The passed handler is not part of the Toolbox.");
	}
	
	public ActionHandler getCurrentHandler() { return this.currentHandler; }
	
	/**
	 * Generates a cached version of the current Toolbox display. Used for
	 * rendering, overlaying it with a highlight for the currnetly selected
	 * handler.
	 */
	private void generateBitmapCache(){
		this.cachedImage = Bitmap.createBitmap((int)getWidth(), (int)getHeight(), Bitmap.Config.ARGB_8888);
		int y = 0;
		for (ActionHandler h : handlers){
			
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		// Toolbox drawing functions a bit like EntityGroups, only manually
		// places the Entities.
		for (ActionHandler a : handlers){
			// Do stuff.
		}
	}
}
