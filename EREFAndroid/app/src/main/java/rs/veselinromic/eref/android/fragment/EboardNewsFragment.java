package rs.veselinromic.eref.android.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.io.IOException;
import java.util.List;

import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.adapter.EboardNewsAdapter;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.EboardNewsItem;

public class EboardNewsFragment extends Fragment
{
    class GetEboardNewsTask extends AsyncTask<Void, Void, Void>
    {
        List<EboardNewsItem> eboardNewsItems;
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
                        ValueAnimator listFade = ObjectAnimator.ofFloat(eboardNewsListView, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                        loadingAnimation.play(listFade).before(progressIndicatorFade);
                        loadingAnimation.start();
                    }
                });

                this.eboardNewsItems = Wrapper.getEboardNews();
            }
            catch (Exception e)
            {
                Log.e("GetEboardNews", "e", e);
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
                ValueAnimator listFade = ObjectAnimator.ofFloat(eboardNewsListView, "alpha", 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                loadingAnimation.play(listFade).after(progressIndicatorFade);
                loadingAnimation.start();

                if (this.eboardNewsItems != null)
                {
                    EboardNewsAdapter eboardNewsAdapter = new EboardNewsAdapter(getActivity(), this.eboardNewsItems);
                    eboardNewsListView.setAdapter(eboardNewsAdapter);
                }

                swipeRefreshLayout.setRefreshing(false);
            }
            catch (Exception e)
            {
                Log.e("Error", "e", e);
            }
        }
    }

    private OnFragmentInteractionListener mListener;

    ListView eboardNewsListView;
    SwipeRefreshLayout swipeRefreshLayout;
    SpinKitView progressIndicator;

    public EboardNewsFragment()
    {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EboardNewsFragment newInstance(String param1, String param2)
    {
        EboardNewsFragment fragment = new EboardNewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_eboard_news, container, false);

        this.eboardNewsListView = (ListView) rootView.findViewById(R.id.eboardNewsListView);
        this.swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new GetEboardNewsTask().execute();
            }
        });
        this.progressIndicator = (SpinKitView) rootView.findViewById(R.id.progressIndicator);

        new GetEboardNewsTask().execute();

        return rootView;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
