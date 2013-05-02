package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import dk.aau.cs.giraf.pictocreator.canvas.ActionHandler;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;

/**
 * An ActionHandler specifically for drawing shapes. This aggregates such
 * things as stroke and fill color.
 * @author lindhart
 */
public abstract class ShapeHandler extends ActionHandler {

	/**
	 * Support values. calcRectBounds will normalize these to fit a proper
	 * rectangle from startPoint and endPoint.
	 */
	protected float left,top,right,bottom;
	
	/**
	 * Location where the registered pointer first was seen.
	 */
	protected FloatPoint startPoint;
	
	/**
	 * Most recent location of the registered pointer.
	 */
	protected FloatPoint endPoint;
	
	/**
	 * The ID of the pointer we're tracking for our handling.
	 */
	protected int currentPointerId;
	
	private String tag = "ShapeHandler.onTouchEvent";
	
	/**
	 * The PrimitiveEntity currently being created. The seperate handlers can
	 * use this both for final addition to the drawStack as well as temp buffer
	 * drawing.
	 */
	protected PrimitiveEntity bufferedEntity;
	
	protected boolean doDraw;

	@Override
	public void setStrokeColor(int color) {
		super.setStrokeColor(color);
		if (bufferedEntity != null) bufferedEntity.setStrokeColor(color);
	}
	
	@Override
	public void setFillColor(int color) {
		super.setFillColor(color);
		if (bufferedEntity != null) bufferedEntity.setFillColor(color);
	}
	
	public ShapeHandler() {
		startPoint = endPoint = new FloatPoint(0, 0);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event, EntityGroup drawStack) {
		int action = event.getAction(); // getActionMasked seems b0rk3d.
		int eventIndex = event.getActionIndex(); // Get the index to the responsible pointer for this event.
		int eventPointerId = event.getPointerId(eventIndex);
		
		Log.i(tag, "MoveEvent handler invoked!");
		
		if (action == MotionEvent.ACTION_DOWN){
			// Just pressed to tab. Store start location and index.
			startPoint = endPoint = new FloatPoint(event.getX(eventIndex), event.getY(eventIndex));
			
			// Register the ID of the pointer responsible for the down event. This is what we actually track.
			this.currentPointerId = eventPointerId;
			
			doDraw = false;
			
			Log.i(tag, "Pointer down. Registering.");
		}
		else if (action == MotionEvent.ACTION_MOVE){
			// Assert that the pointer responsible for the event is also the one we're tracking.
			if (eventPointerId == this.currentPointerId) {
				endPoint = new FloatPoint(event.getX(eventIndex), event.getY(eventIndex));
				Log.i(tag, "Move event. Drawing new shape.");
			}
			else Log.i(tag, "Move event. Wrong pointer. Ignoring.");
			
			// Under all circumstances, draw the buffer canvas to display to the user.
			doDraw = true;
			// drawBuffer(this.bufferCanvas);
		}
		else if (action == MotionEvent.ACTION_UP){
			// Finger raised. If we have valid data, store to drawStack and clear.
			// Validate the pointer ID.
			if (eventPointerId == this.currentPointerId){
				// Test for distance between end points to validate even drawing at all.
				if (startPoint.distance(endPoint) > 1) pushEntity(drawStack);
				else Log.i(tag, "Shape points are not distinct. Ignoring.");

				doDraw = false;
			}
			else Log.w(tag, "Index did not match. Ignoring event.");
		}
		else {
			Log.e(tag, "Unknown and thus unhandled touch event!");
			return false; // Touch event not handled.
		}
		
		return true;
	}
	
	/**
	 * All shape handlers must implement the pushEntity method. Quite simply,
	 * it must create a relevant Entity object and push it onto the drawStack,
	 * finalising a draw action.
	 * @param drawStack The stack of draw objects.
	 */
	public abstract void pushEntity(EntityGroup drawStack);
	
	/**
	 * All handlers must define a drawBuffer method that draws their current UI
	 * output on a passed canvas.
	 * @param canvas The canvas to draw onto.
	 */
	@Override 
	public final void drawBuffer(Canvas canvas) {
		if (doDraw) {
			updateBuffer();
			bufferedEntity.draw(canvas);
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		drawBuffer(canvas);
	}
	
	/**
	 * When called, expects the internal PrimitiveEntity to be updated and
	 * returned.
	 */
	public abstract PrimitiveEntity updateBuffer();
	
	/**
	 * Helper function that recalculates the bounds of a rectangle based on
	 * start and end points. This is useful for most shapes, as they all must
	 * define a hitbox they are drawn within (this is true for rectangles,
	 * ovals and lines).
	 */
	protected void calcRectBounds(){
		top = Math.min(startPoint.y, endPoint.y);
		bottom = Math.max(startPoint.y, endPoint.y);
		left = Math.min(startPoint.x, endPoint.x);
		right = Math.max(startPoint.x, endPoint.x);
	}
}
