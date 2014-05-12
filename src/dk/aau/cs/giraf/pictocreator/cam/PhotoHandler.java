package dk.aau.cs.giraf.pictocreator.cam;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.widget.Toast;

import dk.aau.cs.giraf.gui.GToast;


/**
 * Class used for saving the image data when a picture is taken.
 * @author Vogella
 */
public class PhotoHandler implements PictureCallback {

    private final String TAG = "PhotoHandler";

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

        File pictureFileDir = getPictureDir();

        if(!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d(TAG, "Cannot create directory for the image");
            return;
        }

        String photoFile = "camerapicture";

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
            GToast.makeText(context, "Billede Taget", Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            GToast.makeText(context, "Billede kunne ikke tages", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method for creating the directory to save the picture
     * @return a File object representing the save directory
     */
    private File getPictureDir() {
        File storageDir = context.getCacheDir();
        return new File(storageDir, "img");
    }
}
