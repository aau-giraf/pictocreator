package dk.aau.cs.giraf.pictocreator.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;

import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.gui.R;

/**
 * The preview button is optimally displayed close to regular ColorButtons and
 * signals the current color state for fill and stroke.
 * @author Croc
 */
public class PreviewButton extends GirafButton {
	private Paint fillPaint = new Paint();
	private Paint strokePaint = new Paint();

    private int textWidthPadding;
    private int textHeightPadding;

    private Paint textPaint = new Paint();

    private Paint linePaint = new Paint();

    private DrawType drawtype = DrawType.RECTANGLE;

    /**
     * Sets the stroke color to use. Causes a re-render of the preview Bitmap.
     * @param c Color to use. 0 is transparent ("null") while 0xFF?????? is opaque.
     */
    public void setStrokeColor(int c) {
        strokePaint.setColor(c);
        linePaint.setColor(c);
        setTextPaint(c);
        this.invalidate();
    }

    /**
     * Sets the fill color to use. Causes a re-render of the preview Bitmap.
     * @param c Color to use. 0 is transparent ("null") while 0xFF?????? is opaque.
     */
    public void setFillColor(int c) {
        fillPaint.setColor(c);
        this.invalidate();
    }

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
        textPaint.setTextSize(28 + width);
        linePaint.setStrokeWidth(width);
		invalidate();
	}


    public PreviewButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		// Some default coloring and width.
		setStrokeWidth(4);

		fillPaint.setStyle(Style.FILL);
		strokePaint.setStyle(Style.STROKE);
        textPaint.setTextSize(28);
        textPaint.setStyle(Style.STROKE);
        textWidthPadding = padding + 30;
        textHeightPadding = padding + 10;
    }

/*    *//**
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
        else if (drawtype == DrawType.ERASER){
            Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), dk.aau.cs.giraf.pictocreator.R.drawable.icon_eraser);
            canvas.drawBitmap(mBitmap, padding, padding, null);
        }
        else if (drawtype == DrawType.TEXT)
        {
            canvas.drawRect(padding, padding, canvas.getWidth() - padding, canvas.getHeight() - padding, fillPaint);
            canvas.drawText("A", canvas.getWidth() - textWidthPadding, canvas.getHeight() - textHeightPadding, textPaint);
        }
	}

    public void setTextPaint(int color)
    {
        textPaint.setColor(color);
    }

    public void changePreviewDisplay(DrawType type){
        this.drawtype = type;
        invalidate();
    }
}
