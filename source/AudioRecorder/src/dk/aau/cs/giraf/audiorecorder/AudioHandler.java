package dk.aau.cs.giraf.audiorecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class AudioHandler {

    private static final String TAG = "AudioHandler";

    public AudioHandler(){
    }

    public void saveAudio(short[] shortData) {

        byte[] data = shortToByte(shortData);

        File soundFileDir = getDir();

        if(!soundFileDir.exists() && !soundFileDir.mkdirs()) {
            Log.d(TAG, "Cannot create directory for the image");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String date = dateFormat.format(new Date());
        String audioFile = "GSound_" + date + ".wav";

        String fileName = soundFileDir.getPath() + File.separator + audioFile;

        File soundFile = new File(fileName);

        try {
            FileOutputStream fos = new FileOutputStream(soundFile);
            fos.write(data);
            fos.close();
            // Toast.makeText(context, "Giraf sound: " + audioFile + "\n" +
            //                "Saved in: " + soundFileDir, Toast.LENGTH_LONG).show();
        }
        catch(Exception e) {
            Log.d(TAG, "Sound: " + audioFile + " was not saved" + e.getMessage());
            // Toast.makeText(context, "Sound could not be saved", Toast.LENGTH_LONG).show();
        }

    }

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

    private boolean hasExternalStorage() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    private byte[] shortToByte(short[] input){
        int index;
        int length = input.length;

        ByteBuffer bytes = ByteBuffer.allocate(length * 2);

        for(index = 0; index != length; index++){
            bytes.putShort(input[index]);
        }

        return bytes.array();
    }

}
