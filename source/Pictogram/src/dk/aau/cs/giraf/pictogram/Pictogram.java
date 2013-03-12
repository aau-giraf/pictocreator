package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class Pictogram extends View implements IPictogram{
	private String imagePath;
	private String textPath;
	private String audioPath;
	private String pictogramID;
	
	Pictogram(Context context, AttributeSet attrs, String image, String audio, String text) {
		super(context, attrs);
		
		this.imagePath = image;
		this.audioPath = audio;
		this.textPath = text;
	}
	
	@Override
	public void renderAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderText() {
		// TODO Auto-generated method stub
		if (getBackground() == null) {
			setBackgroundColor(Color.WHITE);
		}
		
		
		
	}

	@Override
	public void renderImage() {
		// TODO Auto-generated method stub
		BitmapDrawable image = new BitmapDrawable(getResources(), imagePath);
		setBackground(image);
		
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
