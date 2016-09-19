package rs.veselinromic.eref.android.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.adapter.UserProfileListAdapter;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.UserProfile;

public class UserProfileFragment extends Fragment
{
    class GetUserProfileTask extends AsyncTask<Void, Void, Void>
    {
        UserProfile userProfile;

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                userProfile = Wrapper.getUserProfile();
            }
            catch (IOException e)
            {
                Log.e("GetUserProfile", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (userProfile != null)
            {
                generalCreditView.setText(userProfile.generalCredit);
                tuitionCreditView.setText(userProfile.tuitionCredit);

                UserProfileListAdapter userProfileListAdapter = new UserProfileListAdapter(getActivity(), userProfile.userData);
                listView.setAdapter(userProfileListAdapter);
            }

//            swipeRefreshLayout.setRefreshing(false);
        }
    }


    TextView generalCreditView;
    TextView tuitionCreditView;

    ListView listView;
//    SwipeRefreshLayout swipeRefreshLayout;

    public UserProfileFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        this.generalCreditView = (TextView) rootView.findViewById(R.id.generalCreditView);
        this.tuitionCreditView = (TextView) rootView.findViewById(R.id.tuitionCreditView);
        this.listView = (ListView) rootView.findViewById(R.id.listView);

//        this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
//        {
//            @Override
//            public void onRefresh()
//            {
//                new GetUserProfileTask().execute();
//            }
//        });

        new GetUserProfileTask().execute();

        return rootView;
    }

}
