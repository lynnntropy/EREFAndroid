package rs.veselinromic.eref.android.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import rs.veselinromic.eref.android.R

import kotlinx.android.synthetic.main.fragment_about.*

import org.sufficientlysecure.htmltextview.HtmlTextView

class AboutFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val rootView = inflater!!.inflate(R.layout.fragment_about, container, false)

        val htmlTextView = rootView.findViewById(R.id.htmlTextView) as HtmlTextView
        htmlTextView.setHtmlFromRawResource(activity, R.raw.licenses, false)

        val versionName = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName

        val version = rootView.findViewById(R.id.version) as TextView
        version.text = "Verzija " + versionName

        return rootView
    }
}