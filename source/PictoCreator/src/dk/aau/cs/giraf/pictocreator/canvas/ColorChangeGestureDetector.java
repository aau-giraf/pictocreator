package dk.aau.cs.giraf.pictocreator.canvas;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ColorChangeGestureDetector extends GestureDetector {

	DrawView drawView;
	
	protected int strokeColor;
	protected int fillColor;
	
	public void setStrokeColor(int c) { strokeColor = c; }
	public void setFillColor(int c) { fillColor = c; }
	
	public ColorChangeGestureDetector(Context context,
			OnGestureListener listener) {
		super(context, listener);
		// TODO Auto-generated constructor stub
	}

	public ColorChangeGestureDetector(Context context,
			OnGestureListener listener, Handler handler) {
		super(context, listener, handler);
		// TODO Auto-generated constructor stub
	}

	public ColorChangeGestureDetector(Context context,
			OnGestureListener listener, Handler handler, boolean unused) {
		super(context, listener, handler, unused);
		// TODO Auto-generated constructor stub
	}

	public void onLongPress(MotionEvent e) {
        Log.e("ColorButton.onLongPress", "Longpress detected");
        drawView.setFillColor(fillColor);
    }

}
