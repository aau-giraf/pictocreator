package dk.aau.cs.giraf.audiorecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class AudioHandler {

    private static final String TAG = "AudioHandler";

    // private WavHandler wavHandler;

    // private AmrHandler amrHandler;

    // private short[] wavShortData;

    // byte[] wavByteData;

    // int wavDataSize = 0;

    // private static String wavFilePath;

    String amrFilePath;

    public AudioHandler(){
        // wavHandler = new WavHandler();
        createAmrFilePath();

    }

    public String getFilePath(){
        return amrFilePath;
    }

    private void createAmrFilePath(){
        File soundFileDir = getDir();

        if(!soundFileDir.exists() && !soundFileDir.mkdirs()) {
            Log.d(TAG, "Cannot create directory for the sound");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String date = dateFormat.format(new Date());
        String audioFile = "GSound_" + date + ".3gp";

        String fileName = soundFileDir.getPath() + File.separator + audioFile;

        amrFilePath = fileName;
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
}

    // public void saveAudioData(short[] shortData, int arraySize){
    //     wavDataSize = arraySize;

    //     wavShortData = new short[wavDataSize];

    //     for(int i = 0; i < wavDataSize; i++){
    //         wavShortData[i] = shortData[i];
    //     }

    //     wavByteData = shortToByte(wavShortData);

    //     try{
    //         wavHandler.saveWavData(wavByteData);
    //     }
    //     catch (FileNotFoundException e){
    //         Log.e(TAG, "Wav data could not be saved");
    //     }
    // }

    // public void stopHandler(){
    //     try {
    //         wavFilePath = wavHandler.getWavFilePath();
    //     }
    //     catch (FileNotFoundException e) {
    //         Log.e(TAG, "wav file path not found");
    //     }

    //     amrHandler = new AmrHandler(wavFilePath);

    //     wavHandler.deleteFile();
    // }

    // private byte[] shortToByte(short[] input){
    //     int index;
    //     int iterations = input.length;

    //     ByteBuffer buffer = ByteBuffer.allocate(iterations *2);

    //     for(index = 0; index != iterations; index++){
    //         buffer.putShort(input[index]);
    //     }

    //     return buffer.array();
    // }
