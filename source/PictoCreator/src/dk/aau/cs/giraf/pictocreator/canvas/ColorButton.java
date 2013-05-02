package dk.aau.cs.giraf.pictocreator.canvas;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class ColorButton extends ImageButton {

	protected ColorChangeGestureDetector gestureDetector;
	
	DrawView drawView;
	
	public ColorButton(DrawView drawView, Context context) {
		super(context);
		init(drawView, context);
	}

	public ColorButton(DrawView drawView, Context context, AttributeSet attrs) {
		super(context, attrs);
		init(drawView, context);
	}

	public ColorButton(DrawView drawView, Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(drawView, context);
	}
	
	private void init(DrawView dv, Context context) {
		gestureDetector = new ColorChangeGestureDetector(context, new GestureDetector.SimpleOnGestureListener());
		this.drawView = dv;
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			Log.i("ColorButton", "Touch event handled (by long?)");
			return true;
		} else {
			Log.i("ColorButton", "Touch event handled regularly.");
			return false;
		}
	}

}
