package dk.aau.cs.giraf.pictocreator.audiorecorder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GCancelButton;
import dk.aau.cs.giraf.gui.GToggleButton;
import dk.aau.cs.giraf.oasis.lib.models.Pictogram;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictogram.AudioPlayer;
import dk.aau.cs.giraf.pictogram.PictoMediaPlayer;

import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.media.AudioManager;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class for the dialog used for recording in the croc application
 * The class extends {@link DialogFragment} and implement {@link RecordInterface}
 * this is done to bind the decibelmeter and the recorder together
 *
 * @author Croc
 */
public class RecordDialogFragment extends DialogFragment implements RecordInterface {

    private static final String TAG = "RecordDialogFragment";

    //Variables for soundPreview
    boolean loaded;

    private int soundID;

    private View view;

    private AudioHandler handler;

    private DecibelMeterView decibelMeter;

    private RecordThread recThread;

    private GToggleButton recordButton;

    private GButton acceptButton;

    private GButton cancelButton;

    private GButton playButton;

    private LinearLayout recordLayout;

    int soundIDPlaying;

    private Boolean recordingExists;

    private boolean hasRecorded;

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
    public void decibelUpdate(double decibelValue){

        final double dbValue = decibelValue;

        decibelMeter.post(new Runnable() {
            /**
             * Function to run when the Thread is started
             */
            @Override
            public void run() {
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

        mediaPlayer = new PictoMediaPlayer(this.getActivity());
        mediaPlayer.setCustomListener(new myCustomListener());
    }

    private void switchLayoutPlayStopButton(){
        try{
            if(mediaPlayer.isPlaying()){
                playButton.setText("Stop");
                playButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.stop_icon), null, null);
                Log.i(TAG, "changed to stop icon");
            }
            else{
                playButton.setText("Afspil");
                playButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.play_icon), null, null);
                Log.i(TAG, "changed to play icon");
            }
        }
        catch(IllegalStateException e){
            Log.e(TAG, e.getMessage());
        }

        //Did not work with invalidate so had to use dirty fix, reference:
        int temp = playButton.getVisibility();
        playButton.setVisibility(View.GONE);
        playButton.setVisibility(temp);

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

        recordButton = (GToggleButton) view.findViewById(R.id.record_button);

        decibelMeter = (DecibelMeterView) view.findViewById(R.id.decibel_meter);

        acceptButton = (GButton) view.findViewById(R.id.record_positive_button);
        acceptButton.setEnabled(false);

        cancelButton = (GButton) view.findViewById(R.id.record_negative_button);

        playButton = (GButton) view.findViewById(R.id.playButton);
        recordingExists = (new File(handler.getFinalPath()).exists());

        recordLayout = (LinearLayout) view.findViewById(R.id.recordLayout);
        recordLayout.setBackgroundResource(R.drawable.fragment_background);

        /*
        * On click listener for stop playing re+cording
        * */
        OnClickListener stopClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stopSound();
            }
        };
        /**
         * On click listener for recording audio
         */
        OnClickListener clickListener = new OnClickListener() {
                @Override
				public void onClick(View view) {
                    if (((GToggleButton) view).isToggled()) {
                        recThread.start();
                      //recordButton.setChecked(true);
                        recordButton.setToggled(true);
                        recordButton.setText("Stop");
                        recordButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.stop_icon),null,null);
                      //  recordButton.SetImage(getResources().getDrawable(R.drawable.stop_icon));
                        playButton.setEnabled(false);
                        acceptButton.setEnabled(false);
                    }
                    else {
                        recThread.stop();
                        decibelMeter.setLevel(0);
                        //recordButton.setChecked(false);
                        recordButton.setToggled(false);
                        recordButton.setText("Optag");
                        recordButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.record_icon),null,null);
                        hasRecorded = true;
                        playButton.setEnabled(true);
                        acceptButton.setEnabled(true);
                        recordingExists = true;
                    }
                }
            };
        playButton.setOnClickListener(playClickListener);

        cancelButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View view){
                    mediaPlayer.stopSound();
                    recThread.onCancel();
                    hasRecorded = false;
                    tmpDialog.cancel();
                }
            });

        acceptButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    mediaPlayer.stopSound();
                    recThread.onAccept();
                    hasRecorded = false;
                    tmpDialog.dismiss();
                }
            });

        recordButton.setOnClickListener(clickListener);

        return view;
    }

    public void loadMusic(){
        try{
            if(!hasRecorded){

                Log.i(TAG, "loading file: " + handler.getFinalPath());
                mediaPlayer.setDataSource(handler.getFinalPath());
            }
            else{
                Log.i(TAG, "loading file: " + handler.getFilePath());
                mediaPlayer.setDataSource(handler.getFilePath());
            }
            mediaPlayer.playSound();
        }
        catch (IOException e){
            Log.e(TAG, "Could not load music");
        }
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
    OnClickListener playClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(recordingExists){
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stopSound();
                else
                    loadMusic();
                switchLayoutPlayStopButton();
               // StopButton();
            }
            else{
                Toast.makeText(getActivity(), "Ingen optagelse", Toast.LENGTH_LONG).show();
            }
        }
    };

    private class myCustomListener implements PictoMediaPlayer.CompleteListener{
        @Override
        public void soundDonePlaying() {
            switchLayoutPlayStopButton();
        }
    }
}
