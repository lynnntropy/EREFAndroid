package rs.veselinromic.eref.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sufficientlysecure.htmltextview.HtmlTextView;
import rs.veselinromic.eref.android.R;

public class AboutFragment extends Fragment
{
    HtmlTextView htmlTextView;

    public AboutFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        this.htmlTextView = (HtmlTextView) rootView.findViewById(R.id.htmlTextView);
        this.htmlTextView.setHtmlFromRawResource(getActivity(), R.raw.licenses, false);

        return rootView;
    }

}
