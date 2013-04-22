package dk.homestead.canvastest.handlers;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.MotionEvent;
import dk.homestead.canvastest.ActionHandler;
import dk.homestead.canvastest.EntityGroup;

/**
 * An ActionHandler specifically for drawing shapes. This aggregates such
 * things as stroke and fill color.
 * @author lindhart
 */
public abstract class ShapeHandler extends ActionHandler {
	
	protected Paint paint;
	
	/**
	 * Color of the shapes outline.
	 */
	protected int strokeColor;
	
	/**
	 * Color of the shapes fill (the inside color).
	 */
	protected int fillColor;
	
	public ShapeHandler(Bitmap buffersrc) {
		super(buffersrc);
		// TODO Auto-generated constructor stub
	}
	
	public void setStrokeColor(int color) { this.strokeColor = color; }
	public void setFillColor(int color) { this.fillColor = color; }

}
