package dk.aau.cs.giraf.pictocreator.management;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import dk.aau.cs.giraf.pictocreator.StoragePictogram;
import dk.aau.cs.giraf.pictocreator.audiorecorder.AudioHandler;

/**
 * Class used for handling of files
 * @author Croc
 */
public class FileHandler{
    private final String TAG = "FileHandler";

    private StoragePictogram storagePictogram;

    private Activity activity;

    /**
     * Constructor for the class
     * @param activity The activity which was used to call the FileHandler
     * @param storagePictogram The StoragePictogram to use for storage
     */
    public FileHandler(Activity activity, StoragePictogram storagePictogram){
        this.activity = activity;
        this.storagePictogram = storagePictogram;
    }

    /**
     * Function for saving the image- and audio-files (if they exist)
     * on the external storage of the device
     * @param textLabel The textLabel/name for the Pictogram to store
     */
    public void saveFinalFiles(String textLabel, String inlineText, Bitmap bitmap){
        storagePictogram.setTextLabel(textLabel);
        storagePictogram.setinlineTextLabel(inlineText);

        //instantiates the files with their specific paths
        File image =  new File(Environment.getExternalStorageDirectory(), ".giraf/img/" + textLabel + "-" + System.currentTimeMillis() + ".jpg");
        File sound =  new File(Environment.getExternalStorageDirectory(), ".giraf/snd/" + textLabel + "-" + System.currentTimeMillis() + ".3gp");

        image.getParentFile().mkdirs();
        sound.getParentFile().mkdir();

        File tmpImgFile = new File(activity.getCacheDir(), "cvs");
        tmpImgFile.delete();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        try{
            FileOutputStream fos = new FileOutputStream(tmpImgFile);
            fos.write(bitmapdata);
        }
        catch (FileNotFoundException e){
            Log.e(TAG, e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage());
        }

        File tmpSndFile = null;
        if(AudioHandler.getFinalPath() != null){
            tmpSndFile = new File(AudioHandler.getFinalPath());
        }

        if(tmpImgFile.exists()){
            copyFile(tmpImgFile, image);
            storagePictogram.setImagePath(image.getPath());
        } else {
            storagePictogram.setImagePath("");
        }

        storagePictogram.setAudioFile(tmpSndFile);
    }

    /**
     * Method for copying file.
     * @param from The file to copy form
     * @param to The File to Copy to
     */
    private void copyFile(File from, File to){
        FileInputStream fromFileStream = null;
        FileOutputStream toFileStream = null;

        Log.d(TAG, "Copying files from: " + from.getAbsolutePath() + " to: " + to.getAbsolutePath());

        try {
            fromFileStream = new FileInputStream(from);
            toFileStream = new FileOutputStream(to);

            byte[] buffer = new byte[1024];

            int length;

            while((length = fromFileStream.read(buffer)) > 0){
                toFileStream.write(buffer, 0, length);
            }
        }
        catch(IOException e){
            Log.e(TAG, "File could not be copied " + e.getMessage());
        }
        finally {
            try {
                if(fromFileStream != null && toFileStream != null){
                    fromFileStream.close();
                    toFileStream.close();
                }
            }
            catch(IOException e){
                Log.e(TAG, "File streams could not be closed " + e.getMessage());
            }
        }
    }
}
