package dk.aau.cs.giraf.pictocreator.canvas;

import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.FreehandHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.LineHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.OvalHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.RectHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.SelectionHandler;
import android.app.Fragment;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.ImageButton;

public class DrawFragment extends Fragment {

	public View view;
	public DrawView drawView;
	
	ImageButton rectHandlerButton;
	ImageButton ovalHandlerButton;
	ImageButton lineHandlerButton;
	ImageButton selectHandlerButton;
	ImageButton freehandHandlerButton;
	
	/**
	 * Quick reference to one of the other handler buttons, whichever is active.
	 */
	ImageButton activeHandlerButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.w("MainActivity", "Invalidating DrawView to force onDraw.");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = inflater.inflate(R.layout.draw_fragment, container, false);
		
		drawView = (DrawView)view.findViewById(R.id.drawingview);
		
		selectHandlerButton = (ImageButton)view.findViewById(R.id.select_handler_button);
		selectHandlerButton.setOnClickListener(onSelectHandlerButtonClick);
		freehandHandlerButton = (ImageButton)view.findViewById(R.id.freehand_handler_button);
		freehandHandlerButton.setOnClickListener(onFreehandHandlerButtonClick);
		rectHandlerButton = (ImageButton)view.findViewById(R.id.rect_handler_button);
		rectHandlerButton.setOnClickListener(onRectHandlerButtonClick);
		lineHandlerButton = (ImageButton)view.findViewById(R.id.line_handler_button);
		lineHandlerButton.setOnClickListener(onLineHandlerButtonClick);
		ovalHandlerButton = (ImageButton)view.findViewById(R.id.oval_handler_button);
		ovalHandlerButton.setOnClickListener(onOvalHandlerButtonClick);
		
		// Set initial handler.
		drawView.setHandler(new RectHandler());
		activeHandlerButton = rectHandlerButton; 
		activeHandlerButton.setEnabled(false);
		
		return view;
	}
	
	private final OnClickListener onSelectHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new SelectionHandler());
			activeHandlerButton.setEnabled(true); // Enable previous.
			activeHandlerButton = selectHandlerButton; // Switch out active.
			activeHandlerButton.setEnabled(false); // Disable new active.
		}
	};
	
	private final OnClickListener onFreehandHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new FreehandHandler());
			activeHandlerButton.setEnabled(true); // Enable previous.
			activeHandlerButton = freehandHandlerButton; // Switch out active.
			activeHandlerButton.setEnabled(false); // Disable new active.
		}
	};
	
	/**
	 * Click handler for the RectHandler. It sets the active handler in
	 * DrawView and marks itself as "in use".
	 */
	private final OnClickListener onRectHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new RectHandler());
			activeHandlerButton.setEnabled(true); // Enable previous.
			activeHandlerButton = rectHandlerButton; // Switch out active.
			activeHandlerButton.setEnabled(false); // Disable new active.
		}
	};
	
	private final OnClickListener onOvalHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new OvalHandler());
			activeHandlerButton.setEnabled(true); // Enable previous.
			activeHandlerButton = ovalHandlerButton; // Switch out active.
			activeHandlerButton.setEnabled(false); // Disable new active.
			
		}
	};
	
	private final OnClickListener onLineHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new LineHandler());
			activeHandlerButton.setEnabled(true); // Enable previous.
			activeHandlerButton = lineHandlerButton; // Switch out active.
			activeHandlerButton.setEnabled(false); // Disable new active.
		}
	};
	
}
