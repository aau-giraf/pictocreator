package dk.aau.cs.giraf.pictocreator.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * The ActionHandler is the basic handler for any interactions between the user
 * and drawing surface. A specific handler is set in the toolbox (by the user)
 * and this handler is subsequently given authority on future non-toolbox touch
 * events. It is responsible for the results of interactions.
 * Some handlers are RectHandler, ElipseHandler, LineHandler, FreedrawHandler
 * and SelectionHandler.
 * @author Croc
 *
 */
public abstract class ActionHandler extends Entity {

	/**
	 * The drawing buffer. Modify this to suit. This buffer will always been
	 * drawn on top of the drawStack as long as the handler is active. For
	 * example, RectHandler uses the buffer to display the rectangle being
	 * created until it is finalised. During this time, DrawView will render
	 * the buffer. Once the rectangle has been finalised and added to the
	 * drawStack, the handler is no longer active. 
	 */
	protected Canvas bufferCanvas;
	
	/**
	 * The raw bitmap that bufferCanvas draws to. Modify it directly if needed,
	 * but most necessary drawing primitives are accessible from bufferCanvas.
	 */
	protected Bitmap bufferBitmap;
	
	private int strokeColor;
	private int fillColor;
	private float strokeWidth;
	
	/**
	 * Sets the colour of the shape's stroke (border colouring).
	 * @param color The new colour in ARGB format. Hint: 0xFF000000 is opaque
	 * black.
	 */
	public void setStrokeColor(int color) { strokeColor = color; }
	
	/**
	 * Retrieves the current stroke colour in ARGB format.
	 * @return The active stroke colour.
	 */
	public int getStrokeColor() { return strokeColor; }
	
	/**
	 * Sets the fill colouring used.
	 * @param color The new colour in ARGB format. 0xFF000000 is opaque black.
	 */
	public void setFillColor(int color) { fillColor = color; }
	
	/**
	 * Retrieves the currently active fill colour.
	 * @return The fill colour, in ARGB format.
	 */
	public int getFillColor() { return fillColor; }
	
	/**
	 * Retrieves the current stroke width (border thickness).
	 * @return The current stroke width.
	 */
	public float getStrokeWidth() { return strokeWidth; }
	
	/**
	 * Sets a new stroke width.
	 * @param width The new width.
	 */
	public void setStrokeWidth(float width) {
        if(width <= 20){
            strokeWidth = width;
        }
        else{
            strokeWidth = 20;
        }
    }
	
	/**
	 * Primary handling mechanism. On every touch event, this method is invoked
	 * and the handler should respond accordingly.
	 * @param event The source event, passed uncorrupted from the DrawView parent.
	 * @param drawStack The stack of completed drawing operations in the view.
	 * Probably only used by SelectionHandler. 
	 * @return Boolean after the same principles as regular TouchEvent
	 * handlers: if you handled the event, return true, otherwise false.
	 */
	public abstract boolean onTouchEvent(MotionEvent event, EntityGroup drawStack);
	
	/**
	 * Shorthand for scaling a Bitmap to a specific size.
	 * @param toScale The Bitmap to scale.
	 * @param newSize The new size-by-size size.
	 * @return The scaled Bitmap.
	 */
	protected static Bitmap scaleBitmap(Bitmap toScale, int newSize){
		return Bitmap.createScaledBitmap(toScale, newSize, newSize, true);
	}
	
	/**
	 * All handlers must define a drawBuffer method that draws their current UI
	 * output on a passed canvas before the bounds are drawn on.
	 */
	public abstract void drawBufferPreBounds(Canvas canvas);
	
	/**
	 * ActionHandlers can choose to implement another draw call for after the
	 * bounds have been drawn. SelectionHandler uses this, for example, to draw
	 * its context icons so they are visible above the bounds area.
	 * @param canvas
	 */
	public void drawBufferPostBounds(Canvas canvas) {
		
	}
}
