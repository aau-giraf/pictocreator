// package dk.aau.cs.giraf.audiorecorder;

// import java.io.File;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.text.SimpleDateFormat;
// import java.util.Date;

// // import javax.sound.sampled.AudioFileFormat;
// // import javax.sound.sampled.AudioSystem;
// // import javax.sound.sampled.AudioInputStream;

// import android.os.Environment;
// import android.util.Log;

// public class WavHandler {

//     private static final String TAG = "WavHandler";

//     String wavFilePath = null;

//     File wavFile;

//     public WavHandler(){
//         createWavFilePath();
//     }

//     public String getWavFilePath() throws FileNotFoundException {
//             if(wavFilePath == null){
//                 Log.e(TAG, "File path was null");
//                 throw new FileNotFoundException();
//             }
//             else{
//                 return wavFilePath;
//             }
//     }

//     public void saveWavData(byte[] wavData) throws FileNotFoundException {
//             if(wavFilePath == null){
//                 Log.e(TAG, "File path was null");
//                 throw new FileNotFoundException();
//             }
//             else{
//                 try {
//                     wavFile = new File(wavFilePath);
//                     FileOutputStream outputStream = new FileOutputStream(wavFile);
//                     outputStream.write(wavData);
//                     outputStream.close();
//                 }
//                 catch(IOException e) {
//                     Log.e(TAG, "Error in saving wav data to file");
//                 }
//             }

//     }

//     public void deleteFile(){

//     }

//     private void createWavFilePath(){
//         File soundFileDir = getDir();

//         if(!soundFileDir.exists() && !soundFileDir.mkdirs()) {
//             Log.d(TAG, "Cannot create directory for the sound");
//             return;
//         }

//         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
//         String date = dateFormat.format(new Date());
//         String audioFile = "GSound_" + date + ".wav";

//         String fileName = soundFileDir.getPath() + File.separator + audioFile;

//         wavFilePath = fileName;
//     }

//     private File getDir() {
//         File storageDir;
//         if(hasExternalStorage()) {
//             storageDir = Environment.getExternalStorageDirectory();
//         }
//         else {
//             storageDir = Environment.getRootDirectory();
//         }
//         return new File(storageDir, ".giraf/snd");
//     }

//     private boolean hasExternalStorage() {
//         return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
//     }


// }
