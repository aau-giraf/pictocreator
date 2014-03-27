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
import android.widget.ToggleButton;

import dk.aau.cs.giraf.oasis.lib.models.Pictogram;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.canvas.BackgroundSingleton;
import dk.aau.cs.giraf.pictogram.AudioPlayer;

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

    private ToggleButton recordButton;

    private ImageButton acceptButton;

    private ImageButton cancelButton;

    private ImageButton playButton;

    private LinearLayout recordLayout;

    int soundIDPlaying;

    private MediaPlayer mediaPlayer;
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

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mediaPlayer.setVolume(volume, volume);
                isPlaying = true;
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                switchLayoutPlayStopButton();
            }
        });
    }

    boolean isPlaying = false;
    private void switchLayoutPlayStopButton(){
        if(isPlaying){
            playButton.setBackgroundResource(R.drawable.stop_preiview_xml);
        }
        else{
            playButton.setBackgroundResource(R.drawable.play_button_xml);
        }
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

        playButton = (ImageButton) view.findViewById(R.id.playButton);


        recordLayout = (LinearLayout) view.findViewById(R.id.recordLayout);

        if (BackgroundSingleton.getInstance().background != null)
            recordLayout.setBackgroundDrawable(BackgroundSingleton.getInstance().background);
        else
            recordLayout.setBackgroundResource(R.drawable.fragment_background);

        /*
        * On click listener for stop playing recording
        * */
        OnClickListener stopClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
            }
        };
        /**
         * On click listener for recording audio
         */
        OnClickListener clickListener = new OnClickListener() {
                @Override
				public void onClick(View view) {
                    if (((ToggleButton) view).isChecked()) {
                        recThread.start();
                        recordButton.setChecked(true);
                        recordButton.setPressed(true);
                        recordButton.setBackgroundResource(R.drawable.stop_preiview_xml);
                    }
                    else {
                        recThread.stop();
                        decibelMeter.setLevel(0);
                        recordButton.setChecked(false);
                        recordButton.setPressed(false);
                        recordButton.setBackgroundResource(R.drawable.record_button_xml);
                        hasRecorded = true;
                    }
                }
            };
        playButton.setOnClickListener(playClickListener);

        cancelButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View view){
                    recThread.onCancel();
                    stopMusic();
                    hasRecorded = false;
                    //Toast.makeText(getActivity(), "File deleted", Toast.LENGTH_LONG).show();
                    tmpDialog.cancel();
                }
            });

        acceptButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    recThread.onAccept();
                    hasRecorded = false;
                    tmpDialog.dismiss();
                }
            });

        recordButton.setOnClickListener(clickListener);

        return view;
    }


    public void stopMusic(){
        mediaPlayer.stop();
    }

    private float volume;
    public void loadMusic(){
        try{

            AudioManager audioManager = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
            float actualVolume = (float) audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume = actualVolume / maxVolume;
            mediaPlayer.reset();
            FileInputStream temp;
            if(!hasRecorded){
                Log.i(TAG, "loading file: " + handler.getFinalPath());
                temp = new FileInputStream(handler.getFinalPath());
            }
            else{
                Log.i(TAG, "loading file: " + handler.getFilePath());
                temp = new FileInputStream(handler.getFilePath());
            }
            mediaPlayer.setDataSource(temp.getFD());
            mediaPlayer.prepareAsync();
        }
        catch (IOException e){
            Log.e(TAG, "Could not load music");
        }
    }

    boolean hasRecorded;
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
            if(isPlaying)
                stopMusic();
            else
                loadMusic();
            isPlaying = !isPlaying;
            switchLayoutPlayStopButton();
        }
    };
}
