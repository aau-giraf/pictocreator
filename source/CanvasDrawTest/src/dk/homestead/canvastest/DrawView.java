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
	 * Width and height of the view.
	 */
	int width, height;
	
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
	Bitmap drawBuffer;
	
	/**
	 * The canvas used to draw on the drawBuffer.
	 */
	Canvas bufferCanvas;
	
	/**
	 * This stack contains all current layers in the *drawing*. See drawStack
	 * for the entire rendering stack used.
	 */
	EntityGroup drawStack;
	
	/**
	 * The toolbox, drawn to the left
	 */
	EntityGroup toolbox;
	
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
		Log.i("DrawView:init_stuff", "Initialisations!");
		paint.setARGB(255, 0, 0, 0);
		drawnShape = new OvalShape();
		drawnShape.resize(200, 100);
		shape = new ShapeDrawable(drawnShape);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		Log.i("DrawView:onSizeChanged", "Size changed!");
		this.width = w;
		this.height = h;
		initBuffers();
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	protected void initBuffers(){
		drawBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bufferCanvas = new Canvas(drawBuffer);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.w("DrawView",  "Invalidated. Redrawing.");
		
		// Drawing order: drawStack, drawBuffer, renderStack (toolbox).
		//drawStack.draw(canvas);
		
		canvas.drawBitmap(drawBuffer, 0, 0, null);
		
		// toolbox.draw(canvas);
		
		// Clear
		// canvas.drawRGB(r,  g,  b);
		/*
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
		// Draw toolbox bitmap?
		*/
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int index = event.getActionIndex();
		int action = event.getActionMasked();
		action = event.getAction();
	
		drawx = event.getX(index);
		drawy = event.getY(index);
		
		if (action == MotionEvent.ACTION_DOWN){
			Log.i("DrawView:onTouchEvent", "Finger was placed.");
			drawing = true;
		}
		else if (action == MotionEvent.ACTION_UP){
			Log.i("DrawView:onTouchEvent", "Finger was raised.");
			drawing = false;
		}
		else if (action == MotionEvent.ACTION_MOVE){
			Log.i("DrawView:onTouchEvent", "Finger moved.");
			bufferCanvas.drawCircle(drawx, drawy, 10, paint);
		}
		else{
			Log.w("DrawView:onTouchEvent", "Unknown touch event!");
		}
		
		invalidate();
		
		return true;
	}
	
	
}
