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
import java.util.List;

import rs.veselinromic.eref.android.MainActivity;
import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.adapter.SubjectsAdapter;
import rs.veselinromic.eref.android.adapter.UserProfileListAdapter;
import rs.veselinromic.eref.wrapper.SubjectsManager;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.Subject;
import rs.veselinromic.eref.wrapper.model.UserProfile;

public class SubjectsFragment extends Fragment
{
    class GetSubjectsTask extends AsyncTask<Void, Void, Void>
    {
        List<Subject> subjectList;

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                this.subjectList = SubjectsManager.getSubjects();
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
            swipeRefreshLayout.setRefreshing(false);

            if (subjectList != null)
            {
                if (subjectsAdapter == null)
                {
                    subjectsAdapter = new SubjectsAdapter(getActivity(), subjectList);
                    listView.setAdapter(subjectsAdapter);
                }
                else
                {
                    subjectsAdapter.clear();
                    subjectsAdapter.addAll(subjectList);
                    subjectsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    SubjectsAdapter subjectsAdapter;

    public SubjectsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_subjects, container, false);

        // this.generalCreditView = (TextView) rootView.findViewById(R.id.generalCreditView);
        // this.tuitionCreditView = (TextView) rootView.findViewById(R.id.tuitionCreditView);
        // this.listView = (ListView) rootView.findViewById(R.id.listView);

        this.listView = (ListView) rootView.findViewById(R.id.listView);
        this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new GetSubjectsTask().execute();
            }
        });

        new GetSubjectsTask().execute();

        return rootView;
    }

}
