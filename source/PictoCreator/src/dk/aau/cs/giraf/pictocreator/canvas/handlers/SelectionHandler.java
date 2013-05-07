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
import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.entity.BitmapEntity;

public class SelectionHandler extends ActionHandler {

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
	
	protected BitmapEntity resizeIcon, rotateIcon, scrapIcon, flattenIcon;
	
	protected int iconSize = 32;
	
	public enum ACTION_MODE {NONE, MOVE, RESIZE, ROTATE};
	
	protected ACTION_MODE currentMode = ACTION_MODE.NONE;
	
	protected boolean showIcons = false;
	
	protected float previousAngle;
	
	protected static String getFullIconName(String base, int size) {
		return String.format("%s_icon_%sx%s", base, size, size);
	}
	
	protected static Bitmap getIconBitmap(String name, int size, Resources res) {
		return BitmapFactory.decodeResource(res,
				res.getIdentifier(getFullIconName(name, size), "drawable", "dk.aau.cs.giraf.pictocreator"));
	}
	
	public SelectionHandler(Resources resources) {
		hlPaint.setStyle(Style.STROKE);
		hlPaint.setPathEffect(new DashPathEffect(new float[]{10.0f,10.0f,5.0f,10.0f}, 0));
		hlPaint.setColor(0xFF000000);
		hlPaint.setStrokeWidth(2);
		
		initIcons(resources);
	}

	private void initIcons(Resources resources) {
		resizeIcon = new BitmapEntity(getIconBitmap("resize", iconSize, resources));
		rotateIcon = new BitmapEntity(getIconBitmap("rotate", iconSize, resources));
		scrapIcon = new BitmapEntity(getIconBitmap("scrap", iconSize, resources));
		flattenIcon = new BitmapEntity(getIconBitmap("flatten", iconSize, resources));
	}
	
	/**
	 * Updates the locations of the icon entities based on the selected Entity.
	 */
	protected void updateIconLocations() {
		flattenIcon.setX(selectedEntity.getHitboxLeft()-flattenIcon.getWidth());
		flattenIcon.setY(selectedEntity.getHitboxTop()-flattenIcon.getHeight());
		
		resizeIcon.setAngle(90);
		resizeIcon.setX(selectedEntity.getHitboxRight());
		resizeIcon.setY(selectedEntity.getHitboxBottom());
		
		rotateIcon.setAngle(180);
		
		rotateIcon.setX(selectedEntity.getHitboxLeft()-rotateIcon.getWidth());
		rotateIcon.setY(selectedEntity.getHitboxBottom());

		scrapIcon.setX(selectedEntity.getHitboxRight());
		scrapIcon.setY(selectedEntity.getHitboxTop()-scrapIcon.getHeight());
	}
	
	protected static FloatPoint rotatePointAroundPoint(FloatPoint p, float angle, FloatPoint pivot) {
		if (pivot == null) pivot = new FloatPoint(); // Default - around (0,0).
		
		FloatPoint fp = new FloatPoint();
		
		//p'x =             cos(theta) * (px -     ox) -      sin(theta) * (py -     oy) +      ox
		fp.x = (float)(Math.cos(angle) * (p.x-pivot.x) - Math.sin(angle) * (p.y-pivot.y) + pivot.x);
		//p'y =             sin(theta) * (px -     ox) +      cos(theta) * (py -     oy) +      oy
		fp.y = (float)(Math.sin(angle) * (p.x-pivot.x) + Math.cos(angle) * (p.y-pivot.y) + pivot.y);
		
		return fp;
	}
	
	protected void scrapEntity(EntityGroup drawStack) {
		drawStack.removeEntity(selectedEntity);
	}
	
	protected void flattenEntity(EntityGroup drawStack) {
		Log.e("SelectionHandler.flattenEntity", "Method not yet implemented! The Entity should now be placed on the bottom-most Bitmap.");
	}
	
	protected void deselect() {
		selectedEntity = null;
		currentMode = ACTION_MODE.NONE;
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
		int action = event.getAction();
		
		if (pointerId != currentPointerId) {
			Log.i(dtag, "Unregistered pointer. Ignoring.");
		}
		
		if (action == MotionEvent.ACTION_UP) {
			Log.i(dtag, "Pointer raised. Ignoring remainder of logic.");
			pointerDown = false;
			currentPointerId = -1;
			showIcons = true;
			return true;
		}
		
		float px = event.getX(index);
		float py = event.getY(index);
		
		Log.i(dtag, "Entering phased logic. pointerDown: " + String.valueOf(pointerDown) + ", actioN:" + String.valueOf(action));
		
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
					Log.i(dtag, "Attempting to select new Entity.");
					selectedEntity = drawStack.getCollidedWithPoint(px, py);
				}
			} else {
				Log.i(dtag, "No selected Entity. Trying to find one.");
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
					selectedEntity.setWidth(px-selectedEntity.getX());
					selectedEntity.setHeight(py-selectedEntity.getY());
					handled = true;
					break;
				}
				case ROTATE: {
					float currentAngle = getAngle(selectedEntity.getCenter(), new FloatPoint(px, py));
					
					// selectedEntity.setAngle(getAngle(selectedEntity.getCenter(), new FloatPoint(px, py))-previousAngle);
					selectedEntity.rotateBy(currentAngle-previousAngle);
					
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
				
				if (currentMode == ACTION_MODE.NONE) showIcons = true;
				else showIcons = false;
				
				return handled;
			}
		}
		
		return false;
	}
	
	public static float getAngle(FloatPoint fromPoint, FloatPoint toPoint) {
	    double dx = toPoint.x - fromPoint.x;
	    // Minus to correct for coord re-mapping
	    double dy = -(toPoint.y - fromPoint.y);

	    double inRads = Math.atan2(dy,dx);

	    // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
	    if (inRads < 0)
	        inRads = Math.abs(inRads);
	    else
	        inRads = 2*Math.PI - inRads;

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
	public void drawBuffer(Canvas canvas) {
		if (selectedEntity != null) {
			drawHighlighted(canvas);
			
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
		drawBuffer(canvas);
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
		canvas.rotate(selectedEntity.getAngle(), selectedEntity.getWidth()/2, selectedEntity.getHeight()/2);
		canvas.drawRect(0.0f, 0.0f, hitbox.width(), hitbox.height(), hlPaint);
		
		canvas.restore();
	}
}
