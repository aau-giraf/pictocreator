package dk.aau.cs.giraf.pictocreator.canvas;

import java.util.Currency;
import java.io.FileNotFoundException;

import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.canvas.entity.BitmapEntity;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.FreehandHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.LineHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.OvalHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.RectHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.SelectionHandler;
import dk.aau.cs.giraf.pictocreator.management.CamImportDialogFragment;
import dk.aau.cs.giraf.pictocreator.management.CamImportDialogFragment.ImportResultPath;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

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

	LinearLayout colorButtonToolbox;

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

		SeekBar strokeWidthBar = (SeekBar)view.findViewById(R.id.strokeWidthBar);
		strokeWidthBar.setOnSeekBarChangeListener(onStrokeWidthChange);

		// Set initial handler.
		drawView.setHandler(new FreehandHandler());
		activeHandlerButton = freehandHandlerButton;
		activeHandlerButton.setEnabled(false);

		View tmp = view.findViewById(R.id.canvasColorPreviewButton);
		Log.w("DrawFragment.onCreateView", String.format("PreviewButton returned as a %s.", tmp.getClass().toString()));
		previewButton = (PreviewButton)tmp;
		previewButton.setStrokeColor(0xFF000000);
		previewButton.setFillColor(0xFFFFFFFF);
		drawView.setStrokeColor(previewButton.getStrokeColor());
		drawView.setFillColor(previewButton.getFillColor());

		importFragmentButton = (ImageButton)view.findViewById(R.id.start_import_dialog_button);
		importFragmentButton.setOnClickListener(onImportClick);

		colorButtonToolbox = (LinearLayout)((ScrollView)view.findViewById(R.id.colorToolbox)).getChildAt(0);

		// Add standard HTML colors to the box.
		addColorButton(0x00000000); // Transparent
		addColorButton(0xFF000000); // Black
		addColorButton(0xFFFFFFFF); // White
		addColorButton(0xFF808080); // Gray
		addColorButton(0xFFC0C0C0); // Silver
		addColorButton(0xFF800000); // Maroon
		addColorButton(0xFFFF0000); // Red
		addColorButton(0xFF808000); // Olive
		addColorButton(0xFFFFFF00); // Yellow
		addColorButton(0xFF008000); // Green
		addColorButton(0xFF00FF00); // Lime
		addColorButton(0xFF008080); // Teal
		addColorButton(0xFF00FFFF); // Aqua
		addColorButton(0xFF000080); // Navy
		addColorButton(0xFF0000FF); // Blue
		addColorButton(0xFF800080); // Purple
		addColorButton(0xFFFF00FF); // Fuchsia

		colorButtonToolbox.removeViewAt(0); // Remove the placeholder.

		strokeWidthBar.setProgress(4); // Set default stroke width.

		return view;
	}

	/**
	 * Adds a new ColorButton with the specified hex color to the color toolbox.
	 * @param color Color to add. Can have alpha, although we suggest keeping it opaque (0xFFxxxxxx).
	 * @param cbList LinearLayout instance where the button should be added.
	 */
	private void addColorButton(int color) {
		colorButtonToolbox.addView(
				new ColorButton(
						drawView,
						previewButton,
						color,
						this.getActivity()));
	}


    public Bitmap saveBitmap(){
        Bitmap tmp = null;
        try {
            tmp = drawView.saveToBitmap(Bitmap.Config.ARGB_8888);
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "No file was found to decode");
        }
        return tmp;
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
			importDialog.setImportPath(new ImportResultPath() {
				@Override
				public void onImport(String path) {
					// Do the import here...
					// Jojo do your stuff (btw path is the path to chosen image)
					//Toast.makeText(getActivity(), path, Toast.LENGTH_LONG).show();
					drawView.loadFromBitmap(BitmapFactory.decodeFile(path));
				}
			});
			importDialog.show(getActivity().getFragmentManager(), TAG);
		}
	};

	private final OnSeekBarChangeListener onStrokeWidthChange = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			previewButton.setStrokeWidth(progress);
			drawView.setStrokeWidth(progress);
			Log.i("DrawFragment", String.format("StrokeWidthBar changed to %s.", progress));
		};

		public void onStopTrackingTouch(SeekBar seekBar) {};
		public void onStartTrackingTouch(SeekBar seekBar) {};
	};
}
