package dk.aau.cs.giraf.audiorecorder;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

@SuppressWarnings("unused")
public class RecordActivity extends Activity{ // implements MicrophoneThreadListener {

    private static final String TAG = "RecordActivity";

    TextView mStatusView;

    Thread runner;

    private static double mEMA = 0.0;

    static final private double EMA_FILTER = 0.6;

    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };

    final Handler mHandler = new Handler();

    MediaRecorder recorder;

    // Creates the file/path for saving the recorded file
    AudioHandler handler;

    public RecordActivity(){
        handler = new AudioHandler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_record);

        // LinearLayout layout = new LinearLayout(this);

        ToggleButton recordButton = (ToggleButton) findViewById(R.id.recordButton);

        recordButton.setOnClickListener(
                                        new OnClickListener() {
                                            public void onClick(View view) {
                                                if (((ToggleButton) view).isChecked()) {
                                                    startRecording();
                                                }
                                                else {
                                                    stopRecording();
                                                }
                                            }
                                        });


        // layout.addView(recordButton,
        //                new LinearLayout.LayoutParams(
        //                                              ViewGroup.LayoutParams.WRAP_CONTENT,
        //                                              ViewGroup.LayoutParams.WRAP_CONTENT,
        //                                              0));

        mStatusView = (TextView) findViewById(R.id.status);

        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(200);
                            Log.i("Noise", Double.toString((getAmplitudeEMA() % 100)) + " Db" );
                        }
                        catch (InterruptedException e) {
                            Log.e("Noise", "Interrupted");
                        };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }

        Log.d(TAG, "onCreate()");
    }

    public void startRecording(){
        recorder = new MediaRecorder();

        try{
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(handler.getFilePath());

            recorder.prepare();
            recorder.start();
        }
        catch(IllegalStateException e){
            Log.e(TAG, "You fucked the order up");
        }
        catch(IOException e){
            Log.e(TAG, "Some IOEx fucked the mediarecorder up");
        }
    }

    public void stopRecording(){
        recorder.stop();

        recorder.reset();

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(recorder != null){
            recorder.release();
            recorder = null;
        }
        runner = null;

        Log.i(TAG, "soundDb: " + soundDb(100));
    }

    public void updateTv(){
        mStatusView.setText(Double.toString((Math.floor(getAmplitudeEMA() % 100))) + " dB");
    }

    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }

    public double getAmplitude() {
        if (recorder != null)
            return  (recorder.getMaxAmplitude());
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

}

    // class RecordButton extends ToggleButton {

    //     OnClickListener clicker = new OnClickListener() {
    //             public void onClick(View v) {
    //                 if (isChecked()) {
    //                     startRecording();
    //                 }
    //                 else {
    //                     stopRecording();
    //                 }
    //             }
    //         };

    //     public RecordButton(Context context){
    //         super(context);
    //         setOnClickListener(clicker);
    //     }
    // }

// readPreferences();
// micThread.setSampleRate(sampleRate);
// micThread.setAudioSource(audioSource);
// micThread.start();
    //     decibelMeter = (BarLevelDrawable)findViewById(R.id.decibel_meter);

    //     final ToggleButton recordButton = (ToggleButton)findViewById(R.id.record_button);

    //     ToggleButton.OnClickListener recordListener =
    //         new ToggleButton.OnClickListener() {
    //             @Override
    //             public void onClick(View view){
    //                 if(recordButton.isChecked()){
    //                     readPreferences();
    //                     micThread.setSampleRate(sampleRate);
    //                     micThread.setAudioSource(audioSource);
    //                     micThread.start();
    //                 }
    //                 else {
    //                     micThread.stop();
    //                 }
    //             }
    //         };
    //     recordButton.setOnClickListener(recordListener);
    // }

    // private void readPreferences(){
    //     SharedPreferences preferences = getSharedPreferences("Record", MODE_PRIVATE);
    //     sampleRate = preferences.getInt("SampleRate", 8000);
    //     audioSource = preferences.getInt("AudioSource", MediaRecorder.AudioSource.VOICE_RECOGNITION);
    // }

    // @Override
    // public void processAudioFrame(short[] audioFrame){
    //     if (!isDrawing){
    //         Log.d(TAG, "If statement, isDrawing = " + isDrawing);
    //         isDrawing = true;
    //         double rms = 0;
    //         int length = audioFrame.length;
    //         int i;

    //         for(i = 0; i < length; i++){
    //             rms += audioFrame[i]*audioFrame[i];
    //         }
    //         rms = Math.sqrt(rms/length);

    //         rmsSmoothed = rmsSmoothed * alpha + (1 - alpha) *rms;
    //         final double rmsDB = 20.0 * Math.log10(gain * rmsSmoothed);

    //         decibelMeter.post(new Runnable() {

    //                 @Override
    //                 public void run() {

    //                     decibelMeter.setLevel((offSetDB + rmsDB) / 70);
    //                     System.out.println(TAG + "DB Level: " + ((offSetDB + rmsDB) / 70));
    //                     isDrawing = false;
    //                 }
    //             });
    //     }
    //     else {
    //         drawingCollided++;
    //         Log.v(TAG, "else state in processAudioFrame");
    //     }
    // }
    // @Override
    // public void onPause(){
    //     super.onPause();
    //     micThread.audioHandler.stopHandler();
    // }


    // private void playFile(String file){

    //     try{
    //         mediaPlayer = new MediaPlayer();

    //         mediaPlayer.setDataSource(file);
    //         mediaPlayer.prepare();
    //         mediaPlayer.start();
    //     }
    //     catch(IOException e){
    //         Log.d(TAG, "Prepare mediaplayer failed");
    //     }

    //     mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

    //             @Override
    //             public void onCompletion(MediaPlayer mp) {

    //                 mp.stop();
    //                 mp.release();
    //             }

    //         });
    // }

    // public void stopPlaying(){
    //     mediaPlayer.stop();
    //     mediaPlayer.release();
    //     mediaPlayer = null;
    // }

    // class PlayButton extends Button {
    //     boolean mStartPlaying = true;

    //     OnClickListener clicker = new OnClickListener() {
    //         public void onClick(View v) {
    //             onPlay(mStartPlaying);
    //             if (mStartPlaying) {
    //                 setText("Stop playing");
    //             }
    //             else {
    //                 setText("Start playing");
    //             }
    //             mStartPlaying = !mStartPlaying;
    //         }
    //     };

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     // Inflate the menu; this adds items to the action bar if it is present.
    //     getMenuInflater().inflate(R.menu.record, menu);
    //     return true;
    // }
