package sg.edu.nus.midify.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sg.edu.nus.helper.Constant;

/**
 * TASK FOR RECORDING AUDIO IN PCM FORMAT
 */
public class Recorder implements Runnable {

    // Indicator whether the recording process is paused
    private volatile boolean isPaused;
    // Indicator whether the recording process is recording
    private volatile boolean isRecording;
    // Recording file name
    private File fileName;
    // Mutex objects for threads sharing
    private final Object mutex = new Object();
    // Changing the sample resolution changes sample type. byte vs. short.
    // Default settings
    private int audioEncoding = Constant.AUDIO_ENCODING_CONFIGURATION;
    private int frequency = Constant.AUDIO_SAMPLE_RATE_CONFIGURATION;
    private int channelConfiguration = Constant.AUDIO_CHANNEL_CONFIGURATION; // CHANNEL_CONFIGURATION_MONO

    /**
     * Set Audio Encoding
     * @param audioEncoding (ie ENCODING_PCM_8BIT,ENCODING_PCM_16BIT)
     */
    public void setAudioEncoding(int audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    /**
     * Constructor
     * @param audioEncoding (ie ENCODING_PCM_8BIT,ENCODING_PCM_16BIT)
     * @param frequency (ie 8000,11025,22050,44100 Hz)
     * @param channelConfiguration MONO, STEREO (ie CHANNEL_CONFIGURATION_MONO,CHANNEL_CONFIGURATION_STEREO)
     */
    public Recorder(int audioEncoding, int frequency, int channelConfiguration) {
        super();
        this.setFrequency(frequency);
        this.setChannelConfiguration(channelConfiguration);
        this.setAudioEncoding(audioEncoding);
        this.setPaused(false);
    }

    /**
     * Start Recording from microphone in high priority
     */
    public void run() {
        // Wait until we’re recording…
        synchronized (mutex) {
            while (!this.isRecording) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Wait() interrupted!", e);
                }
            }
        }

        // Open output stream
        if (this.fileName == null) {
            throw new IllegalStateException("fileName is            null");
        }
        BufferedOutputStream bufferedStreamInstance = null;
        if (fileName.exists()) {
            fileName.delete();
        }
        try {
            fileName.getParentFile().mkdirs();
            fileName.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot create file: " + fileName.toString());
        }
        try {
            bufferedStreamInstance = new BufferedOutputStream(
                    new FileOutputStream(this.fileName));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(
                    "Cannot Open File", e);

        }
        DataOutputStream dataOutputStreamInstance =
                new DataOutputStream(bufferedStreamInstance);

        // Set thread priority
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        // Allocate Recorder and Start Recording
        int bufferRead = 0;
        int bufferSize = AudioRecord.getMinBufferSize(this.getFrequency(),
                this.getChannelConfiguration(), this.getAudioEncoding());
        AudioRecord recordInstance = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT, this.getFrequency(), this.getChannelConfiguration(), this.getAudioEncoding(),
                bufferSize);
        short[] tempBuffer = new short[bufferSize];
        recordInstance.startRecording();

        while (this.isRecording) {
            // Check if the process is paused
            synchronized (mutex) {
                if (this.isPaused) {
                    try {
                        mutex.wait(250);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException("Wait() interrupted!", e);
                    }


                    continue;
                }
            }

            bufferRead = recordInstance.read(tempBuffer, 0, bufferSize);
            if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                throw new IllegalStateException("                read() returned AudioRecord.ERROR_INVALID_OPERATION");
            } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                throw new IllegalStateException(
                        "read() returned AudioRecord.ERROR_BAD_VALUE");
            }

            try {
                for (int idxBuffer = 0; idxBuffer < bufferRead; ++idxBuffer) {
                    dataOutputStreamInstance.writeShort(tempBuffer[idxBuffer]);
                }
            } catch (IOException e) {
                throw new IllegalStateException("dataOutputStreamInstance.writeShort(curVal)");
            }

        }
        recordInstance.stop();

        // Release resources…
        recordInstance.release();
        try {
            bufferedStreamInstance.close();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Cannot close buffered writer.");
        }
    }

    /**
     * Constructor
     * @param fileName File path to a PCM filename to be produced
     */
    public void setFileName(File fileName) {
        this.fileName = fileName;
    }

    /**
     * Constructor
     * @return Current file path to the PCM filename to be produced
     */
    public File getFileName() {
        return fileName;
    }

    /**
     * @param isRecording if currently recording
     */
    public void setRecording(boolean isRecording) {
        synchronized (mutex) {
            this.isRecording = isRecording;
            if (this.isRecording) {
                mutex.notify();
            }
        }
    }

    /**
     * @return true if currently recording
     */
    public boolean isRecording() {
        synchronized (mutex) {
            return isRecording;
        }
    }

    /**
     * @param frequency Set frequency to record with (ie 8000,11025,22050,44100 Hz)
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * @return current frequency to record with (ie 8000,11025,22050,44100 Hz)
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param channelConfiguration (ie CHANNEL_CONFIGURATION_MONO,CHANNEL_CONFIGURATION_STEREO)
     */
    public void setChannelConfiguration(int channelConfiguration) {
        this.channelConfiguration = channelConfiguration;
    }

    /**
     * @return Current channel configuration (ie CHANNEL_CONFIGURATION_MONO,CHANNEL_CONFIGURATION_STEREO)
     * @see AudioFormat
     */
    public int getChannelConfiguration() {
        return channelConfiguration;
    }

    /**
     * @return audioEncoding (ie ENCODING_PCM_8BIT,ENCODING_PCM_16BIT)
     */
    public int getAudioEncoding() {
        return audioEncoding;
    }

    /**
     * @param isPaused
     *            the isPaused to set
     */
    public void setPaused(boolean isPaused) {
        synchronized (mutex) {
            this.isPaused = isPaused;
        }
    }

    /**
     * @return true if Recording is paused
     */
    public boolean isPaused() {
        synchronized (mutex) {
            return isPaused;
        }
    }

    /**
     * @return Stop Recording
     */
    public void stop() {
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
