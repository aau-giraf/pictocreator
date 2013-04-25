package dk.aau.cs.giraf.pictocreator.canvas;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import dk.aau.cs.giraf.pictocreator.R;

public class Dummy extends Fragment{
	
	View view;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = inflater.inflate(R.layout.dummy_fragment, container, false);
		
		ImageView logo = (ImageView)view.findViewById(R.id.logo);
		
		return view;
	}

}
