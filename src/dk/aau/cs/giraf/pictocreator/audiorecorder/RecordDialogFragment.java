package dk.aau.cs.giraf.pictocreator.audiorecorder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import dk.aau.cs.giraf.pictocreator.R;

/**
 * Class for the dialog used for recording in the croc application
 * The class extends {@link DialogFragment} and implement {@link RecordInterface}
 * this is done to bind the decibelmeter and the recorder together
 *
 * @author Croc
 */
public class RecordDialogFragment extends DialogFragment implements RecordInterface {

    private static final String TAG = "RecordDialogFragment";

    private View view;

    private AudioHandler handler;

    private DecibelMeterView decibelMeter;

    private RecordThread recThread;

    private ToggleButton recordButton;

    private ImageButton acceptButton;

    private ImageButton cancelButton;


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
        view = inflater.inflate(R.layout.record_dialog, container);

        final Dialog tmpDialog = getDialog();

        tmpDialog.setCanceledOnTouchOutside(false);

        handler = new AudioHandler(getActivity());

        recThread = new RecordThread(handler, this);

        recordButton = (ToggleButton) view.findViewById(R.id.record_button);

        decibelMeter = (DecibelMeterView) view.findViewById(R.id.decibel_meter);

        acceptButton = (ImageButton) view.findViewById(R.id.record_positive_button);

        cancelButton = (ImageButton) view.findViewById(R.id.record_negative_button);
        /**
         * On click listener for recording audio
         */
        OnClickListener clickListener = new OnClickListener() {
                @Override
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
                public void onClick(View view){
                    recThread.onCancel();
                    //Toast.makeText(getActivity(), "File deleted", Toast.LENGTH_LONG).show();
                    tmpDialog.cancel();
                }
            });

        acceptButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    recThread.onAccept();
                    tmpDialog.dismiss();
                }
            });

        recordButton.setOnClickListener(clickListener);

        return view;
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