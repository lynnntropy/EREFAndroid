package rs.veselinromic.eref.android.fragment;


import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.io.IOException;
import java.util.List;

import rs.veselinromic.eref.android.MainActivity;
import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.adapter.EboardExamplesAdapter;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.EboardExampleItem;

public class KliseiFragment extends Fragment implements RefreshableFragment
{
    boolean isRefreshing = false;

    @Override
    public void refresh()
    {
        if (!isRefreshing) new GetExamplesTask().execute();
    }

    class GetExamplesTask extends AsyncTask<Void, Void, Void>
    {
        List<EboardExampleItem> exampleItemList;
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
                        ValueAnimator listFade = ObjectAnimator.ofFloat(list, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        loadingAnimation.play(listFade).before(progressIndicatorFade);
                        loadingAnimation.start();
                    }
                });

                this.exampleItemList = Wrapper.getEboardExamples();
            }
            catch (Exception e)
            {
                Log.e("GetExamples", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            isRefreshing = false;

            try
            {
                if (loadingAnimation.isRunning()) loadingAnimation.cancel();
                loadingAnimation = new AnimatorSet();
                ValueAnimator listFade = ObjectAnimator.ofFloat(list, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                loadingAnimation.play(listFade).after(progressIndicatorFade);
                loadingAnimation.start();

                if (this.exampleItemList != null)
                {
                    EboardExamplesAdapter eboardExamplesAdapter = new EboardExamplesAdapter(getActivity(), this.exampleItemList);
                    list.setAdapter(eboardExamplesAdapter);
                }

                swipeRefreshLayout.setRefreshing(false);
            }
            catch (Exception e)
            {
                Log.e("Error", "e", e);
            }
        }
    }

    ListView list;
    SwipeRefreshLayout swipeRefreshLayout;
    SpinKitView progressIndicator;

    public KliseiFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_klisei, container, false);

        this.list = (ListView) rootView.findViewById(R.id.list);
        this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new GetExamplesTask().execute();
            }
        });
        this.progressIndicator = (SpinKitView) rootView.findViewById(R.id.progressIndicator);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        new GetExamplesTask().execute();
        return rootView;
    }

}
