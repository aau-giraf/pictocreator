package dk.aau.cs.giraf.audiorecorderv2;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import android.widget.Button;

/**
 * Main activity for the audio recorder
 * @author Croc
 *
 */
public class RecordMainActivity extends Activity // implements RecordInterface
{

    private static final String TAG = "RecordMainActivity";

    // AudioHandler handler;

    // DecibelMeterView decibelMeter;

    // RecordThread recThread;

    // ToggleButton recordButton;

    /**
     * Called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_main);

        final RecordDialogFragment recordDialog = new RecordDialogFragment();
        // recordDialog.show(getFragmentManager(), TAG);

        // recordDialog.setTitle(R.string.record_dialog_title);

        OnClickListener clickListener = new OnClickListener() {
                public void onClick(View view) {
                    recordDialog.show(getFragmentManager(), TAG);
                }
            };

        Button dialogButton = (Button) findViewById(R.id.start_dialog_button);

        dialogButton.setOnClickListener(clickListener);

    //     handler = new AudioHandler();

    //     recThread = new RecordThread(handler, this);

    //     recordButton = (ToggleButton) findViewById(R.id.record_button);

    //     decibelMeter = (DecibelMeterView) findViewById(R.id.decibel_meter);


    //     OnClickListener clickListener = new OnClickListener() {
    //             public void onClick(View view) {
    //                 if (((ToggleButton) view).isChecked()) {
    //                     recThread.start();
    //                 }
    //                 else {
    //                     recThread.stop();
    //                 }
    //             }
    //         };
    // recordButton.setOnClickListener(clickListener);

    }

    /**
     * Creates the action bar
     */
    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     // Inflate the menu; this adds items to the action bar if it is present.
    //     getMenuInflater().inflate(R.menu.record_main, menu);
    //     return true;
    // }

    /**
     * Override function for the RecordInterface
     * @param decibelValue The value to set in the decibelmeter
     */
    // @Override
    // public void decibelUpdate(double decibelValue){

    //     final double dbValue = decibelValue;

    //     decibelMeter.post(new Runnable() {
    //             /**
    //              * Function to run when the Thread is started
    //              */
    //             @Override
    //             public void run(){
    //                 decibelMeter.setLevel(dbValue);
    //             }
    //         });
    // }

    // /**
    //  * Override function
    //  * Called when the activity is paused
    //  */
    // @Override
    // protected void onPause(){
    //     super.onPause();
    //     recThread.mediaRecorder.release();
    // }

    //         decibelMeter.post(new Runnable() {

    //                 @Override
    //                 public void run() {

    //                     decibelMeter.setLevel((offSetDB + rmsDB) / 70);
    //                     System.out.println(TAG + "DB Level: " + ((offSetDB + rmsDB) / 70));
    //                     isDrawing = false;
    //                 }
    //             });
    //     }


}
