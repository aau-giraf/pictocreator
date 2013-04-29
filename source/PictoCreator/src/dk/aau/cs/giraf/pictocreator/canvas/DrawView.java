package dk.aau.cs.giraf.pictocreator.canvas;

import dk.aau.cs.giraf.pictocreator.canvas.handlers.FreehandHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.LineHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.OvalHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.RectHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.SelectionHandler;
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
	/**
	 * Width and height of the view.
	 */
	int width, height;
	
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
	Toolbox toolbox = new Toolbox(64, this.getHeight());
	
	/**
	 * The currently active handler for touch events.
	 */
	ActionHandler currentHandler;

	Paint redPaint = new Paint();
	
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
		redPaint.setColor(0xFFFF0000);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.i("DrawView:onSizeChanged", "Size changed!");
		this.width = w;
		this.height = h;
		initBuffers();
		
		// Set up the toolbox.
		
		toolbox = new Toolbox(64, h);
		toolbox.addHandler(new SelectionHandler());
		toolbox.addHandler(new RectHandler());
		toolbox.addHandler(new OvalHandler());
		toolbox.addHandler(new LineHandler());
		toolbox.addHandler(new FreehandHandler());
		
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
		toolbox.getCurrentHandler().drawBuffer(canvas);
		
		toolbox.draw(canvas);

		canvas.drawText(String.valueOf(drawStack.size()), 30, 30, redPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Always draw for now. Very inefficient, but the optimisation is too demanding for now.
		// We need some way of messaging a "dirty" state of parts of the draw stack. 
		invalidate();
		
		if (toolbox.onTouch(event)) {
			return true;
		}
		else if (toolbox.getCurrentHandler().onTouchEvent(event, drawStack)){
			return true;
		}
		else return false;
	}
}
