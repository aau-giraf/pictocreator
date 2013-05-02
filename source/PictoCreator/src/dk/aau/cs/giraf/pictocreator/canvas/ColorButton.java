package dk.aau.cs.giraf.pictocreator.canvas;

import dk.aau.cs.giraf.pictocreator.R;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageButton;

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
	
	final Handler handler = new Handler(); 
	Runnable mLongPressed = new Runnable() { 
	    public void run() { 
	        Log.i("", "Long press!");
	        applyStrokeColor();
	    }   
	};
	
	/**
	 * The actual DrawView where we perform drawing operations. Its colors are
	 * updated to reflect those currently chosen.
	 */
	DrawView drawView;
	
	public ColorButton(DrawView drawView, PreviewButton previewButton, int color, Context context) {
		super(context);
		init(drawView, previewButton, color, context);
	}

	public ColorButton(DrawView drawView, PreviewButton previewButton, int color, Context context, AttributeSet attrs) {
		super(context, attrs);
		init(drawView, previewButton, color, context);
	}

	public ColorButton(DrawView drawView, PreviewButton previewButton, int color, Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(drawView, previewButton, color, context);
	}
	
	private void init(DrawView dv, PreviewButton previewButton, int color, Context context) {
		this.drawView = dv;
		this.previewButton = previewButton;
		setColor(color);
	}
	
	protected void applyStrokeColor() {
		drawView.setStrokeColor(getColor());
		previewButton.setStrokeColor(getColor());
	}
	
	protected void applyFillColor() {
		drawView.setFillColor(getColor());
		previewButton.setFillColor(getColor());
	}

	public int getColor() {
		return this.color;
	}
	
	public void setColor(int c) {
		this.color = c;
		setBackgroundColor(c);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
	    if(event.getAction() == MotionEvent.ACTION_DOWN)
	        handler.postDelayed(mLongPressed, 1000); // Initiate the runner for a second's time.
	    if((event.getAction() == MotionEvent.ACTION_MOVE)||(event.getAction() == MotionEvent.ACTION_UP)) {
	        handler.removeCallbacks(mLongPressed);
	        applyFillColor();
	    }
	    return super.onTouchEvent(event);
	}

}
