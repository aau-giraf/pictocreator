package dk.aau.cs.giraf.pictocreator.canvas;

import dk.aau.cs.giraf.pictocreator.R;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Small ImageButton extension that uses a blank image to ensure a size, and
 * shows a set background color instead.
 * ColorButtons are used to display the various colour choices in the drawing
 * function and helps setting the colour when the button is pushed.
 * @author lindhart
 */
public class ColorButton extends ImageButton {

	/**
	 * The color of this ColorButton.
	 */
	protected int color;
	
	/**
	 * The button we color to show the user their current choice. Causing
	 * click events in this ColorButton will update the preview to reflect
	 * the change.
	 */
	PreviewButton previewButton;
	
	/**
	 * Did we get a longPress before the previous up event?
	 */
	boolean longPressDetected = false;
	
	/**
	 * 
	 */
	final Handler handler = new Handler();
	
	/**
	 * A Runnable, used to properly detect a long press (>1000ms) when the user
	 * touches the color button. We use this to figure out whether the user
	 * wanted to apply a fill colour (short press) or a stroke colour (long
	 * press).
	 */
	Runnable mLongPressed = new Runnable() { 
	    public void run() { 
	        // Log.i("", "Long press!");
	    	longPressDetected = true;
	        applyStrokeColor();
	    }   
	};
	
	/**
	 * The actual DrawView where we perform drawing operations. Its colors are
	 * updated to reflect those currently chosen.
	 */
	DrawView drawView;
	
	/**
	 * Creates a new ColorButton. Requires expanded arguments, but should be
	 * ready for use like any retrieved View once initialised.
	 * @param drawView The DrawView to reference. When colours the user
	 * interacts with this ColorButton, that DrawView will be affected by the
	 * change.
	 * @param previewButton The PreviewButton instance to bind to. This will be
	 * updated if the user interacts with this button.
	 * @param color The colour to associate with this button.
	 * @param context Context as required by ImageButton.
	 */
	public ColorButton(DrawView drawView, PreviewButton previewButton, int color, Context context) {
		super(context);
		init(drawView, previewButton, color, context);
	}

	/**
	 * Creates a new ColorButton. Requires expanded arguments, but should be
	 * ready for use like any retrieved View once initialised.
	 * @param drawView The DrawView to reference. When colours the user
	 * interacts with this ColorButton, that DrawView will be affected by the
	 * change.
	 * @param previewButton The PreviewButton instance to bind to. This will be
	 * updated if the user interacts with this button.
	 * @param color The colour to associate with this button.
	 * @param context Context as required by ImageButton constructor..
	 * @param attrs Attributes as required by ImageButton constructor.
	 */
	public ColorButton(DrawView drawView, PreviewButton previewButton, int color, Context context, AttributeSet attrs) {
		super(context, attrs);
		init(drawView, previewButton, color, context);
	}

	/**
	 * Creates a new ColorButton. Requires expanded arguments, but should be
	 * ready for use like any retrieved View once initialised.
	 * @param drawView The DrawView to reference. When colours the user
	 * interacts with this ColorButton, that DrawView will be affected by the
	 * change.
	 * @param previewButton The PreviewButton instance to bind to. This will be
	 * updated if the user interacts with this button.
	 * @param color The colour to associate with this button.
	 * @param context Context as required by ImageButton constructor.
	 * @param attrs Attributes as required by ImageButton constructor.
	 * @param defStyle DefStyle as required by ImageButton constructor.
	 */
	public ColorButton(DrawView drawView, PreviewButton previewButton, int color, Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(drawView, previewButton, color, context);
	}
	
	/**
	 * Initialiser method shared by all constructors. Applies the
	 * ColorButton-specific values from the constructor.
	 * @param dv The target DrawView to work with.
	 * @param previewButton The previewbutton to update on ColorButton
	 * interactions.
	 * @param color The colour to associate to this button.
	 * @param context Source context. Used to retrieve a blank image for size.
	 */
	private void init(DrawView dv, PreviewButton previewButton, int color, Context context) {
		this.drawView = dv;
		this.previewButton = previewButton;
		setColor(color);
		setImageResource(R.drawable.blank_32x32);
	}
	
	/**
	 * Applies the ColorButton's colour to the target DrawView and
	 * PreviewButton as a stroke colour.
	 */
	protected void applyStrokeColor() {
		drawView.setStrokeColor(getColor());
		previewButton.setStrokeColor(getColor());
	}
	
	/**
	 * Applies the ColorButton's colour to the target DrawView and
	 * PreviewButton as a stroke colour.
	 */
	protected void applyFillColor() {
		drawView.setFillColor(getColor());
		previewButton.setFillColor(getColor());
	}

	/**
	 * Retrieves this ColorButton's bound colour.
	 * @return The colour as an integer (ARGB).
	 */
	public int getColor() {
		return this.color;
	}
	
	/**
	 * Sets a new colour for this ColorButton.
	 * @param c The new colour in ARGB format. Hint: use 0xFFxxxxxx for a fully
	 * opaque colour.
	 */
	public void setColor(int c) {
		this.color = c;
		setBackgroundColor(c);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int action = event.getAction();
		
	    if(action == MotionEvent.ACTION_DOWN) {
	    	longPressDetected = false; // Reset status.
	        handler.postDelayed(mLongPressed, 1000); // Initiate the runner for a second's time.
	    }
	    else if (action == MotionEvent.ACTION_MOVE) {
	    	handler.removeCallbacks(mLongPressed); // And ignore.
	    }
	    else if(action == MotionEvent.ACTION_UP && !longPressDetected) {
	        handler.removeCallbacks(mLongPressed);
	        applyFillColor();
	    }
	    return super.onTouchEvent(event);
	}

}
