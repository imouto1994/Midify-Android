package sg.edu.nus.midify.record;

public interface ConvertTaskDelegate {

    public void convertPcmToWav();

    public void convertWavToMidi();

    public void populateMidiNotes();

}
