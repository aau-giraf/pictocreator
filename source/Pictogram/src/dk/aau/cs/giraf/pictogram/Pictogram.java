package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class Pictogram extends View implements IPictogram{
	private String imagePath;
	private String text;
	private String audio;
	private String pictogramID;
	
	Pictogram(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void renderAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderText() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderImage() {
		// TODO Auto-generated method stub
		BitmapDrawable image = new BitmapDrawable(getResources(),imagePath);
		
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
	
	
}
