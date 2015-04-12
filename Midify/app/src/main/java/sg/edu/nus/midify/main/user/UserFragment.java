package sg.edu.nus.midify.main.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sg.edu.nus.POJOs.UserPOJO;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.MidifyRestClient;
import sg.edu.nus.helper.recyclerview.DividerItemDecoration;
import sg.edu.nus.midify.R;

/**
 * Created by Youn on 8/4/15.
 */
public class UserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView userList;

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Configure adapter
        UserListAdapter listAdapter = new UserListAdapter();
        userList.setAdapter(listAdapter);
        refreshList();
    }

    private void refreshList() {
        MidifyRestClient.instance().getFriends(new Callback<List<UserPOJO>>() {
            @Override
            public void success(List<UserPOJO> userPOJOs, Response response) {
                UserListAdapter adapter = (UserListAdapter) userList.getAdapter();
                adapter.refreshUserList(userPOJOs);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(Constant.REQUEST_TAG, error.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshList();
    }
}
