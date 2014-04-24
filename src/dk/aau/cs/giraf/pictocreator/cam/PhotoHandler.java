package dk.aau.cs.giraf.pictocreator.cam;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.widget.Toast;

//Vogella's photohandler

/**
 * Class used for saving the image data when a picture is taken
 *
 * @author Croc
 *
 */
public class PhotoHandler implements PictureCallback {
    private final static String TAG = "PhotoHandler";

    private String imagePath = null;
    private final Context context;
    public File pictureFile;

    public PhotoHandler(Context context) {
        this.context = context;
    }

    /**
     * Method called when a picture is taken
     * @param data The image data to save
     * @param camera The camera used to capture the picture
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFileDir = getDir();

        if(!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d(TAG, "Cannot create directory for the image");
            return;
        }

        String photoFile = "camerapicture.jpg";

        String fileName = pictureFileDir.getPath() + File.separator + photoFile;

        pictureFile = new File(fileName);
        if(pictureFile.exists())
        {
            pictureFile.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(context, "Billede Taget", Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Log.d(TAG, "Picture: " + photoFile + " was not saved" + e.getMessage());
            Toast.makeText(context, "Billede kunne ikke tages", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Method for creating the directory to save the picture
     * @return a File object representing the save directory
     */
    private File getDir() {

        File storageDir = context.getCacheDir();
        return new File(storageDir, "img");
    }

}
