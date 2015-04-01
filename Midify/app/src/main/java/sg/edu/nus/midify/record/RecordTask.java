package sg.edu.nus.midify.record;

import android.media.AudioFormat;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import sg.edu.nus.helper.Constant;

public class RecordTask extends AsyncTask<Void, Void, Void> {

    private RecordTaskDelegate delegate;

    public RecordTask (RecordTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Recorder recorderInstance = new Recorder(AudioFormat.ENCODING_PCM_16BIT, 11025, 1);
        this.delegate.setRecorder(recorderInstance);

        Thread th = new Thread(recorderInstance);
        recorderInstance.setFileName(new File(Constant.DEFAULT_PCM_FILE_NAME));
        th.start();
        recorderInstance.setRecording(true);

        while (recorderInstance.isRecording()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void test) {
        Toast.makeText(this.delegate.getContext(), "Finish recording", Toast.LENGTH_SHORT).show();
        this.delegate.convertPCMToMidi();
    }
}
