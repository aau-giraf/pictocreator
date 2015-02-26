package dk.aau.cs.giraf.pictocreator.audiorecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

/**
 * This class handles the creation of the audio storage directory,
 * and the file for saving the recorder audiofile
 * @author Croc
 *
 */
public class AudioHandler {

    //Fields
    private static final String TAG = "AudioHandler";
    private String tempOutputFilePath = null;
    private static String savedFileName;
    private Context context;

    /**
     * Constructor for the class.
     * Calls function to create the output file.
     */
    public AudioHandler(Context context){
        this.context = context;
        createOutputFilePath();
    }

    /**
     * Function for getting the file path for the audiofile
     * @return A string representing the file path for the audiofile
     */
    public String getFilePath(){
        return tempOutputFilePath;
    }

    /**
     * Getter for savedFileName, which is the final sound path, static as it should be accessed without an instance of AudioHandler
     * @return String savedFileName
     */
    public static String getFinalPath(){
        return savedFileName;
    }

    /**
     * Setter for Final Path, static as it should be accessed without an instance of AudioHandler
     * @param path this it the path...
     */
    public static void setFinalPath(String path){
        savedFileName = path;
    }

    /**
     * Set the savedFileName to null
     */
    public static void resetSound(){
        if(savedFileName == null){
            return;
        }

        File tempFile = new File(savedFileName);
        if(tempFile.exists()){
            tempFile.delete();
        }

        savedFileName = null;
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
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
            String date = dateFormat.format(new Date());

            String tmpAudioFile = "GSound_" + date + "tmp" + ".3gp";
            String fileName = soundFileDir.getPath() + File.separator + tmpAudioFile;
            tempOutputFilePath = fileName;

            String audioFile = "GSound_" + date + ".3gp";
            if(savedFileName == "" || savedFileName == null){
                savedFileName = getDir().getPath() + File.separator + audioFile;
            }
        }
    }

    /**
     * Function for deleting the saved file,
     * This function is called by {@link RecordThread} in the cancel() function.
     * This function is also called by {@link #saveFinalFile} when file is copied
     */
    public void deleteTempFile(){
        File tmpFile = new File(tempOutputFilePath);

        if(tmpFile.exists()){
            tmpFile.delete();
        }
    }

    /**
     * Saves and creates the final audioFile
     * Function called by the {@link RecordThread} in onAccept().
     */
    public void saveFinalFile(){

        FileInputStream tmpFileStream = null;
        FileOutputStream finalFileStream = null;

        try {
            if(tempOutputFilePath != null && savedFileName != null){
                File tmpFile = new File(tempOutputFilePath);
                File finalFile = new File(savedFileName);
                if(finalFile.exists()){
                    finalFile.delete();
                    finalFile = new File(savedFileName);
                }

                tmpFileStream = new FileInputStream(tmpFile);
                finalFileStream = new FileOutputStream(finalFile);

                byte[] buffer = new byte[1024];

                int length;

                while((length = tmpFileStream.read(buffer)) > 0){
                    finalFileStream.write(buffer, 0, length);
                }
            }
        }
        catch(IOException e){
            Log.d(TAG, "File could not be copied", e.fillInStackTrace());
        }
        finally {
            try {
                if(tmpFileStream != null && savedFileName != null){
                    tmpFileStream.close();
                    finalFileStream.close();

                    deleteTempFile();
                }
            }
            catch(IOException e){
                Log.d(TAG, "File streams could not be closed", e.fillInStackTrace());
            }
        }

    }

    /**
     * Function for creating the directory for the output file
     * This function is called by {@link #createOutputFilePath()}.
     * @return File representing the output directory
     */
    private File getDir() {
        return new File(context.getCacheDir(), "snd");
    }

}
