package dk.homestead.canvastest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

/**
 * The ActionHandler is the basic handler for any interactions between the user
 * and drawing surface. A specific handler is set in the toolbox (by the user)
 * and this handler is subsequently given authority on future non-toolbox touch
 * events. It is responsible for the results of interactions.
 * Some handlers are RectHandler, ElipseHandler, LineHandler, FreedrawHandler
 * and SelectionHandler.
 * @author lindhart
 *
 */
public abstract class ActionHandler {

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
	
	/**
	 * Creates a new ActionHandler object. 
	 * @param buffersrc A base bitmap upon which a proper canvas can be
	 * initialised.
	 */
	public ActionHandler(Bitmap buffersrc) {
		bufferBitmap = buffersrc.copy(Bitmap.Config.ARGB_8888, true);
		bufferCanvas = new Canvas(bufferBitmap);
		Log.i("ActionHandler[]", "Mqutable? "+String.valueOf(bufferBitmap.isMutable()));
	}
	
	/**
	 * Returns a copy of the internal bitmap. Used by DrawView to decide what
	 * to draw from the handler.
	 * @return
	 */
	public Bitmap getBuffer() { return bufferBitmap; }
	
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
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bufferBitmap, 0, 0, null);	
	}
	
	/**
	 * Helper function that clears the canvas to transparent (not black or white).
	 * Shamelessly ripped from
	 * http://stackoverflow.com/questions/8716854/how-do-i-clear-the-contents-of-a-drawn-canvas-in-android
	 */
	protected void clearBuffer(){
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); 

		Rect rect=new Rect(0,0,bufferCanvas.getWidth(),bufferCanvas.getHeight());
		bufferCanvas.drawRect(rect,paint);
	}
}
