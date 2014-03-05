package dk.aau.cs.giraf.pictocreator.canvas;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.audiorecorder.AudioHandler;
import dk.aau.cs.giraf.pictocreator.audiorecorder.DecibelMeterView;
import dk.aau.cs.giraf.pictocreator.audiorecorder.RecordThread;

/**
 * Created by Praetorian on 05-03-14.
 */
public class ClearDialogFragment extends DialogFragment {
    private static final String TAG = "ClearDialogFragment";

    private View view;

    private ImageButton acceptButton;

    private ImageButton cancelButton;

    private DrawView drawView;
    /**
     * Constructor for the Dialog
     * Left empty on purpose
     */
    public ClearDialogFragment(){

    }

    /**
     * Method called when the dialog is first created
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NO_TITLE;

        setStyle(style, 0);
    }

    /**
     * Method called when the view for the dialog is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.clear_dialog, container);

        final Dialog tmpDialog = getDialog();

        tmpDialog.setCanceledOnTouchOutside(false);

        acceptButton = (ImageButton) view.findViewById(R.id.clear_positive_button);

        cancelButton = (ImageButton) view.findViewById(R.id.clear_negative_button);

        cancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                //Toast.makeText(getActivity(), "File deleted", Toast.LENGTH_LONG).show();
                tmpDialog.cancel();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                if(drawView != null && drawView.drawStack != null){
                    drawView.drawStack.entities.clear();
                    drawView.invalidate();
                }

                tmpDialog.dismiss();
            }
        });

        return view;
    }

    public void setDrawView(DrawView drawView){
        this.drawView = drawView;
    }
    /**
     * Method called when the dialog is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }
}
