package sg.edu.nus.midify.record;

import sg.edu.nus.helper.Constant;

/**
 * Created by Youn on 31/3/15.
 */
public class PcmToWavConverter {
    private final String srcfilename;
    private final String destfilename;
    //Default Settings
    private int samplerate = Constant.AUDIO_SAMPLE_RATE_CONFIGURATION;
    private int channels = Constant.AUDIO_NUMBER_OF_CHANNELS;
    private int bitspersample = Constant.BITS_PER_SAMPLE_CONFIGURATION;

    /**
     * Constructor
     * @return  A converter object
     * @param   srcfilename A source PCM file
     * @param   destfilename A destination WAV file.
     */
    public PcmToWavConverter(String srcfilename, String destfilename) {
        this.srcfilename = srcfilename;
        this.destfilename = destfilename;
    }

    /**
     * Sets bits per sample
     * Some devices don't support stereo recording
     * @param   bitspersample bits per sample for audio conversion (ie 8,16 bits)
     */
    public void setBitPerSample(int bitspersample) {
        this.bitspersample = bitspersample;
    }

    /**
     * Sets number of channels for PCM conversion
     * Some devices don't support stereo recording
     * @param   channels sample rate for audio conversion (ie 1,2)
     */
    public void setChannels(int channels) {
        this.channels = channels;
    }

    /**
     * Sets samplerate for PCM conversion
     * Some devices don't support all frequencies!
     * @param   samplerate sample rate for audio conversion (ie 8000,11025,22050,44100 Hz)
     */
    public void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    /**
     * JNI interface for converting PCM file to a WAV file
     */
    protected native String pcm2wav();

    /**
     * Converting the PCM file to a WAV file
     */
    public void convertPcm2wav() {
        this.pcm2wav();
    }
}
