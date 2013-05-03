package dk.aau.cs.giraf.pictocreator.canvas;

import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.FreehandHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.LineHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.OvalHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.RectHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.SelectionHandler;
import dk.aau.cs.giraf.pictocreator.management.CamImportDialogFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class DrawFragment extends Fragment {
	private static final String TAG = "DrawFragment";

	public View view;
	public DrawView drawView;
	
	ImageButton rectHandlerButton;
	ImageButton ovalHandlerButton;
	ImageButton lineHandlerButton;
	ImageButton selectHandlerButton;
	ImageButton freehandHandlerButton;
	ImageButton importFragmentButton;
	
	CamImportDialogFragment importDialog;
	
	/**
	 * Displays previews of current color choices.
	 */
	PreviewButton previewButton;
	
	/**
	 * Quick reference to one of the other handler buttons, whichever is active.
	 */
	ImageButton activeHandlerButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			// Restore drawStack et al.
			drawView.drawStack = savedInstanceState.getParcelable("drawstack");
		}
		
		Log.w("MainActivity", "Invalidating DrawView to force onDraw.");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = inflater.inflate(R.layout.draw_fragment, container, false);
		
		drawView = (DrawView)view.findViewById(R.id.drawingview);
		
		freehandHandlerButton = (ImageButton)view.findViewById(R.id.freehand_handler_button);
		freehandHandlerButton.setOnClickListener(onFreehandHandlerButtonClick);
		selectHandlerButton = (ImageButton)view.findViewById(R.id.select_handler_button);
		selectHandlerButton.setOnClickListener(onSelectHandlerButtonClick);
		rectHandlerButton = (ImageButton)view.findViewById(R.id.rect_handler_button);
		rectHandlerButton.setOnClickListener(onRectHandlerButtonClick);
		lineHandlerButton = (ImageButton)view.findViewById(R.id.line_handler_button);
		lineHandlerButton.setOnClickListener(onLineHandlerButtonClick);
		ovalHandlerButton = (ImageButton)view.findViewById(R.id.oval_handler_button);
		ovalHandlerButton.setOnClickListener(onOvalHandlerButtonClick);
		
		previewButton = (PreviewButton)view.findViewById(R.id.canvasColorPreviewButton);
		
		importFragmentButton = (ImageButton)view.findViewById(R.id.start_import_dialog_button);
		importFragmentButton.setOnClickListener(onImportClick);
		
		// Set initial handler.
		drawView.setHandler(new FreehandHandler());
		activeHandlerButton = freehandHandlerButton; 
		activeHandlerButton.setEnabled(false);
		
		LinearLayout ll = (LinearLayout)((ScrollView)view.findViewById(R.id.colorToolbox)).getChildAt(0);
		
		ColorButton b;
		b = new ColorButton(drawView, previewButton, getActivity().getResources().getColor(R.color.giraf_blue1), this.getActivity());
		b.setImageResource(R.drawable.blank_100x100);
		ll.addView(b);
		b = new ColorButton(drawView, previewButton, getActivity().getResources().getColor(R.color.giraf_brown1), this.getActivity());
		b.setImageResource(R.drawable.blank_100x100);
		ll.addView(b);
		
		return view;
	}
	
	/**
	 * Shorthand for switching active handler button. Better than serious code redundancy.
	 * @param button The ImageButton to disable. The currently disabled button will be re-enabled.
	 */
	protected void setActiveButton(ImageButton button) {
		activeHandlerButton.setEnabled(true); // Enable previous.
		activeHandlerButton = button; // Switch out active.
		activeHandlerButton.setEnabled(false); // Disable new active.
	}
	
	private final OnClickListener onSelectHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new SelectionHandler(getResources()));
			setActiveButton(selectHandlerButton);
		}
	};
	
	private final OnClickListener onFreehandHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new FreehandHandler());
			setActiveButton(freehandHandlerButton);
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
			setActiveButton(rectHandlerButton);
		}
	};
	
	private final OnClickListener onOvalHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new OvalHandler());
			setActiveButton(ovalHandlerButton);
			
		}
	};
	
	private final OnClickListener onLineHandlerButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			drawView.setHandler(new LineHandler());
			setActiveButton(lineHandlerButton);
			
		}
	};
	
	public void onSaveInstanceState(Bundle outState) {
		// Parcel currentDrawStack = Parcel.obtain();
		// drawView.drawStack.writeToParcel(currentDrawStack, 0);
		Log.i("DrawFragment.onSaveInstanceState", "Saving drawstack in Bundled parcel.");
		outState.putParcelable("drawstack", drawView.drawStack);
	};
	
	private final OnClickListener onImportClick = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			importDialog = new CamImportDialogFragment();
			importDialog.show(getActivity().getFragmentManager(), TAG);
		}
	};
}
