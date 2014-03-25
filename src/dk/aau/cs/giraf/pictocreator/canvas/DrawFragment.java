package dk.aau.cs.giraf.pictocreator.canvas;

import java.io.FileNotFoundException;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import dk.aau.cs.giraf.gui.GDialog;
import dk.aau.cs.giraf.pictocreator.R;
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
        protected ImageButton rectHandlerButton;

        /**
         * Button for the OvalHandler ActionHandler.
         */
        protected ImageButton ovalHandlerButton;

        /**
         * Button for the LineHandler ActionHandler.
         */
        protected ImageButton lineHandlerButton;

        /**
         * Button for the SelectionHandler ActionHandler.
         */
        protected ImageButton selectHandlerButton;

        /**
         * Button for the FreehandHandler ActionHandler (HANDLE!).
         */
        protected ImageButton freehandHandlerButton;

        /**
         * Button for the import action for importing Bitmaps.
         */
        protected ImageButton importFragmentButton;

        protected ImageButton clearButton;

        CamImportDialogFragment importDialog;

        ClearDialogFragment clearDialog;
        /**
         * Displays previews of current color choices.
         */
        PreviewButton previewButton;

        /**
         * Quick reference to one of the other handler buttons, whichever is active.
         */
        ImageButton activeHandlerButton;

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

                clearButton = (ImageButton)view.findViewById(R.id.clearButton);
                clearButton.setOnClickListener(onClearButtonClick);
                //clearButton

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
                previewButton.setFillColor(0xFF000000);
                previewButton.setOnClickListener(onPreviewButtonClick);
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

        /**
         * Instructs the DrawView to flatten its current draw stack and save it as
         * a Bitmap.
         * @todo Write documentation on where it is saved due to the heavy
         * side-effecting we failed to avoid.
         */
        public void saveBitmap(){
            try {
                drawView.saveToBitmap(Bitmap.Config.ARGB_8888);
            }
            catch (FileNotFoundException e){
                Log.e(TAG, "No file was found to decode");
            }

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

        private final OnClickListener  onClearButtonClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Clear Button clicked");
                /*clearDialog = new ClearDialogFragment();
                clearDialog.setDrawView(drawView);
                clearDialog.show(getActivity().getFragmentManager(), TAG);*/
                dialogTest(v);

            }
        };

    //Assume dialogTest is called as the onClick function of a GButton
    private void dialogTest(View v)
    {
        GDialog diag = new GDialog(v.getContext(),
                "Ryd tegnebræt?",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(drawView != null && drawView.drawStack != null){
                            drawView.drawStack.entities.clear();
                            drawView.invalidate();

                    /*Neeeded as selectionhandler would have a deleted item selected otherwise*/
                            if (drawView.currentHandler instanceof SelectionHandler)
                                ((SelectionHandler)drawView.currentHandler).deselect();

                        }

                    }
                });
        diag.show();
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
