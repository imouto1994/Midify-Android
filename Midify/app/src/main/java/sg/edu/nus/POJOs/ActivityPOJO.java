package sg.edu.nus.POJOs;

public class ActivityPOJO {

    private static final int JOIN_ACTIVITY_TYPE = 0;
    private static final int CREATE_ACTIVITY_TYPE = 1;
    private static final int FORK_ACTIVITY_TYPE = 2;
    private static final int PUBLIC_ACTIVITY_TYPE = 3;
    private static final int FOLLOW_ACTIVITY_TYPE = 4;

    private int activityType;
    private UserPOJO giver;
    private UserPOJO receiver;
    private MidiPOJO midiFile;

    public int getActivityType() {
        return this.activityType;
    }

    public UserPOJO getGiver() {
        return this.giver;
    }

    public UserPOJO getReceiver() {
        return this.receiver;
    }

    public MidiPOJO getMidiFile() {
        return this.midiFile;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public void setGiver(UserPOJO user) {
        this.giver = user;
    }

    public void setReceiver(UserPOJO user) {
        this.receiver = user;
    }

    public void setMidiFile(MidiPOJO midi) {
        this.midiFile = midi;
    }


}
