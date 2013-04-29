package dk.aau.cs.giraf.pictocreator.canvas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {

	protected int width, height;
	
	/**
	 * This stack contains all current layers in the *drawing*. See drawStack
	 * for the entire rendering stack used.
	 */
	EntityGroup drawStack = new EntityGroup();
	
	/**
	 * The currently active handler for touch events.
	 */
	ActionHandler currentHandler;
	
	/**
	 * Bounds for the image. These bounds will be used when saving to a bitmap,
	 * while the entire drawStack is saved when stored as XML.
	 */
	Rect imageBounds;
	
	Paint boundsPaint = new Paint();
	
	public DrawView(Context context) {
		super(context);
		initStuff();
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initStuff();
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initStuff();
	}

	private void initStuff(){
		resetImageBounds();
		boundsPaint.setColor(0xAA777777);
		boundsPaint.setStrokeWidth(5);
		boundsPaint.setStyle(Style.FILL);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.width = w;
		this.height = h;
		
		resetImageBounds();
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public void resetImageBounds() {
		int padding = 20; // Random padding.
		// Calc middle.
		int middle = this.width/2;
		// Calc height of rect.
		int rHeight = this.height - padding; 
		imageBounds = new Rect(
				middle-(rHeight/2),
				padding,
				middle+(rHeight/2),
				rHeight-(padding/2));
	}
	
	/**
	 * Sets the active handler.
	 * @param handler The handler to use.
	 */
	public void setHandler(ActionHandler handler) {
		this.currentHandler = handler;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.w("DrawView",  "Invalidated. Redrawing.");
		
		// Drawing order: drawStack, drawBuffer, bounds.
		drawStack.draw(canvas);
		
		if (currentHandler != null) currentHandler.drawBuffer(canvas);
		
		drawBounds(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Always draw for now. Very inefficient, but the optimisation is too demanding for now.
		// We need some way of messaging a "dirty" state of parts of the draw stack. 
		invalidate();
		
		if (currentHandler.onTouchEvent(event, drawStack)) {
			return true;
		} else return false;
	}
	
	/**
	 * Draw the bounds rectangle on a canvas. This is an inverted rectangle.
	 * The inner parts of the rectangle are transparent, while the surrounding
	 * area is greyed a bit.
	 * @param canvas The canvas to draw on.
	 */
	public void drawBounds(Canvas canvas) {
		Rect above = new Rect(0, 0, canvas.getWidth(), imageBounds.top);
		Rect left = new Rect(0, imageBounds.top, imageBounds.left, imageBounds.bottom);
		Rect right = new Rect(imageBounds.right, imageBounds.top, canvas.getWidth(), imageBounds.bottom);
		Rect below = new Rect(0, imageBounds.bottom, canvas.getWidth(), canvas.getHeight());

		canvas.drawRect(above, boundsPaint);
		canvas.drawRect(left, boundsPaint);
		canvas.drawRect(right, boundsPaint);
		canvas.drawRect(below, boundsPaint);
	}
	
	/**
	 * Flattens the drawStack and returns it as a Bitmap.
	 * @return A Bitmap representation of the drawStack within the imageBounds.
	 * @throws FileNotFoundException 
	 */
	public Bitmap saveToBitmap(Bitmap.Config config) throws FileNotFoundException {
		Bitmap toRet = Bitmap.createBitmap(this.width, this.height, config);
		
		Canvas buff = new Canvas(toRet);
		
		// We use the existing draw method to create the buffer. Although it's more expensive in cycles and memory,
		// I suspect the user doesn't mind the time spent when they are saving, anyway.
		this.draw(buff);
		
		Bitmap doRet = Bitmap.createBitmap(toRet, imageBounds.left, imageBounds.top, imageBounds.width(), imageBounds.height());
		
		doRet.compress(Bitmap.CompressFormat.JPEG, 100,
				new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES), "crocimagetest.jpg")));
		
		return doRet;
	}
}
