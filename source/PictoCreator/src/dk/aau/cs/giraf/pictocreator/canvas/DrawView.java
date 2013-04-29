package dk.aau.cs.giraf.pictocreator.canvas;

import dk.aau.cs.giraf.pictocreator.canvas.handlers.FreehandHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.LineHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.OvalHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.RectHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.SelectionHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {

	/**
	 * This stack contains all current layers in the *drawing*. See drawStack
	 * for the entire rendering stack used.
	 */
	EntityGroup drawStack = new EntityGroup();
	
	/**
	 * The currently active handler for touch events.
	 */
	ActionHandler currentHandler;
	
	public DrawView(Context context) {
		super(context);
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Sets the active handler.
	 * @param handler The handler to use.
	 */
	public void setHandler(ActionHandler handler) {
		this.currentHandler = handler;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.w("DrawView",  "Invalidated. Redrawing.");
		
		// Drawing order: drawStack, drawBuffer.
		drawStack.draw(canvas);
		
		if (currentHandler != null) currentHandler.drawBuffer(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Always draw for now. Very inefficient, but the optimisation is too demanding for now.
		// We need some way of messaging a "dirty" state of parts of the draw stack. 
		invalidate();
		
		if (currentHandler.onTouchEvent(event, drawStack)) {
			return true;
		} else return false;
	}
}
