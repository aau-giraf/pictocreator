package dk.aau.cs.giraf.audiorecorder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

// import android.os.Bundle;
// import android.os.Environment;
// import android.app.Activity;
// import android.view.Menu;
// import android.view.View;
// import android.view.ViewGroup;
// import android.widget.Button;
// import android.widget.RelativeLayout;
// import android.widget.Toast;
// import android.content.Context;
// import android.media.MediaRecorder;
// import android.util.Log;

// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.text.SimpleDateFormat;
// import java.util.Date;

@SuppressWarnings("unused")
public class RecordActivity extends Activity implements MicrophoneThreadListener {

    private static final String TAG = "RecordActivity";

    MicrophoneThread micThread;
    BarLevelDrawable decibelMeter;

    double offSetDB = -10;

    double gain = 2500.0/ Math.pow(10.0, 90.0 / 20.0);

    double rmsSmoothed, alpha = 0.9;

    private int sampleRate;
    private int audioSource;

    private volatile boolean isDrawing;
    private volatile int drawingCollided;

    // Toast toast = null;

    // private RecordButton recordButton = null;

    // private static final String TAG = "Audiorecorder";
    // private static String fileName = null;

    // private MediaRecorder recorder = null;


    public RecordActivity(){
        // Log.d(TAG, "Constructor started");

        // File soundFileDir = getDir();

        // if(!soundFileDir.exists() && !soundFileDir.mkdirs()) {
        //     Log.d(TAG, "Cannot create directory for the image");
        //     return;
        // }

        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddd-HHmmss");
        // String date = dateFormat.format(new Date());
        // String soundFile = "GSound_" + date + ".3gp";

        // fileName = soundFileDir.getPath() + File.separator + soundFile;

    }
    // private File getDir() {
    //     File storageDir;
    //     if(hasExternalStorage()) {
    //         storageDir = Environment.getExternalStorageDirectory();
    //     }
    //     else {
    //         storageDir = Environment.getRootDirectory();
    //     }
    //     return (new File(storageDir, "GirafSounds"));
    // }

    // private boolean hasExternalStorage() {
    //     return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    // }

    // private void onRecord(boolean start){
    // 	Log.d(TAG, "inRecord()");

    // 	if(start){
    //         startRecording();
    //     }
    //     else {
    //         stopRecording();
    //     }
    // }

    // public void startRecording(){
    // 	Log.d(TAG, "startRecording()");
    // 	try{
    //     recorder = new MediaRecorder();
    //     recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    //     recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    //     recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    //     recorder.setOutputFile(fileName);
    // 	}
    // 	catch (Exception e){
    // 		Log.d(TAG, "OH YOU FUCKED IT UP");
    // 	}

    //     try{
    //         recorder.prepare();
    //     }
    //     catch (IOException exception){
    //         Log.i(TAG, "prepare() failed");
    //     }

    //     recorder.start();
    // }

    // public void stopRecording(){
    // 	Log.d(TAG, "stopRecording()");
    //     recorder.stop();
    //     recorder.release();
    //     recorder = null;
    // }

    // class RecordButton extends Button{
    //     boolean recording = true;

    //     OnClickListener clicker = new OnClickListener(){
    //             public void onClick(View view){
    //                 onRecord(recording);
    //                 if(recording){
    //                     toast = Toast.makeText(getContext(), "Press Again to stop", Toast.LENGTH_LONG);
    //                     toast.show();
    //                     setText("Stop recording");
    //                 }
    //                 else {
    //                     toast = Toast.makeText(getContext(), "Press Again to start", Toast.LENGTH_LONG);
    //                     toast.show();
    //                     setText("Start recording");
    //                 }
    //                 recording = !recording;
    //             }
    //         };

    //     public RecordButton(Context context){
    //         super(context);
    //         setText("Start recording");
    //         setOnClickListener(clicker);
    //     }
    // }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	Log.d(TAG, "onCreate()");

        micThread = new MicrophoneThread(this);

        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_record);

        decibelMeter = (BarLevelDrawable)findViewById(R.id.decibel_meter);

        final ToggleButton recordButton = (ToggleButton)findViewById(R.id.record_button);

        ToggleButton.OnClickListener recordListener =
            new ToggleButton.OnClickListener() {
                @Override
                public void onClick(View view){
                    if(recordButton.isChecked()){
                        readPreferences();
                        micThread.setSampleRate(sampleRate);
                        micThread.setAudioSource(audioSource);
                        micThread.start();
                    }
                    else {
                        micThread.stop();
                        decibelMeter.setLevel(0.1);
                    }
                }
            };
        recordButton.setOnClickListener(recordListener);

    	// System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.txt");

        // RelativeLayout layout = new RelativeLayout(this);
        // recordButton = new RecordButton(this);

        // layout.addView(recordButton,
        //                new RelativeLayout.LayoutParams(
        //                                                ViewGroup.LayoutParams.WRAP_CONTENT,
        //                                                ViewGroup.LayoutParams.WRAP_CONTENT)
        //                );

    }

    private void readPreferences(){
        SharedPreferences preferences = getSharedPreferences("Record", MODE_PRIVATE);
        sampleRate = preferences.getInt("SampleRate", 8000);
        audioSource = preferences.getInt("AudioSource", MediaRecorder.AudioSource.MIC);
    }

    @Override
    public void processAudioFrame(short[] audioFrame){
        if (!isDrawing){
            isDrawing = true;
            double rms = 0;
            int length = audioFrame.length;
            int i;

            for(i = 0; i < length; i++){
                rms += audioFrame[i]*audioFrame[i];
            }
            rms = Math.sqrt(rms/length);

            rmsSmoothed = rmsSmoothed * alpha + (1 - alpha) *rms;
            final double rmsDB = 20.0 * Math.log10(gain * rmsSmoothed);

            decibelMeter.post(new Runnable() {

                    @Override
                    public void run() {

                        decibelMeter.setLevel((offSetDB + rmsDB) / 70);
                        System.out.println(TAG + "DB Level: " + ((offSetDB + rmsDB) / 70));
                        isDrawing = false;
                    }
                });
        }
        else {
            drawingCollided++;
            Log.v(TAG, "else state in processAudioFrame");
        }
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     // Inflate the menu; this adds items to the action bar if it is present.
    //     getMenuInflater().inflate(R.menu.record, menu);
    //     return true;
    // }

    // @Override
    // public void onPause(){
    //     super.onPause();
    //     if (recorder != null){
    //         recorder.release();
    //         recorder = null;
    //     }
    // }

}
