package sg.edu.nus.midify.record;

public class Note {
    public int note;
    public int vel;
    public float time; //millisecs

    public Note(int note, int vel, float time) {
        this.note = note;
        this.vel = vel;
        this.time = time;

    }
}
