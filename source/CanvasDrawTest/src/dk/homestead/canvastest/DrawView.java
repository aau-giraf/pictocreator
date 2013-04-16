package dk.homestead.canvastest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.*;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {

	enum DRAW_MODE { FREEHAND, LINE, BOX };
	
	/**
	 * Current draw mode, selected from the tool box.
	 */
	DRAW_MODE curDrawMode = DRAW_MODE.FREEHAND;
	
	/**
	 * Location of the touch event. 
	 */
	float drawx,drawy;
	
	/**
	 * Are we currently shaping something or drawing freehand?
	 * This matches a "fingerDown" boolean, denoting that the user's finger
	 * is still pressed to the screen.
	 */
	boolean drawing = false;
	
	
	// Various DEBUG stuff.
	ShapeDrawable shape;
	Shape drawnShape;
	Paint paint = new Paint();
	
	/**
	 * The buffer holds what is "currently being drawn" by the user. This is a
	 * highly volatile drawing area, compared to the draw stack that simply
	 * contains the layers of drawn stuff so far.
	 */
	Bitmap buffer;
	
	/**
	 * This stack contains all current layers in the *drawing*. See drawStack
	 * for the entire rendering stack used.
	 */
	EntityGroup layers;
	
	public DrawView(Context context) {
		super(context);
		init_stuff();
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init_stuff();
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init_stuff();
	}

	/**
	 * Generic initialisations.
	 */
	protected void init_stuff()
	{
		paint.setARGB(255, 0, 0, 0);
		drawnShape = new OvalShape();
		drawnShape.resize(200, 100);
		shape = new ShapeDrawable(drawnShape);
	}
	
    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    /**
     * Not sure this is even used anymore.
     * @author lindhart
     */
    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            DrawView.this.update();
            DrawView.this.invalidate();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
    
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.w("DrawView",  "Invalidated. Redrawing.");
		// super.onDraw(canvas);
		// Clear
		// canvas.drawRGB(r,  g,  b);
		if (drawing)
		{
			canvas.drawCircle(drawx, drawy, 10, paint);
			
		}
		canvas.save();
		canvas.translate(50,  50);
		canvas.scale(0.3f, 0.3f);
		shape.draw(canvas);
		drawnShape.draw(canvas, paint);
		canvas.restore();
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		drawx = event.getX();
		drawy = event.getY();
		drawing = true;
	
		int index = event.getActionIndex();
		int action = event.getActionMasked();
		if ((action & MotionEvent.ACTION_UP) != 0){
			Log.i("DrawView:onTouchEvent", "Finger was raised.");
		}
		else if ((action & MotionEvent.ACTION_DOWN) != 0){
			Log.i("DrawView:onTouchEvent", "Finger was placed.");
		}
		
		invalidate();
		
		return true;
	}
	
	
}
