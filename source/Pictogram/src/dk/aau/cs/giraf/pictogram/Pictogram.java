package dk.aau.cs.giraf.pictogram;

import dk.aau.cs.giraf.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Pictogram extends ViewGroup implements IPictogram{

	private int pictoWidth;
	private int pictoHeight;
	
	private String imagePath;
	private String textPath;
	private String audioPath;
	private String pictogramID;
	
	//Main constructor (no XML)
	public Pictogram(Context context, String image, String audio, String text) {
		super(context);
		
		this.imagePath = image;
		this.audioPath = audio;
		this.textPath = text;
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	//Eventually for XML (if needed) otherwise use other constructor
	public Pictogram(Context context, AttributeSet attrs, String image, String audio, String text) {
		super(context, attrs);
		
		this.imagePath = image;
		this.audioPath = audio;
		this.textPath = text;
		
		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Pictogram, 0, 0);
		try {
			pictoHeight = attributes.getInt(R.styleable.Pictogram_height, -1);
			pictoWidth = attributes.getInt(R.styleable.Pictogram_width, -1);
		}
		finally {
			attributes.recycle();
		}
		setLayoutParams(new LayoutParams(pictoWidth, pictoHeight));
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
	}
	
	@Override
	public void renderAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderText() {
		// TODO Auto-generated method stub
		
		
	}
	
	public void renderText(int position) {
		
		TextView text = new TextView(null);
		text.setGravity(position);
		
		invalidate();
	}

	@Override
	public void renderImage() {
		// TODO Auto-generated method stub
		BitmapDrawable image = new BitmapDrawable(getResources(), imagePath);
		//Draw stuff...
		addView(null);
		
	}

	@Override
	public void playAudio() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImageData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAudioData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTextData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	
}
