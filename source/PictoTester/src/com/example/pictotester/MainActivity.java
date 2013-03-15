package com.example.pictotester;
import dk.aau.cs.giraf.pictogram.*;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends Activity {
	
	String imagePath = Environment.getExternalStorageDirectory().getPath() + "/Pictogram/Film.png";
	String textLabel = "Filmses";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println(imagePath);
		
		Pictogram pic = new Pictogram(this, imagePath, textLabel, null, 0);
		//pic.setTextLabel(textLabel);
		//pic.setImagePath(imagePath);
		pic.renderImage();
		pic.renderText();
		
		LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT);
		// Adding full screen container
		addContentView(pic, params);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
