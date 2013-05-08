package dk.aau.cs.giraf.pictocreator.management;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import android.os.Environment;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import dk.aau.cs.giraf.pictocreator.StoragePictogram;

public class FileHandler {
    private static final String TAG = "FileHandler";

    private String imgPath, sndPath;

    // private File finalImgPath;

    // private File finalSndPath;

    private static String finalImgName;

    private static String finalSndName;

    private StoragePictogram storagePictogram;

    private Context context;

    public FileHandler(Context context, StoragePictogram storagePictogram){
        this.context = context;
        this.storagePictogram = storagePictogram;
    }

    public void saveFinalFiles(String textLabel){
        storagePictogram.setTextLabel(textLabel);

        File image =  new File(Environment.getExternalStorageDirectory(), ".giraf/img/" + textLabel + ".jpg");

        File sound =  new File(Environment.getExternalStorageDirectory(), ".giraf/snd/" + textLabel + ".3gp");

        int imgLength, sndLength;

        image.getParentFile().mkdirs();
        sound.getParentFile().mkdir();

        File tmpImgFile = new File(context.getCacheDir(), "img");
        tmpImgFile.mkdirs();
        File[] tmpImgArray = tmpImgFile.listFiles();

        if((imgLength = tmpImgArray.length) > 0){
            tmpImgFile = tmpImgArray[imgLength - 1];
        }

        File tmpSndFile = new File(context.getCacheDir(), "snd");
        tmpSndFile.mkdirs();
        File[] tmpSndArray = tmpSndFile.listFiles();

        if((sndLength = tmpSndArray.length) > 0){
            tmpSndFile = tmpSndArray[sndLength - 1];
        }

        // File imageFile = new File(image);
        // File soundFile = new File(sound);

        if(tmpImgFile.exists()){
            copyFile(tmpImgFile, image);
            storagePictogram.setImagePath(image.getPath());

        }
        else {
            storagePictogram.setImagePath("");
            Log.d(TAG, "Images path set to null");
        }
        if(tmpSndFile.exists()){
            copyFile(tmpSndFile, sound);
            storagePictogram.setAudioPath(sound.getPath());

        }
        else {
            storagePictogram.setAudioPath("");
            Log.d(TAG, "Sound path set to null");
        }
    }

    private void copyFile(File from, File to){
        FileInputStream fromFileStream = null;
        FileOutputStream toFileStream = null;

        Log.d(TAG, "From: " + from.getAbsolutePath() + "\n"
              + "To: " + to.getAbsolutePath());

        try {
            fromFileStream = new FileInputStream(from);
            toFileStream = new FileOutputStream(to);

            byte[] buffer = new byte[1024];

            int lenght;

            while((lenght = fromFileStream.read(buffer)) > 0){
                toFileStream.write(buffer, 0, lenght);
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
