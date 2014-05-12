package dk.aau.cs.giraf.pictocreator.audiorecorder;

import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

/**
 * Thread for recording audio and storing audiofile
 * @author Croc
 *
 */
public class RecordThread implements Runnable {

    private static final String TAG = "RecordThread";

    private static final int startAmpl = 33000;

    private final int audioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    private final int outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
    private final int audioEncoder = MediaRecorder.AudioEncoder.AMR_NB;

    private String outputFilePath = null;

    private AudioHandler audioHandler;

    private MediaRecorder mediaRecorder = null;

    private double decibel = 0;

    private boolean isRunning = false;

    private Thread recordThread;

    private final RecordInterface recordInterface;

    /**
     * Constructor for the thread
     * @param audioHandler The handler to use for storing the audiofile
     * @param recordInterface The interface to communicate with
     */
    public RecordThread(AudioHandler audioHandler, RecordInterface recordInterface){
        this.recordInterface = recordInterface;
        this.audioHandler = audioHandler;
        this.outputFilePath = this.audioHandler.getFilePath();
    }

    /**
     * Function for starting the thread
     */
    public void start(){
        if(!isRunning){
            isRunning = true;
            recordThread = new Thread(this);

            if(mediaRecorder == null){
                mediaRecorder = new MediaRecorder();
            }

            try {
                mediaRecorder.setAudioSource(audioSource);
                mediaRecorder.setOutputFormat(outputFormat);
                mediaRecorder.setAudioEncoder(audioEncoder);
                mediaRecorder.setOutputFile(outputFilePath);

            }
            catch (IllegalStateException ex){
                Log.e(TAG, "The order in the media recorder is not correct!", ex.fillInStackTrace());
            }

            recordThread.start();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            }
            catch (IOException ex){
                Log.e(TAG, "Could not prepare or start the mediaRecorder", ex.fillInStackTrace());
            }
        }
    }

    /**
     * Function for stopping the thread
     */
    public void stop(){
        try {
            if(isRunning){
                isRunning = false;
                mediaRecorder.stop();
                mediaRecorder.reset();

                recordThread.join();
            }
        }
        catch (InterruptedException interrupt){
            Log.v(TAG, "Interrupted", interrupt);
        }
        catch (IllegalStateException e){
            Log.v(TAG, "Illegalstate", e.fillInStackTrace());
        }

        recordInterface.decibelUpdate(0);
    }

    /**
     * Override function
     * Function to execute when the thread is started
     */
    @Override
    public void run(){
        while(isRunning){
            try {
                //Sleeps to slow down the reading of the amplitude, such that the GUI can keep up with the readings.
                Thread.sleep(100);

                // The value is divided by 2000 to give a value within range
                // of the decibel-meter
                decibel = getAmplitude() / 2000;
                recordInterface.decibelUpdate(decibel);
            }
            catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted.", e.fillInStackTrace());
            }
        }
    }

    /**
     * Function for getting the amplitude from the microphone
     * @return The amplitude value of the microphone
     */
    private double getAmplitude() {
        if (mediaRecorder != null)
            return  (mediaRecorder.getMaxAmplitude());
        else
            return 0;

    }

    /**
     * Function called when the dialog is canceled,
     * and deletes the saved file, if there is any
     */
    public void onCancel(){
        stop();
        audioHandler.deleteTempFile();
    }

    /**
     * Function called when the dialog is ended by ok,
     * copies the tmp file to the final file and
     * deletes the tmp save file
     */
    public void onAccept(){
        stop();
        audioHandler.saveFinalFile();
    }
}
