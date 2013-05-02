package dk.homestead.canvastest;

import dk.homestead.canvastest.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	public DrawView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = (DrawView) findViewById(R.id.drawingview);
		setContentView(R.layout.activity_main);
		
		Log.w("MainActivity", "Invalidating DrawView to force onDraw.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
