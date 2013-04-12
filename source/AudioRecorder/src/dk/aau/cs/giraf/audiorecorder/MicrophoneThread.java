// package dk.aau.cs.giraf.audiorecorder;

// import android.media.AudioFormat;
// import android.media.AudioRecord;
// import android.media.MediaRecorder;
// import android.media.audiofx.NoiseSuppressor;
// import android.util.Log;

// /**
//  * This is the thread to run in the background and capture the audio
//  */

// public class MicrophoneThread implements Runnable {
//     int sampleRate = 8000;
//     int divider = 50;
//     int audioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;
//     final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
//     final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

//     private final MicrophoneThreadListener _listener;
//     private Thread _microThread;
//     private boolean _isRunning;

//     AudioHandler audioHandler = new AudioHandler();
//     AudioRecord recorder;
//     int recorderId;
//     NoiseSuppressor supressor;

//     private static final String TAG = "Microthread";

//     public MicrophoneThread(MicrophoneThreadListener listener){
//         _listener = listener;
//     }

//     public void setSampleRate(int newSampleRate){
//         sampleRate = newSampleRate;
//     }

//     public void setAudioSource(int newAudioSource){
//         audioSource = newAudioSource;
//     }

//     public void start(){
//         if(!_isRunning){
//             _isRunning = true;
//             _microThread = new Thread(this);
//             _microThread.start();
//         }
//     }

//     public void stop(){
//         try {
//             if(_isRunning){
//                 _isRunning = false;
//                 _microThread.join();
//                 // audioHandler.stopPlaying();
//             }
//         }
//         catch (InterruptedException interrupt){
//             Log.v(TAG, "Interrupted", interrupt);
//         }
//     }

//     // Now it becomes interesting :P
//     @Override
//     public void run(){
//         // Buffer for 20 milliseconds of data
//         short[] buffer20ms = new short[sampleRate / divider];

//         int buffer20msLength = buffer20ms.length;

//         short[] dataBuffer = new short[1000000];
//         // Buffer size for the AudioRecorder buffer.
//         int buffer1000msSize = getBufferSize(sampleRate, channelConfig, audioFormat);

//         boolean supression = supressor.isAvailable();

//         try {
//             recorder = new AudioRecord(audioSource,
//                                        sampleRate,
//                                        channelConfig,
//                                        audioFormat,
//                                        buffer1000msSize);
//             recorderId = recorder.getAudioSessionId();
//             supressor = supressor.create(recorderId);

//             Log.d(TAG, "supression: " + supression);
//             recorder.startRecording();

//             int i = 0;

//             while(_isRunning){
//                 int samples = recorder.read(buffer20ms, 0, buffer20msLength);
//                 _listener.processAudioFrame(buffer20ms);
//                 for(int j = 0; j < buffer20msLength; j++){
//                     dataBuffer[i] = buffer20ms[j];
//                     i++;
//                 }
//             }
//             recorder.stop();
//             audioHandler.saveAudioData(dataBuffer, i);
//         }
//         catch(Throwable x) {
//             Log.v(TAG, "Audio reading error.", x);
//         }
//         finally {
//         }
//     }


//     private int getBufferSize(int sampleRateInHz, int mChannelConfig, int mAudioFormat){
//         int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, mChannelConfig, mAudioFormat);

//         if (bufferSize < sampleRateInHz){
//             bufferSize = sampleRateInHz;
//         }

//         return bufferSize;
//     }

//     // private void setMediaRecorder(){
//     //     try {
//     //         mediaRecorder = new MediaRecorder();
//     //         mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//     //         mediaRecorder.setOutputFormat(this.outputFormat);
//     //         mediaRecorder.setAudioEncoder(this.audioEncoder);
//     //         this.outputFile = audioHandler.getOutputFile();
//     //         mediaRecorder.setOutputFile(this.outputFile);
//     //         mediaRecorder.prepare();
//     //     }
//     //     catch (IllegalStateException e){
//     //         Log.d(TAG, "Fucked the order of functions up");
//     //     }
//     //     catch (IOException e){
//     //         Log.d(TAG, "Something else fucked the Mediaplayer up");
//     //     }
//     // }

// }
