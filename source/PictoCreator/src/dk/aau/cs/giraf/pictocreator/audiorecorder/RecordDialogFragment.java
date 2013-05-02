package dk.aau.cs.giraf.pictocreator.audiorecorder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import dk.aau.cs.giraf.pictocreator.R;

/**
 * Class for the dialog used for recording in the croc application
 * The class implement RecordInterface,
 * this is done to bind the decibelmeter and the recorder together
 *
 * @author Croc
 */

/*
  How to use:

  Copy-Paste the below, and make sure the layout have a button with id:start_dialog_button,
  then everything should work fine

  final RecordDialogFragment recordDialog = new RecordDialogFragment();

  OnClickListener clickListener = new OnClickListener() {
  public void onClick(View view) {
  recordDialog.show(getFragmentManager(), TAG);
  }
  };

  Button dialogButton = (Button) findViewById(R.id.start_dialog_button);

  dialogButton.setOnClickListener(clickListener);

 */
public class RecordDialogFragment extends DialogFragment implements RecordInterface {

    private static final String TAG = "RecordDialogFragment";

    private View view;

    AudioHandler handler;

    DecibelMeterView decibelMeter;

    RecordThread recThread;

    ToggleButton recordButton;

    ImageButton acceptButton;

    ImageButton cancelButton;

    /**
     * Constructor for the Dialog
     * Left empty on purpose
     */
    public RecordDialogFragment() {

    }

    /**
     * Override function for the RecordInterface
     * @param decibelValue The value to set in the decibelmeter
     */
    @Override
    public void decibelUpdate(double decibelValue){

        final double dbValue = decibelValue;

        decibelMeter.post(new Runnable() {
                /**
                 * Function to run when the Thread is started
                 */
                @Override
                public void run(){
                    decibelMeter.setLevel(dbValue);
                }
            });
    }

    /**
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NO_TITLE;

        setStyle(style, 0);
    }

    /**
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.record_dialog, container);

        final Dialog tmpDialog = getDialog();

        tmpDialog.setCanceledOnTouchOutside(false);

        handler = new AudioHandler();

        recThread = new RecordThread(handler, this);

        recordButton = (ToggleButton) view.findViewById(R.id.record_button);

        decibelMeter = (DecibelMeterView) view.findViewById(R.id.decibel_meter);

        acceptButton = (ImageButton) view.findViewById(R.id.positive_dialog_button);

        cancelButton = (ImageButton) view.findViewById(R.id.negative_dialog_button);

        OnClickListener clickListener = new OnClickListener() {
                public void onClick(View view) {
                    if (((ToggleButton) view).isChecked()) {
                        recThread.start();
                    }
                    else {
                        recThread.stop();
                        decibelMeter.setLevel(0);
                    }
                }
            };

        cancelButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    recThread.onCancel();
                    //Toast.makeText(getActivity(), "File deleted", Toast.LENGTH_LONG).show();
                    tmpDialog.cancel();
                }
            });

        acceptButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    recThread.onAccept();
                    tmpDialog.dismiss();
                }
            });

        recordButton.setOnClickListener(clickListener);

        return view;
    }

}
