package dk.homestead.canvastest;

import dk.homestead.canvastest.handlers.LineHandler;
import dk.homestead.canvastest.handlers.OvalHandler;
import dk.homestead.canvastest.handlers.RectHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.*;
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
	EntityGroup drawStack = new EntityGroup();
	
	/**
	 * The toolbox, drawn to the left
	 */
	EntityGroup toolbox;
	
	/**
	 * The currently active handler for touch events.
	 */
	ActionHandler currentHandler;
	
	
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
		// DEBUG
		//currentHandler = new OvalHandler(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
		//currentHandler = new RectHandler(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
		currentHandler = new LineHandler(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
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
		drawStack.draw(canvas);
		
		// canvas.drawBitmap(currentHandler.getBuffer(), 0, 0, null);
		currentHandler.draw(canvas);
		
		// toolbox.draw(canvas);
		// DEBUG: Draw entity count.
		Paint redPaint = new Paint();
		redPaint.setColor(0xFFFF0000);
		canvas.drawText(String.valueOf(drawStack.size()), 30, 30, redPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (currentHandler.onTouchEvent(event, drawStack)){
			invalidate();
			return true;
		}
		else return false;
	}
	
	
}
