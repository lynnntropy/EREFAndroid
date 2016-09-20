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
import java.util.concurrent.RunnableFuture;

import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.adapter.NewsAdapter;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.NewsItem;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment
{
    class GetNewsTask extends AsyncTask<Void, Void, Void>
    {
        List<NewsItem> newsItemList;

        @Override
        protected Void doInBackground(Void... params)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    AnimatorSet loadingAnimation = new AnimatorSet();
                    ValueAnimator listFade = ObjectAnimator.ofFloat(newsListView, "alpha", 1f, 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                    ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f, 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
                    loadingAnimation.play(listFade).before(progressIndicatorFade);
                    loadingAnimation.start();
                }
            });

            try
            {
                this.newsItemList = Wrapper.getNews();
            }
            catch (IOException e)
            {
                Log.e("GetNewsTask", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            swipeRefreshLayout.setRefreshing(false);

            AnimatorSet loadingAnimation = new AnimatorSet();
            ValueAnimator listFade = ObjectAnimator.ofFloat(newsListView, "alpha", 0f, 1f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
            ValueAnimator progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f, 0f).setDuration(getResources().getInteger(R.integer.loading_fade_duration));
            loadingAnimation.play(progressIndicatorFade).before(listFade);
            loadingAnimation.start();

            if (this.newsItemList != null)
            {
                NewsAdapter newsAdapter = new NewsAdapter(getActivity(), this.newsItemList);
                newsListView.setAdapter(newsAdapter);
            }
        }
    }

    private OnFragmentInteractionListener mListener;

    ListView newsListView;
    SpinKitView progressIndicator;
    SwipeRefreshLayout swipeRefreshLayout;

    public NewsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2)
    {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

//    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        this.newsListView = (ListView) rootView.findViewById(R.id.newsListView);
//        this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
//        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
//        {
//            @Override
//            public void onRefresh()
//            {
//                new GetNewsTask().execute();
//            }
//        });

        this.progressIndicator = (SpinKitView) rootView.findViewById(R.id.progressIndicator);

        this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new GetNewsTask().execute();
            }
        });

        new GetNewsTask().execute();

//        return inflater.inflate(R.layout.fragment_news, container, false);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
