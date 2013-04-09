package dk.aau.cs.giraf.audiorecorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * This is the thread to run in the background and capture the audio
 */

public class MicrophoneThread implements Runnable {
    int sampleRate = 8000;
    int audioSource = MediaRecorder.AudioSource.MIC;
    final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private final MicrophoneThreadListener _listener;
    private Thread _microThread;
    private boolean _isRunning;

    private int totalSamples = 0;

    AudioHandler audioHandler = new AudioHandler();
    AudioRecord recorder;

    private static final String TAG = "Microthread";

    public MicrophoneThread(MicrophoneThreadListener listener){
        _listener = listener;
    }

    public void setSampleRate(int newSampleRate){
        sampleRate = newSampleRate;
    }

    public void setAudioSource(int newAudioSource){
        audioSource = newAudioSource;
    }

    public void start(){
        if(!_isRunning){
            _isRunning = true;
            _microThread = new Thread(this);
            _microThread.start();
        }
    }

    public void stop(){
        try {
            if(_isRunning){
                _isRunning = false;
                _microThread.join();
            }
        }
        catch (InterruptedException interrupt){
            Log.v(TAG, "Interrupted", interrupt);
        }
    }

    // Now it becomes interesting :P
    @Override
    public void run(){
        // Buffer for 20 milliseconds of data
        short[] buffer20ms = new short[sampleRate / 50];
        // Buffer size for the AudioRecorder buffer.
        int buffer1000msSize = getBufferSize(sampleRate, channelConfig, audioFormat);

        try {
            recorder = new AudioRecord(audioSource,
                                       sampleRate,
                                       channelConfig,
                                       audioFormat,
                                       buffer1000msSize);
            recorder.startRecording();

            while(_isRunning){
                int samples = recorder.read(buffer20ms, 0, buffer20ms.length);
                totalSamples += samples;
                _listener.processAudioFrame(buffer20ms);
            }
            recorder.stop();
            audioHandler.saveAudio(buffer20ms);
        }
        catch(Throwable x) {
            Log.v(TAG, "Audio reading error.", x);
        }
        finally {
        }
    }


    private int getBufferSize(int sampleRateInHz, int mChannelConfig, int mAudioFormat){
        int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, mChannelConfig, mAudioFormat);

        if (bufferSize < sampleRateInHz){
            bufferSize = sampleRateInHz;
        }

        return bufferSize;
    }

}
