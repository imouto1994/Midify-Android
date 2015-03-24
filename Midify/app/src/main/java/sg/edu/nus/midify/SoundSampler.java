package sg.edu.nus.midify;

import android.media.AudioRecord;
import android.util.Log;


public class SoundSampler {

    private static final int  FS = 16000;     // sampling frequency
    public  AudioRecord       audioRecord;
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    public Thread            recordingThread;
    public static volatile Boolean drawFlag = true;

    public SoundSampler(MainActivity mAct) throws Exception
    {
        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));
        }
        catch (Exception e) {
            Log.d("Error in SoundSampler", e.getMessage());
            throw new Exception();
        }

        return;

    }

    public void stopRecording()
    {
        audioRecord.stop();
        audioRecord.release();
    }

    public void init() throws Exception
    {
        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));
        }
        catch (Exception e) {
            Log.d("Error in Init() ", e.getMessage());
            throw new Exception();
        }

        drawFlag = Boolean.valueOf(true);

        MainActivity.bufferSize = AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding);
        MainActivity.buffer = new short[MainActivity.bufferSize];

        audioRecord.startRecording();

        recordingThread = new Thread()
        {
            public void run()
            {
                while (true)
                {
                    if (!drawFlag)
                        return;

                    audioRecord.read(MainActivity.buffer, 0, MainActivity.bufferSize);

//                    if ((Short)(MainActivity.buffer)[0] != 0)
//                    {
//                        MainActivity.surfaceView.drawThread.setBuffer(MainActivity.buffer);
//                    }

                }
            }
        };
        recordingThread.start();

        return;

    }


}