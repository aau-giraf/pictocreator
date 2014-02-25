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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import dk.aau.cs.giraf.pictocreator.canvas.entity.BitmapEntity;

/**
 * The DrawView is a basic draw stack with a rendering loop that attempts
 * to detect when its surface has been dirtied and must be re-rendered.
 * Most interaction and processing has been delegated to DrawFragment
 * and the various ActionHandler subclasses.
 * @author lindhart
 */
public class DrawView extends View {

	/**
	 * Application context. Used when flattening the Bitmap for storing to
	 * disk.
	 * @todo Remove. This should not be handled by DrawView. Too high
	 * coupling.
	 */
    private Context context;

    /**
     * Width of this DrawView. Registered when the onSizeChanged event is fired
     * through the Android framework.
     */
	protected int width;
	
	/**
	 * Height of this DrawView. Registered when the onSizeChanged event is fired
	 * through the Android framework.
	 */
	protected int height;

	/**
	 * This stack contains all current layers in the *drawing*. See drawStack
	 * for the entire rendering stack used.
	 */
	EntityGroup drawStack = new EntityGroup();

	/**
	 * The currently active handler for touch events.
	 */
	protected ActionHandler currentHandler;

	/**
	 * Bounds for the image. These bounds will be used when saving to a bitmap,
	 * while the entire drawStack is saved when stored as XML.
	 */
	Rect imageBounds;

	/**
	 * The Paint used to draw the bounds of the Pictogram (the dark shaded area
	 * around the main square).
	 */
	Paint boundsPaint = new Paint();

	/**
	 * The current colour used for strokes. Defaults to opaque black.
	 */
	private int strokeColor = 0xFF000000;
	
	/**
	 * The current colour used for filling. Defaults to semi-translucent red.
	 */
	private int fillColor = 0xCCFF0000;
	
	/**
	 * The width of strokes.
	 */
	private float strokeWidth = 4;

	/**
	 * Creates a new DrawView.
	 * @param context Application context as required by View.
	 */
	public DrawView(Context context) {
		super(context);
                this.context = context;
		initStuff();
	}

	/**
	 * Creates a new DrawView.
	 * @param context Application context as required by View.
	 * @param attrs AttributeSet as required by View.
	 */
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
                this.context = context;

		initStuff();
	}

	/**
	 * Creates a new DrawView.
	 * @param context Application context as required by View.
	 * @param attrs AttributeSet as required by View.
	 * @param defStyle DefStyle as required by View.
	 */
	public DrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
                this.context = context;
		initStuff();
	}

	/**
	 * Shared initialiser for all constructors. Sets default values.
	 */
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

	/**
	 * Recalculates the bounds for the canvas drawing area based on the current
	 * width and height of the DrawView. Called during initialisation and every
	 * time the size of the view changes.
	 */
	protected void resetImageBounds() {
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
	 * Sets the active handler. The handler's color and stroke settings are
	 * set with those current to the DrawView.
	 * @param handler The handler to use.
	 */
	public void setHandler(ActionHandler handler) {
		this.currentHandler = handler;
		this.currentHandler.setFillColor(fillColor);
		this.currentHandler.setStrokeColor(strokeColor);
		this.currentHandler.setStrokeWidth(strokeWidth);
	}

	/**
	 * Retrieves the ActionHandler currently active.
	 * @return The current ActionHandler - reference.
	 */
	public ActionHandler getCurrentHandler() {
		return currentHandler;
	}

	/**
	 * Sets the fill colour of the DrawView and, by recursion, the active
	 * ActionHandler.
	 * @param color The new colour in ARGB format.
	 */
	public void setFillColor(int color) {
		this.currentHandler.setFillColor(color);
		this.fillColor = color;
		invalidate(); // Handler may need redraw.
	}

	/**
	 * Sets the stroke colour of the DrawView and, by recursion, the active
	 * ActionHandler.
	 * @param color The new colour in ARGB format.
	 */
	public void setStrokeColor(int color) {
		this.currentHandler.setStrokeColor(color);
		this.strokeColor = color;
		invalidate(); // Handler may need redraw.
	}

	/**
	 * Sets a new stroke width for use when painting the edges of an object.
	 * @param width New width of the stroke.
	 */
	public void setStrokeWidth(float width) {
		this.currentHandler.setStrokeWidth(width);
		this.strokeWidth = width;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.w("DrawView",  "Invalidated. Redrawing.");

		// Drawing order: drawStack, drawBuffer, bounds.
		drawStack.draw(canvas);

		if (currentHandler != null) currentHandler.drawBufferPreBounds(canvas);

		drawBounds(canvas);

		if (currentHandler != null) currentHandler.drawBufferPostBounds(canvas);
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
	 * Saves the bitmap to a predetermined path (why the *fuck*?)
	 * @return File object pointing to the saved Bitmap.
	 * @throws FileNotFoundException
	 * @todo Refactor the API. DrawView was never intended to handle the saving itself.
	 */
	public File saveToBitmap(Bitmap.Config config) throws FileNotFoundException {
		String imageFile = "GImage_canvas.jpg";

		File retF = new File(this.context.getCacheDir(), "cvs/" + imageFile);
		
		getFlattenedBitmap(config).compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(retF));

		return retF;
	}
	
	/**
	 * Flattens the draw stack and returns it as a Bitmap.
	 * @param config Optional config of the Bitmap. If null, default values are used.
	 * @return The drawstack, flattened to a single image.
	 */
	public Bitmap getFlattenedBitmap(Bitmap.Config config) {
		Bitmap toRet = Bitmap.createBitmap(this.width, this.height, config);

		Canvas buff = new Canvas(toRet);

		// We use the existing draw method to create the buffer. Although it's more expensive in cycles and memory,
		// I suspect the user doesn't mind the time spent when they are saving, anyway.
		this.draw(buff);

		Bitmap doRet = Bitmap.createBitmap(toRet, imageBounds.left, imageBounds.top, imageBounds.width(), imageBounds.height());

		return doRet;
	}

	/**
	 * Add a new BitmapEntity from a Bitmap source instance.
	 * @param src The source data.
	 */
	public void loadFromBitmap(Bitmap src) {
		drawStack.addEntity(new BitmapEntity(src));
		invalidate();
	}
}