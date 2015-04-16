package sg.edu.nus.midify.midi;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.POJOs.UserPOJO;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.http.MidifyRestClient;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.helper.recyclerview.DividerItemDecoration;
import sg.edu.nus.helper.recyclerview.SectionedListAdapter;
import sg.edu.nus.midify.R;
import sg.edu.nus.midify.main.user.UserListAdapter;

public class MidiActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, MidiListAdapter.MidiListDelegate {
    // Private Variables
    private boolean isLocalUser;
    private String userId;
    private List<MidiPOJO> localMidis;

    // UI Controls
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView midiList;
    private MidiListAdapter listAdapter;

    private MediaPlayer mediaPlayer;
    private String previousFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midi);

        // Retrieve intent params
        Intent intent = getIntent();
        String userId = intent.getStringExtra(Constant.INTENT_PARAM_USER_ID);
        if (userId == null) {
            throw new NullPointerException("The user id is null");
        }
        updateUserId(userId);

        // Initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Initialize refresh layout
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorScheme(android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        // Show indicator initially
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        // Initialize recycler view
        midiList = (RecyclerView) findViewById(R.id.midis_list);
        midiList.setHasFixedSize(true);
        // Configure layout manager
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        midiList.setLayoutManager(llm);
        // Configure item animation
        midiList.setItemAnimator(new DefaultItemAnimator());
        // Configure list adapter
        listAdapter = new MidiListAdapter(this);
        midiList.setAdapter(listAdapter);
        midiList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) midiList.getLayoutManager();
                refreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
        refreshList();
    }

    private void updateUserId(String userId) {
        this.userId = userId;
        String localUserId = PersistenceHelper.getFacebookUserId(this);
        isLocalUser = localUserId.equals(this.userId);
        if (isLocalUser) {
            localMidis = PersistenceHelper.getMidiList(this);
        }
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    private void refreshList() {
        if (ConnectionHelper.checkNetworkConnection()) {
            final Context context = this;
            MidifyRestClient.instance().getMidisForUser(userId, new Callback<List<MidiPOJO>>() {
                @Override
                public void success(List<MidiPOJO> midiPOJOs, Response response) {
                    if (isLocalUser) {
                        Map<String, MidiPOJO> midiMap = new HashMap<>();
                        for (MidiPOJO midi : midiPOJOs) {
                            midiMap.put(midi.getFileId(), midi);
                        }
                        for (MidiPOJO localMidi: localMidis) {
                            if (localMidi.getFileId() != null && midiMap.containsKey(localMidi.getFileId())) {
                                midiMap.get(localMidi.getFileId()).setLocalFilePath(localMidi.getLocalFilePath());
                            } else {
                                midiPOJOs.add(localMidi);
                            }
                        }
                    }
                    listAdapter.refreshMidiList(midiPOJOs);
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(Constant.REQUEST_TAG, error.getMessage());
                    if (isLocalUser) {
                        List<MidiPOJO> localMidiList = PersistenceHelper.getMidiList(context);
                        listAdapter.refreshMidiList(localMidiList);
                    } else {
                        listAdapter.refreshMidiList(new ArrayList<MidiPOJO>());
                    }
                }
            });
        } else {
            if (isLocalUser) {
                List<MidiPOJO> localMidiList = PersistenceHelper.getMidiList(this);
                listAdapter.refreshMidiList(localMidiList);
            } else {
                listAdapter.refreshMidiList(new ArrayList<MidiPOJO>());
            }
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void play(String filePath) {
        File midiFile = new File(filePath);
        if (!midiFile.exists()) {
            Log.e(Constant.MEDIA_TAG, "MIDI file cannot be found for playback");
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, Uri.fromFile(midiFile));
            previousFilePath = filePath;
            mediaPlayer.start();
        } else {
            if (previousFilePath.equals(filePath)) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            } else {
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(this, Uri.fromFile(midiFile));
                previousFilePath = filePath;
                mediaPlayer.start();
            }
        }
    }
}
