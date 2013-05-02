package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.entity.FreehandEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;

/**
 * The FreehandHandler is a kinda-sorta ShapeHandler. The shape is not
 * well-defined, but follows the same basic rules.
 * @author lindhart
 *
 */
public class FreehandHandler extends ShapeHandler {

	private String tag = "FreehandHandler.onTouchEvent";
	
	public FreehandHandler() {
		// Generic paint.
		strokeColor = 0xFF000000;
	}

	@Override
	public void pushEntity(EntityGroup drawStack) {
		drawStack.addEntity(bufferedEntity);
	}

	@Override
	public PrimitiveEntity updateBuffer() {
		// The FreehandEntity should manage to handle itself.
		return bufferedEntity;
	}

	@Override
	public Bitmap getToolboxIcon(int size) {
		Bitmap ret = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(ret);
		
		Paint p = new Paint();
		p.setColor(0xFF000000);
		p.setStyle(Style.STROKE);
		
		RectF rect1 = new RectF(4, 4, size-4, size/2-4);
		
		c.drawArc(rect1, 180, 270, true, p);
		
		return ret;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, EntityGroup drawStack) {
		int action = event.getAction(); // getActionMasked seems b0rk3d.
		int eventIndex = event.getActionIndex(); // Get the index to the responsible pointer for this event.
		int eventPointerId = event.getPointerId(eventIndex);
		
		Log.i(tag, "MoveEvent handler invoked!");
		
		if (action == MotionEvent.ACTION_DOWN){
						// Register the ID of the pointer responsible for the down event. This is what we actually track.
			this.currentPointerId = eventPointerId;
			
			bufferedEntity = new FreehandEntity(strokeColor);
			
			Log.i(tag, "Pointer down. Registering.");
			
			doDraw = true;
		}
		else if (action == MotionEvent.ACTION_MOVE){
			// Assert that the pointer responsible for the event is also the one we're tracking.
			if (bufferedEntity == null) {
				Log.i(tag, "No active entity. Assuming corrupted MOVE event chain.");
			}
			else if (eventPointerId == this.currentPointerId) {
				((FreehandEntity)bufferedEntity).addPoint(event.getX(eventIndex), event.getY(eventIndex));
				doDraw = true;
			}
			else Log.i(tag, "Move event. Wrong pointer. Ignoring.");
		}
		else if (action == MotionEvent.ACTION_UP){
			// Finger raised. If we have valid data, store to drawStack and clear.
			// Validate the pointer ID.
			if (bufferedEntity == null) {
				Log.i(tag, "Cannot add null entity. Assumnig bad MOVE event chain.");
			}
			else if (eventPointerId == this.currentPointerId){
				pushEntity(drawStack);
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
}
