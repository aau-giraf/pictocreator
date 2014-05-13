package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import dk.aau.cs.giraf.pictocreator.canvas.ActionHandler;
import dk.aau.cs.giraf.pictocreator.canvas.DrawStackSingleton;
import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.entity.BitmapEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;

/**
 * The SelectionHandler is the select/move/rotate/scale tool on the canvas.
 * @author lindhart
 *
 */
public class SelectionHandler extends ActionHandler {

	/**
	 * The currently selected Entity. May be null if no Entity is selected.
	 */
	private Entity selectedEntity;
	
	/**
	 * The ID of the pointer currently being tracked. Check pointerDown to see
	 * if one is *actually* being tracked.
	 */
	private int currentPointerId;

	/**
	 * Tracks whether a pointer is currently being tracked.
	 */
	private boolean pointerDown = false;
	
	/**
	 * Offset difference between where the pointer was registered, and the
	 * top-left corner of the selected Entity's hitbox. Could be used for more natural
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
	
	/**
	 * The Entity that displays the icon for resizing and handles collisions
	 * for interaction with it.
	 */
	protected BitmapEntity resizeIcon;
	
	/**
	 * The Entity that displays the icon for rotating and handles collisions
	 * for interaction with it.
	 */
	protected BitmapEntity rotateIcon;
	
	/**
	 * The Entity that displays the icon for deletion and handles collisions
	 * for interaction with it.
	 */
	protected BitmapEntity scrapIcon;
	
	/**
	 * The Entity that displays the icon for flattening and handles collisions
	 * for interaction with it.
	 */
	protected BitmapEntity flattenIcon;
	
	/**
	 * Square size of the icons. The icons will be resized to iconSizeXiconSize.
	 */
	protected int iconSize = 124;
	
	/**
	 * The ACTION_MODE enum makes explicit the various modes that
	 * SelectionHandler enters during interaction.
	 * @author lindhart
	 *
	 */
	public enum ACTION_MODE {NONE, MOVE, RESIZE, ROTATE};
	
	/**
	 * The currently active SelectionHandler mode. Defaults to none.
	 */
	protected ACTION_MODE currentMode = ACTION_MODE.NONE;
	
	/**
	 * Used to determine whether icons should be rendered or not. Icons are
	 * only rendered when an Entity has been selected.
	 */
	protected boolean showIcons = false;
	
	/**
	 * Used to create smooth rotations when using the rotate action.
	 */
	protected float previousAngle;
	
	/**
	 * Retrieves the actual Bitmap resource for an icon.
	 * @param name Base name of the icon (for example "resize" or "scrap").
	 * pixels variants are built into the package.
	 * @param res The source Resource collection to look in.
	 * @return The matching Bitmap is returned, ready to use. If not found, 
	 * behaviour is undefined.
	 */
	protected static Bitmap getIconBitmap(String name, Resources res) {
		return BitmapFactory.decodeResource(res,
				res.getIdentifier(name, "drawable", "dk.aau.cs.giraf.pictocreator"));
	}
	
	/**
	 * Creates a new SelectionHandler using a specific Resource collection for
	 * icons.
	 * @param resources Resource collection to retrieve icons from. Must
	 * contain iconSize-sized Bitmaps with the naming convention
	 * "<iconName>_icon_<iconSize>x<iconSize>" for resize, rotate, scrap and
	 * flatten.
	 */
	public SelectionHandler(Resources resources) {
		hlPaint.setStyle(Style.STROKE);
		hlPaint.setPathEffect(new DashPathEffect(new float[]{10.0f,10.0f,5.0f,10.0f}, 0));
		hlPaint.setColor(0xFF000000);
		hlPaint.setStrokeWidth(2);
		
		initIcons(resources);
	}

	/**
	 * Retrieves Bitmaps and initialises the BitmapEntity objects for actions.
	 * @param resources Source Resource collection, passed from the constructor.
	 */
	private void initIcons(Resources resources) {
		resizeIcon = new BitmapEntity(getIconBitmap("resize", resources),iconSize);
		rotateIcon = new BitmapEntity(getIconBitmap("rotate", resources),iconSize);
		scrapIcon = new BitmapEntity(getIconBitmap("delete", resources),iconSize);
		flattenIcon = new BitmapEntity(getIconBitmap("flatten", resources),iconSize);
	}
	
	/**
	 * Updates the locations of the icon entities based on the selected Entity.
	 */
	protected void updateIconLocations() {
		flattenIcon.setX(selectedEntity.getHitboxLeft()-flattenIcon.getWidth());
		flattenIcon.setY(selectedEntity.getHitboxTop()-flattenIcon.getHeight());

		resizeIcon.setX(selectedEntity.getHitboxRight());
		resizeIcon.setY(selectedEntity.getHitboxBottom());

		rotateIcon.setX(selectedEntity.getHitboxLeft()-rotateIcon.getWidth());
		rotateIcon.setY(selectedEntity.getHitboxBottom());

		scrapIcon.setX(selectedEntity.getHitboxRight());
		scrapIcon.setY(selectedEntity.getHitboxTop()-scrapIcon.getHeight());
	}
	
	/**
	 * Deletes (scraps) the currently selected Entity. It is removed from the
	 * draw stack.
	 * @param drawStack The draw stack EntityGroup to remove the Entity from.
	 */
	protected void scrapEntity(EntityGroup drawStack) {
		drawStack.removeEntity(selectedEntity);
	}
	
	/**
	 * Flattens the currently selected Entity onto the stack.
	 * @param drawStack
	 * @todo Implement. This method remains unimplemented because the basic
	 * interaction metaphor remains undefined. Should we flatten to a
	 * background Bitmap (and risk moving a top-layer Entity to the back),
	 * flatten from that Entity downwards (with potential side-effecting) or
	 * simply convert the Entity to a Bitmap (confusing the metaphor)?
     * Note from SW608F14: We decided to move the entity into the back
     * of the canvas (front of the drawStack).
     * We leave the previous note in case another implementation is requested.
	 */
	protected void flattenEntity(EntityGroup drawStack) {
        drawStack.moveToBack(this.selectedEntity);
        DrawStackSingleton.getInstance().mySavedData = drawStack;
	}
	
	/**
	 * Simply deselects the Entity by setting selectedEntity to null and 
	 * resetting to default action mode.
	 */
	public void deselect() {
		selectedEntity = null;
		currentMode = ACTION_MODE.NONE;
	}

    /**
     * Touch event which handles the different functionality of the selection tools.
     * @param event The source event, passed uncorrupted from the DrawView parent.
     * @param drawStack The stack of completed drawing operations in the view.
     * Probably only used by SelectionHandler.
     * @return
     */
	@Override
	public boolean onTouchEvent(MotionEvent event, EntityGroup drawStack) {
		// Determine type of action.
		// Down: find first collision, highlight.
		// Move: reposition by delta.
		// Up: stop. Un-select is a different gesture.
		String TouchEventTag = "SelectionHandler.onTouchEvent";
		
		int index = event.getActionIndex();
		int pointerId = event.getPointerId(index);
		int action = event.getAction();

		if (pointerId != currentPointerId) {
			Log.i(TouchEventTag, "Unregistered pointer. Ignoring.");
		}
		
		if (action == MotionEvent.ACTION_UP) {
			Log.i(TouchEventTag, "Pointer raised. Ignoring remainder of logic.");
			pointerDown = false;
			currentPointerId = -1;
			showIcons = true;
			return true;
		}
		
		float px = event.getX(index);
		float py = event.getY(index);
		
		Log.i(TouchEventTag, "Entering phased logic. pointerDown: " + String.valueOf(pointerDown) + ", action:" + String.valueOf(action));
		
		// Determine action or new Entity selection.
		if (action == MotionEvent.ACTION_DOWN && !pointerDown) {
			if (selectedEntity != null) {
				// Attempt to collide with action icon.
				if (resizeIcon.collidesWithPoint(px, py)) currentMode = ACTION_MODE.RESIZE;
				else if (rotateIcon.collidesWithPoint(px, py)) {
					currentMode = ACTION_MODE.ROTATE;
					previousAngle = getAngle(selectedEntity.getCenter(), new FloatPoint(px, py));
				}
				else if (scrapIcon.collidesWithPoint(px, py)) {
					scrapEntity(drawStack);
					deselect();
				}
				else if (flattenIcon.collidesWithPoint(px, py)) {
					flattenEntity(drawStack);
					deselect();
				}
				else if (selectedEntity.collidesWithPoint(px, py)) {
					currentMode = ACTION_MODE.MOVE;
				}
				else {
					Log.i(TouchEventTag, "Attempting to select new Entity.");
					selectedEntity = drawStack.getCollidedWithPoint(px, py);
				}
			} else {
				Log.i(TouchEventTag, "No selected Entity. Trying to find one.");
				selectedEntity = drawStack.getCollidedWithPoint(px, py);
			}
			
			if (selectedEntity == null) deselect();
			else updateIconLocations(); // Icons.
			
			// In all cases, register pointer id etc.
			pointerDown = true;
			currentPointerId = pointerId;
			
			previousPointerLocation = new FloatPoint(px, py);
			
			return true; // Handled.
		}
		else if (pointerDown && selectedEntity != null) {
			if (action == MotionEvent.ACTION_MOVE) {
				boolean handled = false;
				
				switch (currentMode) {
                    case MOVE: {
                        FloatPoint diff = new FloatPoint(
                                px-previousPointerLocation.x,
                                py-previousPointerLocation.y);
                        selectedEntity.setX(selectedEntity.getX()+diff.x);
                        selectedEntity.setY(selectedEntity.getY()+diff.y);
                        handled = true;
                        break;
                    }
                    case RESIZE: {
                        // WARNING! Breaks if the resize icon is NOT in the lower-right corner!
                        float x = selectedEntity.getHitboxRight() - (selectedEntity.getX() +selectedEntity.getWidth());
                        float y = selectedEntity.getHitboxBottom() - (selectedEntity.getY() +selectedEntity.getHeight());

                        float widthNewValue = px - x - selectedEntity.getX();
                        float heightNewValue = py - y - selectedEntity.getY();

                        if(selectedEntity instanceof BitmapEntity)
                        {
                            float ratio = selectedEntity.getHeight() / selectedEntity.getWidth();

                            selectedEntity.setWidth(widthNewValue);
                            selectedEntity.setHeight(widthNewValue * ratio);
                        }
                        else
                        {
                            selectedEntity.setWidth(widthNewValue);
                            selectedEntity.setHeight(heightNewValue);
                        }
                        handled = true;
                        break;
                    }
                    case ROTATE: {
                        float currentAngle = getAngle(selectedEntity.getCenter(), new FloatPoint(px, py));

                        selectedEntity.rotateBy(currentAngle - previousAngle);

                        previousAngle = currentAngle;
                        handled = true;
                        break;
                    }
                    default: {
                        handled = false;
                    }
				}
				
				previousPointerLocation = new FloatPoint(px, py);
				updateIconLocations(); // Icons.
				
				if (currentMode == ACTION_MODE.NONE){
                    showIcons = true;
                }
				else{
                    showIcons = false;
                }
				
				return handled;
			}
		}
		return false;
	}
	
	/**
	 * Helper function, basic math. Calculates the angle between two points
	 * with (0,0) as pivotal point.
	 * @param fromPoint The starting point of the angle.
	 * @param toPoint The ending point of the angle.
	 * @return The angle, in <b>degrees</b>. Use Math.toRadians to convert.
	 */
	public static float getAngle(FloatPoint fromPoint, FloatPoint toPoint) {
	    double dx = (toPoint.x - fromPoint.x);
	    double dy = (toPoint.y - fromPoint.y);

	    double inRads = Math.atan2(dy,dx);

	    return (float)Math.toDegrees(inRads);
	}
	
	@Override
	public Bitmap getToolboxIcon(int size) {
		Bitmap toRet = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas tmp = new Canvas(toRet);
		
		tmp.drawRect(8, 8, size-8, size-8, hlPaint);
		
		return toRet;
	}

	@Override
	public void drawBufferPreBounds(Canvas canvas) {
		if (selectedEntity != null) {
			drawHighlighted(canvas);
		}
	}
	
	@Override
	public void drawBufferPostBounds(Canvas canvas) {
		if (selectedEntity != null) {
			if (showIcons) {
				resizeIcon.draw(canvas);
				rotateIcon.draw(canvas);
				scrapIcon.draw(canvas);
				flattenIcon.draw(canvas);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		drawBufferPreBounds(canvas);
	}
	
	/**
	 * Similar to draw
	 * @param canvas
	 */
	public void drawHighlighted(Canvas canvas) {
		// No need to draw the Entity through the buffer. It is already on the draw stack.
		// selectedEntity.draw(canvas); // Perform normal draw operation.
		
		canvas.save();
		
		RectF hitbox = selectedEntity.getHitbox();
		
		canvas.translate(hitbox.left, hitbox.top);
		canvas.drawRect(0.0f, 0.0f, hitbox.width(), hitbox.height(), hlPaint);
		canvas.restore();
	}
	
	@Override
	public void setStrokeColor(int color) {
		if (selectedEntity != null && ((Object)selectedEntity).getClass().isAssignableFrom(PrimitiveEntity.class)) {
			PrimitiveEntity tmp = (PrimitiveEntity)selectedEntity;
			tmp.setStrokeColor(color);
		}
		super.setStrokeColor(color);
	}
	
	@Override
	public void setFillColor(int color) {
		if (selectedEntity != null && ((Object)selectedEntity).getClass().isAssignableFrom(PrimitiveEntity.class)) {
			PrimitiveEntity tmp = (PrimitiveEntity)selectedEntity;
			tmp.setFillColor(color);
		}
		super.setFillColor(color);
	}
}
