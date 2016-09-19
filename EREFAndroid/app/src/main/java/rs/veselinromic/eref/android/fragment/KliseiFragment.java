package rs.veselinromic.eref.android.fragment;


import android.Manifest;
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

import java.io.IOException;
import java.util.List;

import rs.veselinromic.eref.android.MainActivity;
import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.adapter.EboardExamplesAdapter;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.EboardExampleItem;

public class KliseiFragment extends Fragment
{
    class GetExamplesTask extends AsyncTask<Void, Void, Void>
    {
        List<EboardExampleItem> exampleItemList;

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                this.exampleItemList = Wrapper.getEboardExamples();
            }
            catch (IOException e)
            {
                Log.e("GetExamples", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (this.exampleItemList != null)
            {
                EboardExamplesAdapter eboardExamplesAdapter = new EboardExamplesAdapter(getActivity(), this.exampleItemList);
                list.setAdapter(eboardExamplesAdapter);
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }

    ListView list;
    SwipeRefreshLayout swipeRefreshLayout;

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
