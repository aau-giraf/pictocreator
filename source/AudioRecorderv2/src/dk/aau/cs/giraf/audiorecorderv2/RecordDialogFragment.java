package dk.aau.cs.giraf.audiorecorderv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.BitmapFactory;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class RecordDialogFragment extends DialogFragment implements RecordInterface {

    private static final String TAG = "RecordDialogFragment";

    private View view;

    AudioHandler handler;

    DecibelMeterView decibelMeter;

    RecordThread recThread;

    ToggleButton recordButton;

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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // int style = DialogFragment.STYLE_NO_TITLE;

        // setStyle(style, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.record_dialog, container);

        handler = new AudioHandler();

        recThread = new RecordThread(handler, this);

        recordButton = (ToggleButton) view.findViewById(R.id.record_button);

        decibelMeter = (DecibelMeterView) view.findViewById(R.id.decibel_meter);

        OnClickListener clickListener = new OnClickListener() {
                public void onClick(View view) {
                    if (((ToggleButton) view).isChecked()) {
                        recThread.start();
                    }
                    else {
                        recThread.stop();
                    }
                }
            };
        recordButton.setOnClickListener(clickListener);

        return view;
    }

}
