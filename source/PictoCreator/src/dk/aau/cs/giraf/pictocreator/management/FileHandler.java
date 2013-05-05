package dk.aau.cs.giraf.pictocreator.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private String finalImgPath = Environment.getExternalStorageDirectory().getPath() + "/.giraf/img/";

    private String finalSndPath = Environment.getExternalStorageDirectory().getPath() + "/.giraf/snd/";

    private static String finalImgName;

    private static String finalSndName;

    private StoragePictogram storagePictogram;

    private Context context;

    public FileHandler(Context context, StoragePictogram storagePictogram){
        this.context = context;
        this.storagePictogram = storagePictogram;

        imgPath = context.getCacheDir().getPath() + "finalImgName.jpg";

        sndPath = context.getCacheDir().getPath() + "finalSndName.3pg";

    }
    public void saveFinalFiles(String textLabel){
        storagePictogram.setTextLabel(textLabel);

        String image = finalImgPath + textLabel + ".jpg";
        String sound = finalSndPath + textLabel + ".3gp";

        File imgFile = new File(imgPath);
        File sndFile = new File(sndPath);

        File imageFile = new File(image);
        File soundFile = new File(sound);

        if(imgFile.exists() && imageFile.mkdirs()){
            copyFile(imgFile, imageFile);
            storagePictogram.setImagePath(imageFile.getPath());
        }
        else {
            storagePictogram.setImagePath("");
        }
        if(sndFile.exists() && soundFile.mkdirs()){
            copyFile(sndFile, soundFile);
            storagePictogram.setAudioPath(soundFile.getPath());
        }
        else {
            storagePictogram.setAudioPath("");
        }
    }

    private void copyFile(File from, File to){
        FileInputStream fromFileStream = null;
        FileOutputStream toFileStream = null;

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
            Log.d(TAG, "File could not be copied");
        }
        finally {
            try {
                if(from.getPath() != null && to.getPath() != null){
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
