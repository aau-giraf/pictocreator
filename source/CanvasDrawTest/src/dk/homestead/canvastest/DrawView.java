package dk.homestead.canvastest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {

	enum DRAW_MODE { FREEHAND, LINE, BOX };
	
	DRAW_MODE curDrawMode = DRAW_MODE.FREEHAND;
	
	float drawx,drawy;
	
	boolean drawing = false;
	
	
	
	public DrawView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();

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
	
    private int r,g,b = 50;
    
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.w("DrawView",  "Invalidated. Redrawing.");
		// super.onDraw(canvas);
		// Clear
		// canvas.drawRGB(r,  g,  b);
		if (drawing)
		{
			Paint p = new Paint();
			p.setARGB(255, 0, 0, 0);
			canvas.drawCircle(drawx, drawy, 10, p);
			
		}
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		drawx = event.getX();
		drawy = event.getY();
		drawing = true;
		
		invalidate();
		
		return true;
	}
	
}
