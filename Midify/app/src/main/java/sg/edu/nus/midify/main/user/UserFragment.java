package sg.edu.nus.midify.main.user;

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

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sg.edu.nus.POJOs.UserPOJO;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.http.MidifyRestClient;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.helper.recyclerview.DividerItemDecoration;
import sg.edu.nus.helper.recyclerview.SectionedListAdapter;
import sg.edu.nus.helper.recyclerview.SectionedListAdapter.Section;
import sg.edu.nus.midify.R;

public class UserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView userList;
    private UserListAdapter listAdapter;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

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
        userList = (RecyclerView) view.findViewById(R.id.users_list);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userList.setHasFixedSize(true);
        // Configure layout manager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        userList.setLayoutManager(llm);
        // Configure item decoration
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST);
        userList.addItemDecoration(itemDecoration);
        // Configure item animation
        userList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialize original adapter
        listAdapter = new UserListAdapter(this.getActivity());

        // Initialize the list of sections
        List<SectionedListAdapter.Section> sections =new ArrayList<>();
        sections.add(new SectionedListAdapter.Section(0,"Me"));
        sections.add(new SectionedListAdapter.Section(1,"Friends"));
        // Initialize the section adapter container
        Section[] dummy = new Section[sections.size()];
        SectionedListAdapter sectionedAdapter = new SectionedListAdapter(
                this.getActivity(), R.layout.item_section, R.id.section_text,listAdapter);
        sectionedAdapter.setSections(sections.toArray(dummy));

        userList.setAdapter(sectionedAdapter);
        userList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) userList.getLayoutManager();
                refreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

        if (PersistenceHelper.getFacebookToken(getActivity()) != null) {
            refreshList();
        }
    }

    public void refreshList() {
        if (ConnectionHelper.checkNetworkConnection()) {
            MidifyRestClient.instance().getFriends(new Callback<List<UserPOJO>>() {
                @Override
                public void success(List<UserPOJO> userPOJOs, Response response) {
                    listAdapter.refreshUserList(userPOJOs);
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(Constant.REQUEST_TAG, error.getMessage());
                    listAdapter.refreshUserList(new ArrayList<UserPOJO>());
                    refreshLayout.setRefreshing(false);
                }
            });
        } else {
            listAdapter.refreshUserList(new ArrayList<UserPOJO>());
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    public UserListAdapter getListAdapter() {
        return this.listAdapter;
    }
}
