package rs.veselinromic.eref.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                this.eboardNewsItems = Wrapper.getEboardNews();
            }
            catch (IOException e)
            {
                Log.e("GetEboardNews", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (this.eboardNewsItems != null)
            {
                EboardNewsAdapter eboardNewsAdapter = new EboardNewsAdapter(getActivity(), this.eboardNewsItems);
                eboardNewsListView.setAdapter(eboardNewsAdapter);
            }
        }
    }

    private OnFragmentInteractionListener mListener;

    ListView eboardNewsListView;

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
