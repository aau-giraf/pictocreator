package dk.aau.cs.giraf.audiorecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

/**
 * This class handles the creation of the audio storage directory,
 * and the file for saving the recorder audiofile
 * @author Croc
 *
 */
public class AudioHandler {

    private static final String TAG = "AudioHandler";

    private String outputFilePath = null;

    /**
     * Constructor for the class.
     * Calls function to create the output file.
     */
    public AudioHandler(){
        createOutputFilePath();
    }

    /**
     * Function for getting the file path for the audiofile
     * @return A string representing the file path for the audiofile
     */
    public String getFilePath(){
        if(outputFilePath != null){
            return outputFilePath;
        }
        else {
            return null;
        }
    }

    /**
     * Function for creating the output file, and the path to the file
     * This function is called by the constructor
     */
    private void createOutputFilePath(){
        File soundFileDir = getDir();

        if(!soundFileDir.exists() && !soundFileDir.mkdirs()) {
            Log.d(TAG, "Cannot create directory for the sound");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String date = dateFormat.format(new Date());
        String audioFile = "GSound_" + date + ".3gp";

        String fileName = soundFileDir.getPath() + File.separator + audioFile;

        outputFilePath = fileName;
    }

    /**
     * Function for creating the directory for the output file
     * This function is called by createOutputFilePath.
     * @return File representing the output directory
     */
    private File getDir() {
        File storageDir;
        if(hasExternalStorage()) {
            storageDir = Environment.getExternalStorageDirectory();
        }
        else {
            storageDir = Environment.getRootDirectory();
        }
        return new File(storageDir, ".giraf/snd");
    }

    /**
     * Function for figuring out if the device have external storage
     * @return true if the device have external storage, false otherwise
     */
    private boolean hasExternalStorage() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }
}
