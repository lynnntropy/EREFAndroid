package rs.veselinromic.eref.android.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTextView;
import rs.veselinromic.eref.android.R;

public class AboutFragment extends Fragment
{
    HtmlTextView htmlTextView;
    TextView version;

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

        try
        {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = pInfo.versionName;

            this.version = (TextView) rootView.findViewById(R.id.version);
            this.version.setText("Verzija " + versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.e("Version", "e", e);
        }

        return rootView;
    }
}
