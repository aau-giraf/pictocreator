package dk.aau.cs.giraf.pictocreator.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * The preview button is optimally displayed close to regular ColorButtons and
 * signals the current color state for fill and stroke.
 * @author lindhart
 */
public class PreviewButton extends ImageButton {

	private int fillColor;
	private int strokeColor;
	
	public int padding = 4;
	public int strokeWidth = 4;
	
	public PreviewButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PreviewButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PreviewButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private void init() {
		// Some default coloring.
		setFillColor(0xFFFFFFFF);
		setStrokeColor(0xFF000000);
	}
	
	/**
	 * Sets the stroke color to use. Causes a re-render of the preview Bitmap.
	 * @param c Color to use. 0 is transparent ("null") while 0xFF?????? is opaque.
	 */
	public void setStrokeColor(int c) {
		strokeColor = c;
		redraw();
	}
	
	/**
	 * Sets the fill color to use. Causes a re-render of the preview Bitmap.
	 * @param c Color to use. 0 is transparent ("null") while 0xFF?????? is opaque.
	 */
	public void setFillColor(int c) {
		fillColor = c;
		redraw();
	}
	
	/**
	 * Retrieves the current stroke color in use.
	 * @return
	 */
	public int getStrokeColor() { return strokeColor; }
	
	/**
	 * Retrieves the fill color currently in use.
	 * @return
	 */
	public int getFillColor() { return fillColor; }
	
	/**
	 * Redraws and resets the Bitmap used to display this button.
	 */
	public void redraw() {
		Bitmap b = Bitmap.createBitmap(this.getWidth()/2, this.getHeight()/2, Config.ARGB_8888);
		Canvas c = new Canvas(b);
		
		RectF r = new RectF(padding, padding, b.getWidth()-padding, b.getHeight()-padding);
		
		Paint p = new Paint();
		p.setColor(getFillColor());
		p.setStyle(Style.FILL);
		c.drawRect(r, p);
		p.setColor(getStrokeColor());
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(strokeWidth);
		c.drawRect(r, p);
		
		this.setImageBitmap(b);
		
		// this.setBackgroundColor(getFillColor());
		
	}
}
