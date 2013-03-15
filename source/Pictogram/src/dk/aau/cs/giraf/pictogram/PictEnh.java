package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import dk.aau.cs.giraf.R;

public class PictEnh extends FrameLayout implements IPictogram{
	
	private String imagePath;
	private String textLabel;
	private String audioPath;
	private long pictogramID;
	private Gravity textGravity;
	
	//Main constructor (no XML)
	public PictEnh(Context context, String image, String text, String audio, long id) {
		super(context);
		this.imagePath = image;
		this.textLabel = text;
		this.audioPath = audio;
		this.pictogramID = id;
	}
	
	@Override
	public void renderAll() {
		renderImage();
		renderText();
	}

	@Override
	public void renderText() {
		// TODO Auto-generated method stub
		TextView text = new TextView(getContext());
		text.setText(textLabel);
		text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		this.addView(text);
	}
	
	public void renderText(int gravity) {
		TextView text = new TextView(getContext());
		text.setText(textLabel);
		text.setPadding(10, 10, 10, 10);
		text.setGravity(Gravity.CENTER_HORIZONTAL | gravity);
		this.addView(text);
	}

	@Override
	public void renderImage() {
		// TODO Auto-generated method stub
		Bitmap img = BitmapFactory.decodeFile(imagePath);
		ImageView image = new ImageView(getContext());
		image.setImageBitmap(img);
		this.addView(image);
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

	public String getImagePath() {
		return this.imagePath;
	}
	
	public String getTextLabel() {
		return this.textLabel;
	}
	
	public String getAudioPath() {
		return this.audioPath;
	}
	
	public long getPictogramID() {
		return this.pictogramID;
	}
	
}
