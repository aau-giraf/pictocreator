package dk.homestead.canvastest.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import dk.homestead.canvastest.ActionHandler;
import dk.homestead.canvastest.EntityGroup;
import dk.homestead.canvastest.FloatPoint;
import dk.homestead.canvastest.entity.RectEntity;

/**
 * The RectHandler class handles drawing rectangles on the drawing surface.
 * Drawing is started when the user places their finger on the board, marking
 * a corner of the rect. Further movement resizes and places the opposing
 * corner of the rectangle. Lifting the finger saves the rectangle on the
 * stack.
 * @author lindhart
 *
 */
public class RectHandler extends ActionHandler {

	protected FloatPoint startPoint;
	protected FloatPoint endPoint;
	protected int currentIndex;
	
	protected float left,top,right,bottom;
	
	protected Paint paint;
	
	private String tag = "RectHandler.onTouchEvent";
	
	public RectHandler(Bitmap buffersrc) {
		super(buffersrc);
		// Set up generic paint.
		paint = new Paint();
		paint.setARGB(255, 0, 0, 0); // Black!
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, EntityGroup drawStack) {
		int index = event.getActionIndex();
		int action = event.getAction(); // getActionMasked seems b0rk3d.
		
		boolean doDraw = false; // Set to true if we have data to draw.
		
		if (action == MotionEvent.ACTION_DOWN){
			// Just pressed to tab. Store start location and index.
			startPoint = endPoint = new FloatPoint(event.getX(index), event.getY(index));
			currentIndex = index;
			doDraw = false;
			Log.i(tag, "Finger down. No new action.");
		}
		else if (action == MotionEvent.ACTION_MOVE){
			// Movement. Assert correct index and store secondary position.
			if (index == currentIndex){
				endPoint = new FloatPoint(event.getX(index), event.getY(index));
				Log.i(tag, "Move event. Drawing new rectangle.");
			}
			else Log.i(tag,"Ignoring move event. Index does not match.");
			doDraw = true;
		}
		else if (action == MotionEvent.ACTION_UP){
			// Finger raised. If we have valid data, store to drawStack and clear.
			if (index == currentIndex){
				if (startPoint.distance(endPoint) < 2) Log.i(tag, "Points are not distinct. Ignoring.");
				else{
					calcRectBounds();
					drawStack.addEntity(new RectEntity(left, top, right, bottom, paint));
				}
				doDraw=false;
				Log.i(tag, "Drawing ended. Storing rect on drawStack.");
				clearBuffer();
			}
			else Log.w(tag, "Index did not match. Ignoring event.");
		}
		else Log.e(tag, "Unknown and thus unhandled touch event!"); 
		
		// Draw a rect from our stored starting point and current touch points.
		if (doDraw){
			Log.i(tag, "Drawing rect on buffer.");
			drawRect();
		}
		
		return true;
	}
	
	/**
	 * Draws the rectangle to the buffer.
	 */
	protected void drawRect(){
		// Clear the buffer, first of all.
		this.clearBuffer();
		
		calcRectBounds();
		
		bufferCanvas.drawRect(left, top, right, bottom, paint);
	}
	
	/**
	 * Helper function that recalculates the bounds of the rectangle based on
	 * start and end points.
	 */
	protected void calcRectBounds(){
		top = Math.min(startPoint.y, endPoint.y);
		bottom = Math.max(startPoint.y, endPoint.y);
		left = Math.min(startPoint.x, endPoint.x);
		right = Math.max(startPoint.x, endPoint.x);
	}
}
