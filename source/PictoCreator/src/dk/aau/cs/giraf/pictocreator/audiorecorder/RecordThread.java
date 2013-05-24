package dk.aau.cs.giraf.pictocreator.audiorecorder;

import java.io.IOException;
import java.lang.IllegalStateException;
import java.lang.InterruptedException;

import android.media.*;
import android.util.Log;

/**
 * Thread for recording audio and storing audiofile
 * @author Croc
 *
 */
public class RecordThread implements Runnable {

    private static final String TAG = "RecordThread";

    private static final double EMA_FILTER = 0.6;

    int audioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    int outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
    int audioEncoder = MediaRecorder.AudioEncoder.AMR_NB;

    String outputFilePath = null;

    AudioHandler audioHandler;

    MediaRecorder mediaRecorder = null;

    private double startAmpl = 0;

    private double ema = 0.0;

    private double decibel = 0;

    private boolean isRunning = false;

    private Thread recordThread;

    private final RecordInterface _interface;

    /**
     * Constructor for the thread
     * @param handler The handler to use for storing the audiofile
     * @param recordInterface The interface to communicate with
     */
    public RecordThread(AudioHandler handler, RecordInterface recordInterface){
        _interface = recordInterface;
        audioHandler = handler;
        outputFilePath = audioHandler.getFilePath();
    }

    /**
     * Function for starting the thread
     */
    public void start(){
        if(!isRunning){
            isRunning = true;
            recordThread = new Thread(this);

            if(mediaRecorder == null){
                Log.d(TAG, "beginning init og MedioRecorder");
                mediaRecorder = new MediaRecorder();
                Log.d(TAG, "mediaRecorder intilized");
            }

            try {
                mediaRecorder.setAudioSource(audioSource);
                mediaRecorder.setOutputFormat(outputFormat);
                mediaRecorder.setAudioEncoder(audioEncoder);
                mediaRecorder.setOutputFile(outputFilePath);

            }
            catch (IllegalStateException ex){
                Log.e(TAG, "Fucked the order in the media recorder up!", ex.fillInStackTrace());
            }

            recordThread.start();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            }
            catch (IOException ex){
                Log.e(TAG, "Something else fucked the media recorder up", ex.fillInStackTrace());
            }
            startAmpl = 10;
            Log.d(TAG, "Start ampl: " + startAmpl);
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
                Log.d(TAG, "recordThread joined");
            }
        }
        catch (InterruptedException interrupt){
            Log.v(TAG, "Interrupted", interrupt);
        }
        catch (IllegalStateException e){
            Log.v(TAG, "Illegalstate", e.fillInStackTrace());
        }

        _interface.decibelUpdate(0);
    }

    /**
     * Override function
     * Function to execute when the thread is started
     */
    @Override
    public void run(){
        while(isRunning){
            try {
                Thread.sleep(500);
                decibel = (getAmplitudeEMA() / 10000) - 1.0;
                Log.d(TAG, "getAmplitudeEMA: " + getAmplitudeEMA());
                _interface.decibelUpdate(decibel);
            }
            catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted.", e.fillInStackTrace());
            }
        }
    }

    /**
     * Function for converting amplitude to decibel
     * @param amplitude The amplitude to convert
     * @return Decibel value of the amplitude
     */
    private double amplitudeToDecibel(int amplitude){
        // Formular from Wikipedia
        double tmpDecibel = 20 * Math.log10(amplitude / startAmpl);
        Log.d(TAG, "tmpDecibel: " + tmpDecibel);
        return tmpDecibel;
    }

    /**
     * Function for getting the amplitude from the microphone
     * @return The amplitude value of the microphone
     */
    public double getAmplitude() {
        if (mediaRecorder != null)
            return  (mediaRecorder.getMaxAmplitude());
        else
            return 0;

    }

    /**
     * Function for converting amplitude to ema
     * @return ema value of the amplitude
     */
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        ema = EMA_FILTER * amp + (1.0 - EMA_FILTER) * ema;
        return ema;
    }

    /**
     * Function called when the dialog is canceled,
     * and deletes the saved file, if there is any
     */
    public void onCancel(){
        stop();
        audioHandler.deleteFile();
    }

    /**
     * Function called when the dialog is ended by ok,
     * copies the tmp file to the final file and
     * deletes the tmp save file
     */
    public void onAccept(){
        audioHandler.saveFinalFile();
    }


}
