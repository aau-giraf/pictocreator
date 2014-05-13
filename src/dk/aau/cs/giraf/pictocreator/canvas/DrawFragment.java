package dk.aau.cs.giraf.pictocreator.canvas;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GColorPicker;
import dk.aau.cs.giraf.gui.GSeekBar;
import dk.aau.cs.giraf.gui.GToggleButton;
import dk.aau.cs.giraf.oasis.lib.models.Tag;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.audiorecorder.RecordDialogFragment;
import dk.aau.cs.giraf.pictocreator.cam.CamFragment;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.FreehandHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.LineHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.OvalHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.RectHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.SelectionHandler;

/**
 * The DrawFragment is the part of Croc that handles free-form drawing with
 * multiple tools. The basic layout draw_fragment.xml is expanded procedurally
 * with a number of colouring options and action tools in the constructor and
 * initialisation code.
 * @author lindhart
 */
public class DrawFragment extends Fragment {

    private static final String TAG = "DrawFragment";

    public View view;

    /**
     * The DrawView contained within this fragment. A lot of communication goes
     * more or less transparently through, which makes this reference useful.
     */
    public DrawView drawView;

    /**
     * Button for the RectHandler ActionHandler.
     */
    protected GToggleButton rectHandlerButton,ovalHandlerButton,
            lineHandlerButton, selectHandlerButton, freehandHandlerButton;

    /**
     * Button for the import action for importing Bitmaps.
     */
    protected GButton importFragmentButton;
    protected GButton recordDialogButton;

    CamFragment cameraDialog;

    /**
     * Button for custom color picking function, with the default starting color black.
     */
    protected GButton colorFrameButton;
    protected PreviewButton currentCustomColor;
    private int customColor = 0xFF000000;

    RecordDialogFragment recordDialog;

    /**
     * Displays previews of current color choices.
     */
    PreviewButton previewButton;

    /**
     * The LinearLayout view that contains all choosable colours.
     */
    LinearLayout colorButtonToolbox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
                // Restore drawStack et al.
                DrawStackSingleton.getInstance().mySavedData = savedInstanceState.getParcelable("drawstack");
                drawView.invalidate();
        }

        Log.w("MainActivity", "Invalidating DrawView to force onDraw.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.draw_fragment, container, false);

        drawView = (DrawView)view.findViewById(R.id.drawingview);

        freehandHandlerButton = (GToggleButton)view.findViewById(R.id.freehand_handler_button);
        freehandHandlerButton.setOnClickListener(onFreehandHandlerButtonClick);

        selectHandlerButton = (GToggleButton)view.findViewById(R.id.select_handler_button);
        selectHandlerButton.setOnClickListener(onSelectHandlerButtonClick);

        rectHandlerButton = (GToggleButton)view.findViewById(R.id.rect_handler_button);
        rectHandlerButton.setOnClickListener(onRectHandlerButtonClick);

        lineHandlerButton = (GToggleButton)view.findViewById(R.id.line_handler_button);
        lineHandlerButton.setOnClickListener(onLineHandlerButtonClick);

        ovalHandlerButton = (GToggleButton)view.findViewById(R.id.oval_handler_button);
        ovalHandlerButton.setOnClickListener(onOvalHandlerButtonClick);

        recordDialogButton = (GButton)view.findViewById(R.id.start_record_dialog_button);
        recordDialogButton.setOnClickListener(showRecorderClick);

        GSeekBar strokeWidthBar = (GSeekBar)view.findViewById(R.id.strokeWidthBar);
        strokeWidthBar.setOnSeekBarChangeListener(onStrokeWidthChange);

        colorFrameButton = (GButton) view.findViewById(R.id.customColor);
        colorFrameButton.setOnClickListener(customColorButton);

        currentCustomColor = (PreviewButton) view.findViewById(R.id.currentCustomColor);
        currentCustomColor.setOnClickListener(onCurrentCustomColorButtonClick);
        currentCustomColor.setStrokeColor(0x00000000);
        currentCustomColor.setFillColor(0xFF000000);

        // Set initial handler.
        drawView.setHandler(new FreehandHandler());

        previewButton = (PreviewButton) view.findViewById(R.id.canvasColorPreviewButton);
        // Initial colours are set to black
        previewButton.setStrokeColor(0xFF000000);
        previewButton.setFillColor(0xFF000000);
        previewButton.setOnClickListener(onPreviewButtonClick);
        previewButton.changePreviewDisplay(drawType.LINE);
        previewButton.setEnabled(false);

        drawView.setStrokeColor(previewButton.getStrokeColor());
        drawView.setFillColor(previewButton.getFillColor());

        importFragmentButton = (GButton)view.findViewById(R.id.start_import_dialog_button);
        importFragmentButton.setOnClickListener(onImportClick);

        if(!checkForCamera(this.getActivity())) {
           importFragmentButton.setEnabled(false);
        }

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

        return view;
    }

    /**
     * Function for checking whether a camera is available at the device
     * @param context The context in which the function is called
     * @return True if a camera is found on the device, false otherwise
     */
    private boolean checkForCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            Log.d(TAG, "No camera found on device");
            return false;
        }
    }

    /**
     * Adds a new ColorButton with the specified hex color to the color toolbox.
     * @param color Color to add. Can have alpha, although we suggest keeping it opaque (0xFFxxxxxx).
     */
    private void addColorButton(int color) {
        colorButtonToolbox.addView(
                new ColorButton(
                        drawView,
                        previewButton,
                        color,
                        this.getActivity()));
    }

    private final OnClickListener showRecorderClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            recordDialog = new RecordDialogFragment();
            recordDialog.show(getFragmentManager(), TAG);
        }
    };

    /**
     * Toggles all the tool buttons off.
     */
    private void setAllUnToggle(){
        rectHandlerButton.setToggled(false);
        ovalHandlerButton.setToggled(false);
        lineHandlerButton.setToggled(false);
        selectHandlerButton.setToggled(false);
        freehandHandlerButton.setToggled(false);
    }

    /**
     * Click handlers for the all the tool buttons. It sets the active handler in
     * DrawView, untoggles all the tools and toggles itself.
     */
    private final OnClickListener onSelectHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new SelectionHandler(getResources()));
            setAllUnToggle();
            selectHandlerButton.setToggled(true);
            previewButton.changePreviewDisplay(drawType.NONE);
            previewButton.setEnabled(false);

        }
    };
    private final OnClickListener onFreehandHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new FreehandHandler());
            setAllUnToggle();
            freehandHandlerButton.setToggled(true);
            previewButton.changePreviewDisplay(drawType.LINE);
            previewButton.setEnabled(false);
        }
    };
    private final OnClickListener onRectHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new RectHandler());
            setAllUnToggle();
            rectHandlerButton.setToggled(true);
            previewButton.changePreviewDisplay(drawType.RECTANGLE);
            previewButton.setEnabled(true);
        }
    };
    private final OnClickListener onOvalHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new OvalHandler());
            setAllUnToggle();
            ovalHandlerButton.setToggled(true);
            previewButton.changePreviewDisplay(drawType.CIRCLE);
            previewButton.setEnabled(true);

        }
    };
    private final OnClickListener onLineHandlerButtonClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            drawView.setHandler(new LineHandler());
            setAllUnToggle();
            lineHandlerButton.setToggled(true);
            previewButton.changePreviewDisplay(drawType.LINE);
            previewButton.setEnabled(false);
        }
    };

    private final OnClickListener customColorButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ColorPicker(v);
        }
    };

    /**
     * Utilizes the GColorPicker to open up the color picker.
     * Returns a color which is sat as fillcolor, previewButton's fillcolor, and the customColor buttons color.
     * The parameter in GColorPicker constructor is the previously selected color.
     * @param v View
     */
    private void ColorPicker(View v){
        GColorPicker colorPicker = new GColorPicker(v.getContext(), customColor, new GColorPicker.OnOkListener() {
            @Override
            public void OnOkClick(GColorPicker diag, int color) {
                customColor = color;
                drawView.setFillColor(color);
                previewButton.setFillColor(color);
                currentCustomColor.setFillColor(color);
            }
        });
        colorPicker.SetCurrColor(customColor);
        colorPicker.show();
    }

    public void DeselectEntity(){
        if (drawView.currentHandler instanceof SelectionHandler){
            ((SelectionHandler)drawView.currentHandler).deselect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("DrawFragment.onSaveInstanceState", "Saving drawstack in Bundled parcel.");
        outState.putParcelable("drawstack", DrawStackSingleton.getInstance().mySavedData);
    };

    private final OnClickListener onImportClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            cameraDialog = new CamFragment();
            cameraDialog.show(getActivity().getFragmentManager(), TAG);
            DeselectEntity();
        }
    };

    /**
     * The seekbar changes the strokewidth of the entities.
     * The stroke width is sat to be from 1 to 20 by dividing the value by 5, as standard seekbar range is from 1 to 100.
     */
    private final OnSeekBarChangeListener onStrokeWidthChange = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                previewButton.setStrokeWidth(progress/5);
                drawView.setStrokeWidth(progress/5);
                Log.i("DrawFragment", String.format("StrokeWidthBar changed to %s.", progress));
        };

        //Have to be overridden, but we do not need this functionality, therefore, it is empty.
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {};
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {};
    };

    /**
     * Swaps the colour in the previewButton and assigns the colour to the drawView colours.
     */
    private final OnClickListener onPreviewButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            previewButton.swapColors();
            drawView.setFillColor(previewButton.getFillColor());
            drawView.setStrokeColor(previewButton.getStrokeColor());
            Log.i(TAG, String.format("Swapping colors from %s to %s", previewButton.getStrokeColor(), previewButton.getFillColor()));
        }
    };

    /**
     * Chooses the custom color as fill color in both drawView and previewButton.
     */
    private final OnClickListener onCurrentCustomColorButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            drawView.setFillColor(customColor);
            previewButton.setFillColor(customColor);
        }
    };
}