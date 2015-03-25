package dk.aau.cs.giraf.pictocreator.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.R;


/**
 * The preview button is optimally displayed close to regular ColorButtons and
 * signals the current color state for fill and stroke.
 * @author Croc
 */
public class PreviewButton extends GButton {
	private Paint fillPaint = new Paint();
	private Paint strokePaint = new Paint();
    private int textWeightPadding;
    private int textHeightPadding;

    private Paint linePaint = new Paint();

    private DrawType drawtype = DrawType.RECTANGLE;

    /**
     * Sets the stroke color to use. Causes a re-render of the preview Bitmap.
     * @param c Color to use. 0 is transparent ("null") while 0xFF?????? is opaque.
     */
    public void setStrokeColor(int c) {
        strokePaint.setColor(c);
        this.invalidate();
    }

    /**
     * Sets the fill color to use. Causes a re-render of the preview Bitmap.
     * @param c Color to use. 0 is transparent ("null") while 0xFF?????? is opaque.
     */
    public void setFillColor(int c) {
        fillPaint.setColor(c);
        linePaint.setColor(c);
        this.invalidate();
    }

    /**
     * Retrieves the current stroke color in use.
     * @return Returns the currently previewed stroke colour as ARGB.
     */
    public int getStrokeColor() { return strokePaint.getColor(); }

    /**
     * Retrieves the fill color currently in use.
     * @return Returns the currently previewed fill colour as ARGB.
     */
    public int getFillColor() { return fillPaint.getColor(); }

    /**
     * Padding for the previewed entity
     */
	public int padding = 20;

    public void setPadding(int padding) { this.padding = padding; }

    /**
     * Retrieves the stroke width currently in use.
     * @return Returns the currently previewed stroke width.
     */
	public float getStrokeWidth() {
		return strokePaint.getStrokeWidth();
	}

    /**
     * Sets the stroke width of the previewed entity.
     * @param width
     */
	public void setStrokeWidth(float width) {
		strokePaint.setStrokeWidth(width);
        linePaint.setStrokeWidth(width);
		invalidate();
	}


    public PreviewButton(Context context) {
		super(context);
		
		init();
	}

	public PreviewButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public PreviewButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// Some default coloring and width.
		setFillColor(0xFFFF00FF);
		setStrokeColor(0xFFFF0000);
		setStrokeWidth(4);

		fillPaint.setStyle(Style.FILL);
		strokePaint.setStyle(Style.STROKE);
        fillPaint.setTextSize(20);
        textWeightPadding = padding + 32;
        textHeightPadding = padding + 20;

    }

    /**
     * Draws an icon on the canvas based on the selected entity
     */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        if (drawtype == DrawType.RECTANGLE){
            canvas.drawRect(padding, padding, canvas.getWidth() - padding, canvas.getHeight() - padding, fillPaint);
            canvas.drawRect(padding, padding, canvas.getWidth() - padding, canvas.getHeight() - padding, strokePaint);
        }
        else if (drawtype == DrawType.CIRCLE){
            canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, (canvas.getWidth()-(2*padding))/2, fillPaint);
            canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, (canvas.getWidth()-(2*padding))/2, strokePaint);
        }
        else if (drawtype == DrawType.LINE){
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeJoin(Paint.Join.ROUND);
            linePaint.setStrokeCap(Paint.Cap.ROUND);

            Path p = new Path();
            p.moveTo(padding,padding);
            p.lineTo(canvas.getWidth()-padding, canvas.getHeight()-padding);

            //drawLine ignores the style of the paint, which means the edges of the line are not rounded, so drawPath is used instead.
            canvas.drawPath(p, linePaint);
        }
        else if (drawtype == DrawType.SELECT){
            Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.select);
            canvas.drawBitmap(mBitmap, padding, padding, null);
        }
        else if (drawtype == DrawType.TEXT)
        {
            canvas.drawRect(padding, padding, canvas.getWidth() - padding, canvas.getHeight() - padding, strokePaint);
            canvas.drawText("A", canvas.getWidth() - textWeightPadding, canvas.getHeight() - textHeightPadding, fillPaint);
        }
	}

	public void swapColors() {
        int tempColor = fillPaint.getColor();
		setFillColor(strokePaint.getColor());
		setStrokeColor(tempColor);
        invalidate();
	}

    public void changePreviewDisplay(DrawType type){
        this.drawtype = type;
        invalidate();
    }

}
