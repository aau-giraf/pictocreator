// package dk.aau.cs.giraf.audiorecorder;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.OutputStream;
// import java.text.SimpleDateFormat;
// import java.util.Date;

// // import javax.sound.sampled.AudioFileFormat;
// // import javax.sound.sampled.AudioSystem;
// // import javax.sound.sampled.AudioInputStream;

// import android.media.*;
// import android.os.Environment;
// import android.util.Log;

// public class AmrHandler {

//     private static final String TAG = "AmrHandler";

//     InputStream wavStream;

//     String amrFilePath;

//     AmrInputStream amrStream;

//     FileOutputStream outputStream;

//     public AmrHandler(String wavFilePath){

//         createAmrFilePath();

//         try {
//             wavStream = new FileInputStream(wavFilePath);
//         }
//         catch (FileNotFoundException e) {
//             Log.e(TAG, "wav path not found");
//         }

//         amrStream = new AmrInputStream(wavStream);

//         File file = new File(amrFilePath);

//         try {
//             file.createNewFile();
//         }
//         catch (IOException e) {
//             Log.e(TAG, "IO exception");
//         }

//         try {
//             outputStream = new FileOutputStream(file);
//         }
//         catch (FileNotFoundException e) {
//             Log.e(TAG, "File not found");
//         }

//         saveAmrData();

//     }

//     private void saveAmrData(){

//         try{
//             outputStream.write(0x23);
//             outputStream.write(0x21);
//             outputStream.write(0x41);
//             outputStream.write(0x4D);
//             outputStream.write(0x52);
//             outputStream.write(0x0A);
//         }
//         catch (IOException e){
//             Log.e(TAG, "Error writing tag to 6 first bytes");
//         }

//         byte[] x = new byte[1024];
//         int len;
//         try {
//             while ((len = amrStream.read(x)) > 0) {
//                 outputStream.write(x,0,len);
//             }
//         }
//         catch (IOException e){
//             Log.e(TAG, "Error in writting to file");
//         }

//         try{
//             outputStream.close();
//         }
//         catch (IOException e){
//             Log.e(TAG, "Error trying to close output stream");
//         }
//     }

//     private void createAmrFilePath(){
//         File soundFileDir = getDir();

//         if(!soundFileDir.exists() && !soundFileDir.mkdirs()) {
//             Log.d(TAG, "Cannot create directory for the sound");
//             return;
//         }

//         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
//         String date = dateFormat.format(new Date());
//         String audioFile = "GSound_" + date + ".3gp";

//         String fileName = soundFileDir.getPath() + File.separator + audioFile;

//         amrFilePath = fileName;
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
