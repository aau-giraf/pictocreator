package dk.aau.cs.giraf.pictocreator.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Environment;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;

import dk.aau.cs.giraf.pictocreator.StoragePictogram;

/**
 * Class used for handling of files
 *
 * @author Croc
 *
 */
public class FileHandler{
    private static final String TAG = "FileHandler";

    private String imgPath, sndPath;

    private static String finalImgName;

    private static String finalSndName;

    private StoragePictogram storagePictogram;

    private Activity activity;

    /**
     * Constructor for the class
     * @param activity The activity which was used to call the FileHandler
     * @param storagePictogram The StoragePictogram to use for storage
     */
    public FileHandler(Activity activity, StoragePictogram storagePictogram){
        this.activity = activity;n
        this.storagePictogram = storagePictogram;
    }

    /**
     * Function for saving the image- and audio-files (if they exists)
     * on the devices external storage
     * @param textLabel The textLabel/name for the Pictogram to store
     */
    public void saveFinalFiles(String textLabel){
        storagePictogram.setTextLabel(textLabel);

        File image =  new File(Environment.getExternalStorageDirectory(), ".giraf/img/" + textLabel + ".jpg");

        File sound =  new File(Environment.getExternalStorageDirectory(), ".giraf/snd/" + textLabel + ".3gp");

        int imgLength, sndLength;
        Intent girafIntent = activity.getIntent();
        long author = girafIntent.getLongExtra("currentGuardianID", 0);


        image.getParentFile().mkdirs();
        sound.getParentFile().mkdir();

        File tmpImgFile = new File(activity.getCacheDir(), "cvs");
        tmpImgFile.mkdirs();
        File[] tmpImgArray = tmpImgFile.listFiles();

        if(tmpImgArray.length > 0){
            imgLength = tmpImgArray.length;
            tmpImgFile = tmpImgArray[imgLength - 1];
        }

        File tmpSndFile = new File(activity.getCacheDir(), "snd");
        tmpSndFile.mkdirs();
        File[] tmpSndArray = tmpSndFile.listFiles();

        if(tmpSndArray.length > 0){
            sndLength = tmpSndArray.length;
            tmpSndFile = tmpSndArray[sndLength - 1];

        }

        if(tmpImgFile.exists()){
            copyFile(tmpImgFile, image);
            storagePictogram.setImagePath(image.getPath());
        } else {
            storagePictogram.setImagePath("");
            Log.d(TAG, "Images path set to null");
        }

        if(tmpSndFile.exists()){
            copyFile(tmpSndFile, sound);
            storagePictogram.setAudioPath(sound.getPath());
        } else {
            storagePictogram.setAudioPath("");
            Log.d(TAG, "Sound path set to null");
        }
    }

    /**
     * Method for copying file.
     * @param from The file to copy form
     * @param to The File to Copy to
     */
    private void copyFile(File from, File to){
        FileInputStream fromFileStream = null;
        FileOutputStream toFileStream = null;

        Log.d(TAG, "From: " + from.getAbsolutePath() + "\n"
              + "To: " + to.getAbsolutePath());

        try {
            fromFileStream = new FileInputStream(from);
            toFileStream = new FileOutputStream(to);

            byte[] buffer = new byte[1024];

            int length = fromFileStream.read(buffer);

            while(length > 0){
                toFileStream.write(buffer, 0, length);
                length = fromFileStream.read(buffer);
            }
        }
        catch(IOException e){
            Log.d(TAG, "File could not be copied" + e.getMessage());
        }
        finally {
            try {
                if(fromFileStream != null && toFileStream != null){
                    fromFileStream.close();
                    toFileStream.close();
                }
            }
            catch(IOException e){
                Log.d(TAG, "File streams could not be closed");
            }
        }
    }
}
