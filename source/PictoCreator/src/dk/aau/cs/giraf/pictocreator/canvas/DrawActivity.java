package dk.aau.cs.giraf.pictocreator.canvas;

import dk.aau.cs.giraf.pictocreator.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ToggleButton;

public class DrawActivity extends Fragment {

	public View view;
	public DrawView drawView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setContentView(R.layout.activity_main);
		
		Log.w("MainActivity", "Invalidating DrawView to force onDraw.");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = inflater.inflate(R.layout.draw_fragment, container, false);
		
		drawView = (DrawView)view.findViewById(R.id.drawingview);
		
		return view;
	}
	
}
