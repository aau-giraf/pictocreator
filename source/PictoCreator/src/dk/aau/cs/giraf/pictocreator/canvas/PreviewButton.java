package dk.aau.cs.giraf.pictocreator.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

/**
 * The preview button is optimally displayed close to regular ColorButtons and
 * signals the current color state for fill and stroke.
 * @author lindhart
 */
public class PreviewButton extends ImageButton {
	private Paint fillPaint = new Paint();
	private Paint strokePaint = new Paint();
	
	public int padding = 20;
	
	public float getStrokeWidth() {
		return strokePaint.getStrokeWidth();
	}
	
	public void setStrokeWidth(float width) {
		strokePaint.setStrokeWidth(width);
	}
	
	public PreviewButton(Context context) {
		super(context);
		
		init();
	}

	public PreviewButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public PreviewButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	private void init() {
		// Some default coloring.
		setFillColor(0xFFFF00FF);
		setStrokeColor(0xFFFF0000);
		setStrokeWidth(4);
		
		fillPaint.setStyle(Style.FILL);
		strokePaint.setStyle(Style.STROKE);
	}
	
	/**
	 * Sets the stroke color to use. Causes a re-render of the preview Bitmap.
	 * @param c Color to use. 0 is transparent ("null") while 0xFF?????? is opaque.
	 */
	public void setStrokeColor(int c) {
		strokePaint.setColor(c);
		this.invalidate();
	}
	
	/**
	 * Sets the fill color to use. Causes a re-render of the preview Bitmap.
	 * @param c Color to use. 0 is transparent ("null") while 0xFF?????? is opaque.
	 */
	public void setFillColor(int c) {
		fillPaint.setColor(c);
		this.invalidate();
	}
	
	/**
	 * Retrieves the current stroke color in use.
	 * @return
	 */
	public int getStrokeColor() { return strokePaint.getColor(); }
	
	/**
	 * Retrieves the fill color currently in use.
	 * @return
	 */
	public int getFillColor() { return fillPaint.getColor(); }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawRect(padding, padding, canvas.getWidth()-padding, canvas.getHeight()-padding, fillPaint);

		canvas.drawRect(padding, padding, canvas.getWidth()-padding, canvas.getHeight()-padding, strokePaint);
	}

}
