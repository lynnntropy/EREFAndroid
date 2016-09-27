package rs.veselinromic.eref.android.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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

import com.github.ybq.android.spinkit.SpinKitView;

import java.io.IOException;
import java.sql.Ref;
import java.util.List;

import rs.veselinromic.eref.android.MainActivity;
import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.adapter.SubjectsAdapter;
import rs.veselinromic.eref.android.adapter.UserProfileListAdapter;
import rs.veselinromic.eref.wrapper.SubjectsManager;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.Subject;
import rs.veselinromic.eref.wrapper.model.UserProfile;

public class SubjectsFragment extends Fragment implements RefreshableFragment
{
    boolean isRefreshing = false;

    @Override
    public void refresh()
    {
        if (!isRefreshing) new GetSubjectsTask().execute();
    }

    class GetSubjectsTask extends AsyncTask<Void, Void, Void>
    {
        List<Subject> subjectList;
        AnimatorSet loadingAnimation;

        @Override
        protected Void doInBackground(Void... params)
        {
            isRefreshing = true;

            try
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        loadingAnimation = new AnimatorSet();
                        ValueAnimator listFade = ObjectAnimator.ofFloat(listView, "alpha", 1f, 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f, 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        loadingAnimation.play(listFade).before(progressIndicatorFade);
                        loadingAnimation.start();
                    }
                });

                this.subjectList = SubjectsManager.getSubjects();
            }
            catch (Exception e)
            {
                Log.e("GetUserProfile", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            isRefreshing = false;

            try
            {
                swipeRefreshLayout.setRefreshing(false);

                if (loadingAnimation.isRunning()) loadingAnimation.cancel();
                loadingAnimation = new AnimatorSet();
                ValueAnimator listFade = ObjectAnimator.ofFloat(listView, "alpha", 0f, 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f, 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                loadingAnimation.play(progressIndicatorFade).before(listFade);
                loadingAnimation.start();

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
            catch (Exception e)
            {
                Log.e("Error", "e", e);
            }
        }
    }

    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    SubjectsAdapter subjectsAdapter;
    SpinKitView progressIndicator;

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

        this.progressIndicator = (SpinKitView) rootView.findViewById(R.id.progressIndicator);

        new GetSubjectsTask().execute();

        return rootView;
    }

}
