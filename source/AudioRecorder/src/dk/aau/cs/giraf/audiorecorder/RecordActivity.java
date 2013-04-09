package dk.aau.cs.giraf.audiorecorder;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unused")
public class RecordActivity extends Activity {

    Toast toast = null;

    private RecordButton recordButton = null;

    private static final String TAG = "Audiorecorder";
    private static String fileName = null;

    private MediaRecorder recorder = null;


    public RecordActivity(){
        Log.d(TAG, "Constructor started");

        File soundFileDir = getDir();

        if(!soundFileDir.exists() && !soundFileDir.mkdirs()) {
            Log.d(TAG, "Cannot create directory for the image");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddd-HHmmss");
        String date = dateFormat.format(new Date());
        String soundFile = "GSound_" + date + ".3gp";

        fileName = soundFileDir.getPath() + File.separator + soundFile;

    }
    private File getDir() {
        File storageDir;
        if(hasExternalStorage()) {
            storageDir = Environment.getExternalStorageDirectory();
        }
        else {
            storageDir = Environment.getRootDirectory();
        }
        return (new File(storageDir, "GirafSounds"));
    }

    private boolean hasExternalStorage() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    private void onRecord(boolean start){
    	Log.d(TAG, "inRecord()");

    	if(start){
            startRecording();
        }
        else {
            stopRecording();
        }
    }

    public void startRecording(){
    	Log.d(TAG, "startRecording()");
    	try{
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(fileName);
    	}
    	catch (Exception e){
    		Log.d(TAG, "OH YOU FUCKED IT UP");
    	}

        try{
            recorder.prepare();
        }
        catch (IOException exception){
            Log.i(TAG, "prepare() failed");
        }

        recorder.start();
    }

    public void stopRecording(){
    	Log.d(TAG, "stopRecording()");
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    class RecordButton extends Button{
        boolean recording = true;

        OnClickListener clicker = new OnClickListener(){
                public void onClick(View view){
                    onRecord(recording);
                    if(recording){
                        toast = Toast.makeText(getContext(), "Press Again to stop", Toast.LENGTH_LONG);
                        toast.show();
                        setText("Stop recording");
                    }
                    else {
                        toast = Toast.makeText(getContext(), "Press Again to start", Toast.LENGTH_LONG);
                        toast.show();
                        setText("Start recording");
                    }
                    recording = !recording;
                }
            };

        public RecordButton(Context context){
            super(context);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	Log.d(TAG, "onCreate()");

    	System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.txt");

        RelativeLayout layout = new RelativeLayout(this);
        recordButton = new RecordButton(this);

        layout.addView(recordButton,
                       new RelativeLayout.LayoutParams(
                                                       ViewGroup.LayoutParams.WRAP_CONTENT,
                                                       ViewGroup.LayoutParams.WRAP_CONTENT)
                       );

        Log.d(TAG, "onCreate()");
        setContentView(layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record, menu);
        return true;
    }

    @Override
    public void onPause(){
        super.onPause();
        if (recorder != null){
            recorder.release();
            recorder = null;
        }
    }

}
