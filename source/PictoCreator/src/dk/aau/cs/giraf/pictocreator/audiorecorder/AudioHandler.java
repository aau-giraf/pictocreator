package dk.aau.cs.giraf.pictocreator.audiorecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private String savedFileName;

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
     * Function for creating the tmp output file, and the path to the
     * both the tmp and the tmp file
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

        String tmpAudioFile = "GSound_" + date + "tmp" + ".3gp";

        String fileName = soundFileDir.getPath() + File.separator + tmpAudioFile;

        outputFilePath = fileName;

        savedFileName = audioFile;
    }

    /**
     * Function for deleting the saved file,
     * This function is called by RecordThread in the cancel() function.
     */
    public void deleteFile(){
            File tmpFile = new File(outputFilePath);

            if(tmpFile.exists()){
                tmpFile.delete();
            }

    }

    /**
     * Saves and creates the final audioFile
     * Function called by the RecordThread in onAccept().
     */
    public void saveFinalFile(){
        String finalFilePath = getDir().getPath() + File.separator + savedFileName;

        FileInputStream tmpFileStream = null;
        FileOutputStream finalFileStream = null;

        try {
            File tmpFile = new File(outputFilePath);
            File finalFile = new File(finalFilePath);

            tmpFileStream = new FileInputStream(tmpFile);
            finalFileStream = new FileOutputStream(finalFile);

            byte[] buffer = new byte[1024];

            int lenght;

            while((lenght = tmpFileStream.read(buffer)) > 0){
                finalFileStream.write(buffer, 0, lenght);
            }
        }
        catch(IOException e){
            Log.d(TAG, "File could not be copied");
        }
        finally {
            try {
                tmpFileStream.close();
                finalFileStream.close();

                deleteFile();
            }
            catch(IOException e){
                Log.d(TAG, "File streams could not be closed");
            }
        }

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
        return new File(storageDir, ".giraf/croc/snd");
    }

    /**
     * Function for figuring out if the device have external storage
     * @return true if the device have external storage, false otherwise
     */
    private boolean hasExternalStorage() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }
}
