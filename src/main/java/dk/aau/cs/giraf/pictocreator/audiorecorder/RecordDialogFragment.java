package dk.aau.cs.giraf.pictocreator.audiorecorder;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GToast;
import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictogram.CompleteListener;
import dk.aau.cs.giraf.pictogram.PictoMediaPlayer;

import java.io.File;

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
    private DecibelMeterView decibelMeter;
    private LinearLayout recordLayout;

    private GirafButton recordButton;
    private GirafButton acceptButton;
    private GirafButton cancelButton;
    private GirafButton playButton;

    private AudioHandler handler;
    private RecordThread recThread;

    private boolean recordingExists;
    private boolean hasRecorded = false;

    private PictoMediaPlayer mediaPlayer;

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
    public void decibelUpdate(final double decibelValue){
        decibelMeter.post(new Runnable() {
            /**
             * Function to run when the Thread is started
             */
            @Override
            public void run() {
                decibelMeter.setLevel(decibelValue);
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

        mediaPlayer = new PictoMediaPlayer(this.getActivity());
        mediaPlayer.setCustomListener(new CustomMediaPlayerListener());
    }

    /**
     * Method called when the view for the dialog is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.record_dialog, container);

        final Dialog tmpDialog = getDialog();

        tmpDialog.setCanceledOnTouchOutside(false);

        handler = new AudioHandler(getActivity());

        recThread = new RecordThread(handler, this);

        recordButton = (GirafButton) view.findViewById(R.id.record_button);
        recordButton.setOnClickListener(recordClickListener);

        decibelMeter = (DecibelMeterView) view.findViewById(R.id.decibel_meter);

        playButton = (GirafButton) view.findViewById(R.id.playButton);
        playButton.setOnClickListener(playClickListener);

        recordingExists = (new File(handler.getFinalPath()).exists());

        recordLayout = (LinearLayout) view.findViewById(R.id.recordLayout);
        recordLayout.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.SOLID));

        acceptButton = (GirafButton) view.findViewById(R.id.record_positive_button);
        acceptButton.setEnabled(false);
        acceptButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                mediaPlayer.stopSound();
                recThread.onAccept();
                hasRecorded = false;
                tmpDialog.dismiss();
            }
        });

        cancelButton = (GirafButton) view.findViewById(R.id.record_negative_button);
        cancelButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                mediaPlayer.stopSound();
                recThread.onCancel();
                hasRecorded = false;
                tmpDialog.cancel();
            }
        });

        return view;
    }

    /**
     * Switches between play and stop icon on playButton, conditioned on the mediaPlayer playing or not.
     */
    private void switchLayoutPlayStopButton(){
        try{
            if(mediaPlayer.isPlaying()){
                //playButton.setText("Stop");
                playButton.setIcon(getResources().getDrawable(R.drawable.stop));
                Log.i(TAG, "changed to stop icon");
            }
            else{
                // playButton.setText("Afspil");
                playButton.setIcon(getResources().getDrawable(R.drawable.play));
                Log.i(TAG, "changed to play icon");
            }
        }
        catch(IllegalStateException e){
            Log.e(TAG, e.getMessage());
        }

        //Did not work with invalidate so had to use dirty fix
        playButton.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);

    }


    /**
     * Plays the sound that is currently assigned to the pictogram or a newly recorded sound.
     * The newly recorded sound is taking priority.
     */
    private void playSound(){
        if(!hasRecorded){
            mediaPlayer.setDataSource(handler.getFinalPath());
        }
        else{
            mediaPlayer.setDataSource(handler.getFilePath());
        }
        mediaPlayer.playSound();
    }

    /**
     * Method called when the dialog is resumed
     */
    @Override
	public void onResume() {
		super.onResume();
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}

    /*
    * On click listener for play recording
    * */
    private final OnClickListener playClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(recordingExists){
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stopSound();
                }
                else{
                    playSound();
                }
                switchLayoutPlayStopButton();
            }
            else{
                GToast.makeText(getActivity(), "Ingen optagelse", Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * On click listener for recording audio
     */
    private final OnClickListener recordClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!((GirafButton) view).isChecked()) { // TODO FIX change to isToggled
                recThread.start();
                recordButton.toggle();
                //recordButton.setText("Stop");
                recordButton.setIcon(getResources().getDrawable(R.drawable.stop));
                playButton.setEnabled(false);
                acceptButton.setEnabled(false);
            }
            else {
                recThread.stop();
                decibelMeter.setLevel(0);
                recordButton.toggle();
                //recordButton.setText("Optag");
                recordButton.setIcon(getResources().getDrawable(R.drawable.record));
                hasRecorded = true;
                playButton.setEnabled(true);
                acceptButton.setEnabled(true);
                recordingExists = true;
            }
        }
    };

    /**
     * A custom listener that implements CompleteListener from pictogram-lib, necessary to register when the sound is done playing.
     */
    private class CustomMediaPlayerListener implements CompleteListener{
        @Override
        public void soundDonePlaying() {
            switchLayoutPlayStopButton();
        }
    }
}
