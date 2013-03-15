package dk.aau.cs.giraf.pictogram;

import dk.aau.cs.giraf.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class Pictogram extends ViewGroup implements IPictogram{
	
	private String imagePath;
	private String textLabel;
	private String audioPath;
	private long pictogramID;
	
	//Main constructor (no XML)
	public Pictogram(Context context, String image, String text, String audio, long id) {
		super(context);
		this.imagePath = image;
		this.textLabel = text;
		this.audioPath = audio;
		this.pictogramID = id;
	}
	
	//Eventually for XML (if needed) otherwise use other constructor
	public Pictogram(Context context, AttributeSet attrs) {
		super(context, attrs);
		/*
		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Pictogram, 0, 0);
		try {
			pictoHeight = attributes.getInt(R.styleable.Pictogram_height, -1);
			pictoWidth = attributes.getInt(R.styleable.Pictogram_width, -1);
		}
		finally {
			attributes.recycle();
		}*/
		
	}
	
	@Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = super.getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childLeft = getPaddingLeft();
                final int childTop = getPaddingTop();
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());

            }
        }
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                
                int tempHeight = child.getMeasuredHeight();
                int tempWidth = child.getMeasuredWidth();
                
                if(maxHeight < tempHeight) {
                	maxHeight = tempHeight;
                }
                if(maxWidth < tempWidth) {
                	maxWidth = tempWidth;
                }
            }
        }

        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();
        /*
        Drawable drawable = getBackground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }*/

        //setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
        setMeasuredDimension(maxWidth, maxHeight);
    }
	
	@Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
    
	@Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }
 
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
    
    @Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}
	
	@Override
	public void renderAll() {
		renderImage();
		renderText();
	}

	@Override
	public void renderText() {

		PictoText label = new PictoText(getContext());
		label.setGravity(Gravity.BOTTOM);
		this.addView(label);
		invalidate();
	}

	@Override
	public void renderImage() {

		PictoImage pic = new PictoImage(getContext());
		this.addView(pic);
		invalidate();
	}

	@Override
	public void playAudio() {
		
	}

	@Override
	public String[] getTags() {
		return null;
	}

	@Override
	public String getImageData() {
		return null;
	}

	@Override
	public String getAudioData() {
		return null;
	}

	@Override
	public String getTextData() {
		return null;
	}
	
	public void setImagePath(String path) {
		this.imagePath = path;
	}
	
	public String getImagePath() {
		return this.imagePath;
	}
	
	public void setTextLabel(String label) {
		this.textLabel = label;
	}
	
	public String getTextLabel() {
		return this.textLabel;
	}
	
	public void setAudioPath(String path) {
		this.audioPath = path;
	}
	
	public String getAudioPath() {
		return this.audioPath;
	}
	
	public void setPictogramID(long id) {
		this.pictogramID = id;
	}
	
	public long getPictogramID() {
		return this.pictogramID;
	}
	
	private class PictoImage extends ImageView {
		
		private Rect imageBounds;
		private Bitmap image;
		Paint bitmapPaint = new Paint();

		public PictoImage(Context context) {
			super(context);
			//super.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			try {
				image = BitmapFactory.decodeFile(imagePath);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
			setScaleType(ScaleType.CENTER);
			bitmapPaint.setFilterBitmap(true);
			imageBounds = new Rect(0, 0, this.getWidth(), this.getHeight());
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawBitmap(image, null, imageBounds, bitmapPaint);
		}
		
		@Override
		protected void onSizeChanged(int width, int height, int oldwwidth, int oldheight) {
			imageBounds = new Rect(0, 0, width, height);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			int size = MeasureSpec.getSize(widthMeasureSpec);
			String someSize = MeasureSpec.toString(size);
			System.out.println(someSize);
			/*int width = 0;
			int height = 0;
			if(image != null) {
				width = image.getWidth();
				height = image.getHeight();
			}
			else {
				width = MeasureSpec.getSize(widthMeasureSpec);
				height = MeasureSpec.getSize(heightMeasureSpec);
			}			
			setMeasuredDimension(width, height);*/
		}
		
		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);
		}
	}
	
	private class PictoText extends TextView {
		
		Paint textPaint = new Paint();
		int gravity = 0;
		int xCord = 0;
		int yCord = 0;
		int viewWidth = 0;
		int viewHeight = 0;

		public PictoText(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			//gravity = getGravity();
			init();
			/*
			int temp = gravity & Gravity.VERTICAL_GRAVITY_MASK;
			if(gravity == Gravity.BOTTOM) {
				xCord = getWidth()/2-(textLabel.length()/2);
				yCord = (int) (getHeight()-textPaint.getTextSize());
			}
			else if (gravity == Gravity.CENTER) {
				xCord = getWidth()/2-(textLabel.length()/2);
				yCord = (int) ((getHeight()/2)-textPaint.getTextSize());
			}
			else if (gravity == Gravity.TOP) {
				xCord = getWidth()/2-(textLabel.length()/2);
				yCord = 0;
			}*/
			
			xCord = getWidth()/2-(textLabel.length()/2);
			yCord = (int) (getHeight()-textPaint.getTextSize());
			
			System.out.println(xCord + " " + yCord);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
			canvas.drawText(textLabel, xCord, yCord, textPaint);

		}
		
		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);
		}
		
		private void init() {
			setPadding(3, 3, 3, 3);
			textPaint.setTextSize(16);
			textPaint.setColor(Color.BLACK);
			textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			int reqWidth;
			int reqHeight;
			
			int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			
			int widthSize = MeasureSpec.getSize(widthMeasureSpec);
			int heightSize = MeasureSpec.getSize(heightMeasureSpec);
			
			if(widthMode == MeasureSpec.EXACTLY){
				reqWidth = widthSize;
			}
			else {
				reqWidth = (int)(textPaint.measureText(textLabel));
			}

			if(heightMode == MeasureSpec.EXACTLY){
				reqHeight = heightSize;
			}
			else {
				reqHeight = (int) textPaint.getTextSize();
			}
			setMeasuredDimension(reqWidth, reqHeight);
			/*
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			
			setMeasuredDimension(width, height);*/
		}
		
		@Override
	    public void onSizeChanged (int w, int h, int oldw, int oldh){
	        super.onSizeChanged(w, h, oldw, oldh);
	        viewWidth = w;
	        viewHeight = h;
	    }
	}
	
	
}
