package dk.aau.cs.giraf.pictocreator.audiorecorder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import dk.aau.cs.giraf.pictocreator.R;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.media.AudioManager;
import android.content.Context;

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

    private Button playButton;

    private Button stopPlayButton;
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

        // Prepare the sound for previewing
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
                playSound();
            }
        });

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

        playButton = (Button) view.findViewById(R.id.playButton);

        stopPlayButton = (Button) view.findViewById(R.id.stopPlayButton);

        /*
        * On click listener for play recording
        * */
        OnClickListener playClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
                loadMusic();
            }
        };

        /*
        * On click listener for stop playing recording
        * */
        OnClickListener stopClikListener = new OnClickListener() {
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
                    }
                    else {
                        recThread.stop();
                        decibelMeter.setLevel(0);
                    }
                }
            };
        stopPlayButton.setOnClickListener(stopClikListener);
        playButton.setOnClickListener(playClickListener);
        cancelButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    recThread.onCancel();
                    stopMusic();
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


    public void stopMusic(){
        soundPool.stop(soundIDPlaying);
    }

    public void loadMusic(){
        soundID = soundPool.load(handler.getFilePath(),100000);
    }

    /**
     * Method called when the dialog is resumed
     */
    @Override
	public void onResume() {
		super.onResume();
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}

    private SoundPool soundPool;
    int soundIDPlaying;
    public void playSound(){
        AudioManager audioManager = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;

        // Is the sound loaded already?
        if (loaded) {
            soundIDPlaying = soundPool.play(soundID, volume, volume, 1, 0, 1f);
            Log.e(TAG, "Played sound");
        }
    }
}
