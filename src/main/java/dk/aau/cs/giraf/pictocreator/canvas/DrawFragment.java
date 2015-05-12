package dk.aau.cs.giraf.pictocreator.canvas;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import dk.aau.cs.giraf.gui.GColorPicker;
import dk.aau.cs.giraf.gui.GSeekBar;
import dk.aau.cs.giraf.gui.GTextView;
import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.audiorecorder.RecordDialogFragment;
import dk.aau.cs.giraf.pictocreator.cam.CamFragment;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.EraserHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.FreehandHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.LineHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.OvalHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.RectHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.SelectionHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.TextHandler;

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
    protected GirafButton rectHandlerButton,ovalHandlerButton, eraserHandlerButton,
            lineHandlerButton, selectHandlerButton, freehandHandlerButton, textHandlerButton;

    private GColorPicker backgroundColorPicker;
    private GColorPicker strokeColorPicker;

    private GTextView strokeWidthText;
    private int currentBackgroundColor;
    private int currentStrokeColor;
    /**
     * Button for the import action for importing Bitmaps.
     */
    protected GirafButton importFragmentButton;
    protected GirafButton recordDialoGirafButton;

    CamFragment cameraDialog;

    /**
     * Button for custom color picking function, with the default starting color black.
     */
    protected GirafButton currentBackgroundColorButton;
    protected GirafButton currentStrokeColorButton;
    private int customColor;

    RecordDialogFragment recordDialog;

    /**
     * Displays previews of current color choices.
     */
    PreviewButton canvasHandlerPreviewButton;

    /**
     * The LinearLayout view that contains all choosable colours.
     */
    LinearLayout colorButtonToolbox;

    public DrawFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // Restore drawStack et al.
            DrawStackSingleton.getInstance().mySavedData = savedInstanceState.getParcelable("drawstack");
            drawView.invalidate();
        }

        Log.w(TAG, "Invalidating DrawView to force onDraw.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.draw_fragment, container, false);

        drawView = (DrawView)view.findViewById(R.id.drawingview);

        strokeWidthText = (GTextView)view.findViewById(R.id.strokeWidthText);

        freehandHandlerButton = (GirafButton)view.findViewById(R.id.freehand_handler_button);
        freehandHandlerButton.setOnClickListener(onFreehandHandlerButtonClick);
        freehandHandlerButton.toggle();

        selectHandlerButton = (GirafButton)view.findViewById(R.id.select_handler_button);
        selectHandlerButton.setOnClickListener(onSelectHandlerButtonClick);

        eraserHandlerButton = (GirafButton)view.findViewById(R.id.eraser_handler_button);
        eraserHandlerButton.setOnClickListener(onEraserHandlerButtonClick);

        rectHandlerButton = (GirafButton)view.findViewById(R.id.rect_handler_button);
        rectHandlerButton.setOnClickListener(onRectHandlerButtonClick);

        lineHandlerButton = (GirafButton)view.findViewById(R.id.line_handler_button);
        lineHandlerButton.setOnClickListener(onLineHandlerButtonClick);

        ovalHandlerButton = (GirafButton)view.findViewById(R.id.oval_handler_button);
        ovalHandlerButton.setOnClickListener(onOvalHandlerButtonClick);

        textHandlerButton = (GirafButton)view.findViewById(R.id.text_handler_button);
        textHandlerButton.setOnClickListener(onTextHandlerButtonClick);

        recordDialoGirafButton = (GirafButton)view.findViewById(R.id.start_record_dialog_button);
        recordDialoGirafButton.setOnClickListener(showRecorderClick);

        GSeekBar strokeWidthBar = (GSeekBar)view.findViewById(R.id.strokeWidthBar);
        strokeWidthBar.setOnSeekBarChangeListener(onStrokeWidthChange);

        currentBackgroundColorButton = (GirafButton) view.findViewById(R.id.backgroundColorButton);
        currentBackgroundColorButton.setOnClickListener(onCurrentBackgroundColorButtonClick);

        currentStrokeColorButton = (GirafButton) view.findViewById(R.id.strokeColorButton);
        currentStrokeColorButton.setOnClickListener(getOnCurrentStrokeColorButtonClick);

        // Set initial handler.
        drawView.setHandler(new FreehandHandler());

        // Set initial colors
        currentStrokeColor = getResources().getColor(R.color.black);
        currentBackgroundColor = getResources().getColor(R.color.giraf_white);
        customColor = getResources().getColor(R.color.giraf_white);

        drawView.setFillColor(currentBackgroundColor);
        drawView.setStrokeColor(currentStrokeColor);

        canvasHandlerPreviewButton = (PreviewButton) view.findViewById(R.id.canvasHandlerPreviewButton);

        canvasHandlerPreviewButton.changePreviewDisplay(DrawType.LINE);
        canvasHandlerPreviewButton.setFillColor(currentBackgroundColor);
        canvasHandlerPreviewButton.setStrokeColor(currentStrokeColor);
        canvasHandlerPreviewButton.setEnabled(false);

        importFragmentButton = (GirafButton)view.findViewById(R.id.start_import_dialog_button);
        importFragmentButton.setOnClickListener(onImportClick);

        importFragmentButton = (GirafButton)view.findViewById(R.id.start_import_dialog_button);
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
        try{
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
                return true;
            } else {
                Log.d(TAG, "No camera found on device");
                return false;
            }
        } catch (Exception e){
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
                        canvasHandlerPreviewButton,
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
        if (rectHandlerButton.isChecked())
            rectHandlerButton.toggle();
        if (ovalHandlerButton.isChecked())
            ovalHandlerButton.toggle();
        if (lineHandlerButton.isChecked())
            lineHandlerButton.toggle();
        if (selectHandlerButton.isChecked())
            selectHandlerButton.toggle();
        if (freehandHandlerButton.isChecked())
           freehandHandlerButton.toggle();
        if (textHandlerButton.isChecked())
            textHandlerButton.toggle();
        if (eraserHandlerButton.isChecked())
            eraserHandlerButton.toggle();

        strokeWidthText.setText(getString(R.string.stroke_width));
    }

    /**
     * Click handlers for the all the tool buttons. It sets the active handler in
     * DrawView, untoggles all the tools and toggles itself.
     */
    private final OnClickListener onSelectHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new SelectionHandler(getResources(), getActivity()));
            setAllUnToggle();
            selectHandlerButton.toggle();
            canvasHandlerPreviewButton.changePreviewDisplay(DrawType.SELECT);
        }
    };
    private final OnClickListener onEraserHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Bitmap eraserBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eraser);
            drawView.setHandler(new EraserHandler(-1, eraserBitmap));
            setAllUnToggle();
            eraserHandlerButton.toggle();
            canvasHandlerPreviewButton.changePreviewDisplay(DrawType.ERASER);
        }
    };
    private final OnClickListener onFreehandHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new FreehandHandler());
            setAllUnToggle();
           // colorFrameButton.setText(getText(R.string.pick_stroke_color));
            freehandHandlerButton.toggle();
            canvasHandlerPreviewButton.changePreviewDisplay(DrawType.LINE);
        }
    };
    private final OnClickListener onRectHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new RectHandler());
            setAllUnToggle();
        //    colorFrameButton.setText(getText(R.string.pick_color));
            rectHandlerButton.toggle();
            canvasHandlerPreviewButton.changePreviewDisplay(DrawType.RECTANGLE);
        }
    };
    private final OnClickListener onOvalHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new OvalHandler());
            setAllUnToggle();

         //   colorFrameButton.setText(getText(R.string.pick_color));
            ovalHandlerButton.toggle();
            canvasHandlerPreviewButton.changePreviewDisplay(DrawType.CIRCLE);
        }
    };

    private final OnClickListener onTextHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new TextHandler(getActivity(), drawView));
            setAllUnToggle();
            strokeWidthText.setText(getString(R.string.text_size));
            textHandlerButton.toggle();
            canvasHandlerPreviewButton.changePreviewDisplay(DrawType.TEXT);
        }
    };

    private final OnClickListener onLineHandlerButtonClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            drawView.setHandler(new LineHandler());
            setAllUnToggle();
        //    colorFrameButton.setText(getText(R.string.pick_stroke_color));
            lineHandlerButton.toggle();
            canvasHandlerPreviewButton.changePreviewDisplay(DrawType.LINE);
        }
    };

    /**
     * Utilizes the GColorPicker to open up the color picker.
     * Returns a color which is sat as fillcolor, canvasHandlerPreviewButton's fillcolor, and the customColor buttons color.
     * The parameter in GColorPicker constructor is the previously selected color.
     * @param v View
     */

    private void ColorPicker(View v, final int currentColor, final boolean background){
        GColorPicker colorPicker = new GColorPicker(v.getContext(), customColor, new GColorPicker.OnOkListener() {
            @Override
            public void OnOkClick(GColorPicker diag, int color) {
                if (background)
                {
                    currentBackgroundColor = color;
                    drawView.setFillColor(color);
                    canvasHandlerPreviewButton.setFillColor(color);
                }
                else
                {
                    currentStrokeColor = color;
                    drawView.setStrokeColor(color);
                    canvasHandlerPreviewButton.setStrokeColor(color);
                }
            }
        });

        colorPicker.SetCurrColor(currentColor);
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
            canvasHandlerPreviewButton.setStrokeWidth(progress/5);
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
     * Chooses the custom color as fill color in both drawView and canvasHandlerPreviewButton.
     */
    private final OnClickListener onCurrentBackgroundColorButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ColorPicker(v, currentBackgroundColor, true); // 3rd parameter (true) means set background color
        }
    };

    /**
     * Chooses the custom color as fill color in both drawView and canvasHandlerPreviewButton.
     */
    private final OnClickListener getOnCurrentStrokeColorButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ColorPicker(v, currentStrokeColor, false); // 3rd parameter (false) means set stroke color
        }
    };
}