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
        String photoFile = "tmpTest/test.jpg";

        // imgPath = Environment.getExternalStorageDirectory().getPath() + File.separator + photoFile;
        imgPath = context.getCacheDir().getPath() + "/img/"; // + "finalImgName.jpg";

        sndPath = context.getCacheDir().getPath()+ "/snd/";
        // String finalFilePath = getDir().getPath() + File.separator + savedFileName;


    }
    public void saveFinalFiles(String textLabel){
        storagePictogram.setTextLabel(textLabel);

        String image = finalImgPath + textLabel + ".jpg";
        String sound = finalSndPath + textLabel + ".3gp";

        int imgLength, sndLength;

        File tmpImgFile = new File(imgPath);
        File[] tmpImgArray = tmpImgFile.listFiles();

        if((imgLength = tmpImgArray.length) > 0){
            tmpImgFile = tmpImgArray[imgLength - 1];
        }

        File tmpSndFile = new File(sndPath);
        File[] tmpSndArray = tmpSndFile.listFiles();

        if((sndLength = tmpSndArray.length) > 0){
            tmpSndFile = tmpSndArray[sndLength - 1];
        }

        File imageFile = new File(image);
        File soundFile = new File(sound);

        if(tmpImgFile.exists() && imageFile.mkdirs()){
            copyFile(tmpImgFile, imageFile);
            storagePictogram.setImagePath(imageFile.getPath());
        }
        else {
            storagePictogram.setImagePath("");
        }
        if(tmpSndFile.exists() && soundFile.mkdirs()){
            copyFile(tmpSndFile, soundFile);
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
