package dk.aau.cs.giraf.pictocreator.canvas;

import android.app.Fragment;
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

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GDialogMessage;
import dk.aau.cs.giraf.gui.GToggleButton;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.audiorecorder.AudioHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.FreehandHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.LineHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.OvalHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.RectHandler;
import dk.aau.cs.giraf.pictocreator.canvas.handlers.SelectionHandler;
import dk.aau.cs.giraf.pictocreator.management.CamImportDialogFragment;
import dk.aau.cs.giraf.pictocreator.management.CamImportDialogFragment.ImportResultPath;

/**
 * The DrawFragment is the part of Croc that handles free-form drawing with
 * multiple tools. The basic layout draw_fragment.xml is expanded procedurally
 * with a number of colouring options and action tools in the constructor and
 * initialisation code.
 * @author lindhart
 */
public class DrawFragment extends Fragment {

    private static final String TAG = "DrawFragment";

    /**
     *
     */
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
    protected GButton clearButton;

    CamImportDialogFragment importDialog;

    GDialogMessage clearDialog;

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
                drawView.drawStack = savedInstanceState.getParcelable("drawstack");
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
        freehandHandlerButton.setPressed(true);

        selectHandlerButton = (GToggleButton)view.findViewById(R.id.select_handler_button);
        selectHandlerButton.setOnClickListener(onSelectHandlerButtonClick);

        rectHandlerButton = (GToggleButton)view.findViewById(R.id.rect_handler_button);
        rectHandlerButton.setOnClickListener(onRectHandlerButtonClick);

        lineHandlerButton = (GToggleButton)view.findViewById(R.id.line_handler_button);
        lineHandlerButton.setOnClickListener(onLineHandlerButtonClick);

        ovalHandlerButton = (GToggleButton)view.findViewById(R.id.oval_handler_button);
        ovalHandlerButton.setOnClickListener(onOvalHandlerButtonClick);



        clearButton = (GButton)view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(onClearButtonClick);
        //clearButton

        SeekBar strokeWidthBar = (SeekBar)view.findViewById(R.id.strokeWidthBar);
        strokeWidthBar.setOnSeekBarChangeListener(onStrokeWidthChange);

        // Set initial handler.
        drawView.setHandler(new FreehandHandler());

        View tmp = view.findViewById(R.id.canvasColorPreviewButton);
        Log.w("DrawFragment.onCreateView", String.format("PreviewButton returned as a %s.", tmp.getClass().toString()));
        previewButton = (PreviewButton)tmp;
        previewButton.setStrokeColor(0xFF000000);
        previewButton.setFillColor(0xFF000000);
        previewButton.setOnClickListener(onPreviewButtonClick);
        drawView.setStrokeColor(previewButton.getStrokeColor());
        drawView.setFillColor(previewButton.getFillColor());

        importFragmentButton = (GButton)view.findViewById(R.id.start_import_dialog_button);
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
     */
    private void addColorButton(int color) {
        colorButtonToolbox.addView(
                new ColorButton(
                        drawView,
                        previewButton,
                        color,
                        this.getActivity()));
    }

    private void setAllUnToggle(){
        rectHandlerButton.setToggled(false);
        ovalHandlerButton.setToggled(false);
        lineHandlerButton.setToggled(false);
        selectHandlerButton.setToggled(false);
        freehandHandlerButton.setToggled(false);
    }

    private final OnClickListener onSelectHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new SelectionHandler(getResources()));
            setAllUnToggle();
            selectHandlerButton.setToggled(true);
        }
    };

    private final OnClickListener onFreehandHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new FreehandHandler());
            setAllUnToggle();
            freehandHandlerButton.setToggled(true);
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
            setAllUnToggle();
            rectHandlerButton.setToggled(true);
        }
    };



    private final OnClickListener onOvalHandlerButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawView.setHandler(new OvalHandler());
            setAllUnToggle();
            ovalHandlerButton.setToggled(true);
        }
    };

    private final OnClickListener onLineHandlerButtonClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            drawView.setHandler(new LineHandler());
            setAllUnToggle();
            lineHandlerButton.setToggled(true);
        }
    };

    private final OnClickListener  onClearButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"Clear Button clicked");
            clearDialog = new GDialogMessage(v.getContext(),"Ryd tegnebræt?",onAcceptClearCanvasClick);
            clearDialog.show();
        }
    };

    private final OnClickListener onAcceptClearCanvasClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AudioHandler.resetSound();
            if(drawView != null && drawView.drawStack != null){
                drawView.drawStack.entities.clear();
                drawView.invalidate();

                /*Neeeded as selectionhandler would have a deleted item selected otherwise*/
                DeselectEntity();

            }
        }
    };


    public void DeselectEntity(){
        if (drawView.currentHandler instanceof SelectionHandler)
            ((SelectionHandler)drawView.currentHandler).deselect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Parcel currentDrawStack = Parcel.obtain();
        // drawView.drawStack.writeToParcel(currentDrawStack, 0);
        super.onSaveInstanceState(outState);
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
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                previewButton.setStrokeWidth(progress);
                drawView.setStrokeWidth(progress);
                Log.i("DrawFragment", String.format("StrokeWidthBar changed to %s.", progress));
        };

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {};
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {};
    };

    private final OnClickListener onPreviewButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            previewButton.swapColors();
            drawView.setFillColor(previewButton.getFillColor());
            drawView.setStrokeColor(previewButton.getStrokeColor());
            Log.i("DrawFragment", String.format("Swapping colors from %s to %s", previewButton.getStrokeColor(), previewButton.getFillColor()));
        }
    };
}
