package rs.veselinromic.eref.android.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.io.IOException;
import java.sql.Ref;

import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.adapter.UserProfileListAdapter;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.UserProfile;

public class UserProfileFragment extends Fragment implements RefreshableFragment
{
    @Override
    public void refresh()
    {
        new GetUserProfileTask().execute();
    }

    class GetUserProfileTask extends AsyncTask<Void, Void, Void>
    {
        UserProfile userProfile;
        AnimatorSet loadingAnimation;

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        loadingAnimation = new AnimatorSet();
                        ValueAnimator listFade = ObjectAnimator.ofFloat(listView, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        ValueAnimator tableFade = ObjectAnimator.ofFloat(tableLayout, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        loadingAnimation.play(listFade).with(tableFade).before(progressIndicatorFade);
                        loadingAnimation.start();
                    }
                });

                userProfile = Wrapper.getUserProfile();
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
            try
            {
                if (loadingAnimation.isRunning()) loadingAnimation.cancel();
                loadingAnimation = new AnimatorSet();
                ValueAnimator listFade = ObjectAnimator.ofFloat(listView, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                ValueAnimator tableFade = ObjectAnimator.ofFloat(tableLayout, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                loadingAnimation.play(listFade).with(tableFade).after(progressIndicatorFade);
                loadingAnimation.start();

                if (userProfile != null)
                {
                    generalCreditView.setText(userProfile.generalCredit);
                    tuitionCreditView.setText(userProfile.tuitionCredit);

                    UserProfileListAdapter userProfileListAdapter = new UserProfileListAdapter(getActivity(), userProfile.userData);
                    listView.setAdapter(userProfileListAdapter);
                }
            }
            catch (Exception e)
            {
                Log.e("Error", "e", e);
            }
        }
    }


    TableLayout tableLayout;
    TextView generalCreditView;
    TextView tuitionCreditView;

    ListView listView;
    SpinKitView progressIndicator;



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
        this.progressIndicator = (SpinKitView) rootView.findViewById(R.id.progressIndicator);
        this.tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);

        new GetUserProfileTask().execute();

        return rootView;
    }

}
