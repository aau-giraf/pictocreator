package com.example.pictotester;
import java.io.IOException;

import android.media.MediaPlayer;
import dk.aau.cs.giraf.pictogram.*;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridLayout;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT);
		LayoutParams params2 = new LayoutParams(100 , 100);
		
		LinearLayout grid = new LinearLayout(this);
		addContentView(grid, params);
		
		Pictogram[] arr = new Pictogram[10];

		for (int i = 0; i < 10; i++) {
			arr[i] = PictoFactory.INSTANCE.getPictogram(this, i);
			arr[i].renderImage();
			arr[i].renderText();
			grid.addView(arr[i], params2);
		}

		//arr[0].playAudio();
		
		/*
		Pictogram pic = PictoFactory.INSTANCE.getPictogram(this, 0);
		pic.renderImage();
		pic.renderText();
		grid.addView(pic);
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
