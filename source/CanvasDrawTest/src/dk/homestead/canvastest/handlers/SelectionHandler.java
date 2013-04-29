package dk.homestead.canvastest.handlers;

import java.util.ArrayList;
import java.util.Currency;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;
import android.view.MotionEvent;
import dk.homestead.canvastest.ActionHandler;
import dk.homestead.canvastest.Entity;
import dk.homestead.canvastest.EntityGroup;
import dk.homestead.canvastest.FloatPoint;

public class SelectionHandler extends ActionHandler {

	protected Entity selectedEntity;
	
	protected int currentPointerId;

	/**
	 * Offset difference between where the pointer was registered, and the
	 * top-left corner of the selected Entity's hitbox. Used for more natural
	 * dragging of Entity objects.
	 */
	protected FloatPoint offset;
	
	/**
	 * The location of the Pointer the last place we saw it.
	 */
	protected FloatPoint previousPointerLocation;
	
	/**
	 * Paint used for highlighting.
	 */
	protected Paint hlPaint = new Paint();
	
	public SelectionHandler() {
		hlPaint.setStyle(Style.STROKE);
		hlPaint.setPathEffect(new DashPathEffect(new float[]{10.0f,10.0f,5.0f,10.0f}, 0));
		hlPaint.setColor(0xFF000000);
		hlPaint.setStrokeWidth(2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, EntityGroup drawStack) {
		// Determine type of action.
		// Down: find first collision, highlight.
		// Move: reposition by delta.
		// Up: stop. Un-select is a different gesture.
		String dtag = "SelectionHandler.onTouchEvent";
		
		int index = event.getActionIndex();
		int pointerId = event.getPointerId(index);
		
		
		
		if (pointerId != currentPointerId) {
			Log.i(dtag, "Unregistered pointer. Ignoring.");
		}
		
		float px = event.getX(index);
		float py = event.getY(index);
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN : {
			currentPointerId = pointerId;
			// Attempt to select an Entity.
			previousPointerLocation = new FloatPoint(px, py);
			selectedEntity = drawStack.getCollidedWithPoint(px, py);
			if (selectedEntity != null) {
				// Calculate offset for moving.
				return true;
			}
			else return false;
		}
		case MotionEvent.ACTION_MOVE : {
			if (pointerId == currentPointerId && selectedEntity != null) {
				FloatPoint diff = new FloatPoint(
						px-previousPointerLocation.x,
						py-previousPointerLocation.y);
				selectedEntity.setX(selectedEntity.getX()+diff.x);
				selectedEntity.setY(selectedEntity.getY()+diff.y);
				previousPointerLocation = new FloatPoint(px, py);
				return true;
			}
			else return false;
		}
		default : return false;
		}
	}

	@Override
	public Bitmap getToolboxIcon(int size) {
		Bitmap toRet = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas tmp = new Canvas(toRet);
		
		tmp.drawRect(8, 8, size-8, size-8, hlPaint);
		
		return toRet;
	}

	@Override
	public void drawBuffer(Canvas canvas) {
		if (selectedEntity != null) {
			drawHighlighted(canvas);
		}
	}

	/**
	 * Similar to draw
	 * @param canvas
	 */
	public void drawHighlighted(Canvas canvas) {
		// No need to draw the Entity through the buffer. It is already on the draw stack.
		// selectedEntity.draw(canvas); // Perform normal draw operation.
		
		// Now highlight.
		canvas.drawRect(
				selectedEntity.getHitboxLeft(),
				selectedEntity.getHitboxTop(),
				selectedEntity.getHitboxRight(),
				selectedEntity.getHitboxBottom(),
				hlPaint);
		/*
		RectShape rs = new RectShape();
		rs.resize(getWidth(), getHeight());
		canvas.translate(getX(), getY());
		canvas.rotate(getAngle());
		rs.draw(canvas, hlPaint);
		*/
		// TODO: Support rotated Entity.
		// canvas.drawRect(getHitboxLeft(), getHitboxTop(), getHitboxRight(), getHitboxBottom(), hlPaint);
	}
}
