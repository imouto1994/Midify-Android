package sg.edu.nus.midify.main.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sg.edu.nus.POJOs.ActivityPOJO;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.http.MidifyRestClient;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.helper.recyclerview.DividerItemDecoration;
import sg.edu.nus.helper.recyclerview.SectionDividerItemDecoration;
import sg.edu.nus.midify.R;


public class ActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView activityList;
    private ActivityListAdapter listAdapter;

    public static ActivityFragment newInstance() {
        return new ActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        // Initialize refresh layout
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorScheme(android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);
        if (ConnectionHelper.checkNetworkConnection()) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
        }



        // Initialize recycler view
        activityList = (RecyclerView) view.findViewById(R.id.activity_list);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activityList.setHasFixedSize(true);
        // Configure layout manager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        activityList.setLayoutManager(llm);
        // Configure item decoration
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this.getActivity(), SectionDividerItemDecoration.VERTICAL_LIST);
        activityList.addItemDecoration(itemDecoration);
        // Configure item animation
        activityList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialize original adapter
        listAdapter = new ActivityListAdapter(this.getActivity());
        activityList.setAdapter(listAdapter);
        activityList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) activityList.getLayoutManager();
                refreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

        if (PersistenceHelper.getFacebookToken(getActivity()) != null) {
            refreshList();
        }
    }

    public void refreshList() {
        if (ConnectionHelper.checkNetworkConnection()) {
            MidifyRestClient.instance().getActivities(new Callback<List<ActivityPOJO>>() {
                @Override
                public void success(List<ActivityPOJO> activityPOJOs, Response response) {
                    listAdapter.refreshActivityList(activityPOJOs);
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(Constant.REQUEST_TAG, error.getMessage());
                    listAdapter.refreshActivityList(new ArrayList<ActivityPOJO>());
                    refreshLayout.setRefreshing(false);
                }
            });
        } else {
            listAdapter.refreshActivityList(new ArrayList<ActivityPOJO>());
            refreshLayout.setRefreshing(false);
            Toast.makeText(this.getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    public ActivityListAdapter getListAdapter() {
        return this.listAdapter;
    }
}
