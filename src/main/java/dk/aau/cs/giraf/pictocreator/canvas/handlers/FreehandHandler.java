package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.util.Log;
import android.view.MotionEvent;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.entity.FreehandEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;

/**
 * The FreehandHandler is a kinda-sorta ShapeHandler. The shape is not
 * well-defined, but follows the same basic rules.
 * @author Croc
 */
public class FreehandHandler extends ShapeHandler {

	private String TAG = "FreehandHandler.onTouchEvent";

	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}

	@Override
	public PrimitiveEntity updateBuffer() {
		// The FreehandEntity should manage to handle itself.
        bufferedEntity.setStrokeWidth(getStrokeWidth());
		return bufferedEntity;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, EntityGroup drawStack) {
		int action = event.getAction(); // getActionMasked seems b0rk3d.
		int eventIndex = event.getActionIndex(); // Get the index to the responsible pointer for this event.
		int eventPointerId = event.getPointerId(eventIndex);
		
		Log.i(TAG, "MoveEvent handler invoked!");
		
		if (action == MotionEvent.ACTION_DOWN){
						// Register the ID of the pointer responsible for the down event. This is what we actually track.
			this.currentPointerId = eventPointerId;
			
			bufferedEntity = new FreehandEntity(getStrokeColor());
			
			Log.i(TAG, "Pointer down. Registering.");

            ((FreehandEntity)bufferedEntity).addPoint(event.getX(eventIndex), event.getY(eventIndex));
            ((FreehandEntity)bufferedEntity).addPoint(event.getX(eventIndex) + 1, event.getY(eventIndex) + 1);

			doDraw = true;
		}
		else if (action == MotionEvent.ACTION_MOVE){
			// Assert that the pointer responsible for the event is also the one we're tracking.
			if (bufferedEntity == null) {
				Log.i(TAG, "No active entity. Assuming corrupted MOVE event chain.");
			}
			else if (eventPointerId == this.currentPointerId) {
				((FreehandEntity)bufferedEntity).addPoint(event.getX(eventIndex), event.getY(eventIndex));
				doDraw = true;
			}
			else Log.i(TAG, "Move event. Wrong pointer. Ignoring.");
		}
		else if (action == MotionEvent.ACTION_UP){
			// Finger raised. If we have valid data, store to drawStack and clear.
			// Validate the pointer ID.
			if (bufferedEntity == null) {
				Log.i(TAG, "Cannot add null entity. Assumnig bad MOVE event chain.");
			}
			else if (eventPointerId == this.currentPointerId){
				pushEntity(drawStack);
				doDraw = false;
                bufferedEntity = null;
			}
			else Log.w(TAG, "Index did not match. Ignoring event.");
		}
		else {
			Log.e(TAG, "Unknown and thus unhandled touch event!");
			return false; // Touch event not handled.
		}
		
		return true;
	}
}
