package rs.veselinromic.eref.android.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.android.adapter.EboardResultsAdapter
import rs.veselinromic.eref.wrapper.Wrapper
import rs.veselinromic.eref.wrapper.model.EboardResultsItem

import kotlinx.android.synthetic.main.fragment_results.*

class ResultsFragment : Fragment(), RefreshableFragment
{
    internal var isRefreshing = false

    override fun refresh()
    {
        if (!isRefreshing) GetResultsTask().execute()
    }

    internal inner class GetResultsTask : AsyncTask<Void, Void, Void>()
    {
        var results: List<EboardResultsItem>? = null
        var loadingAnimation = AnimatorSet()

        override fun doInBackground(vararg params: Void): Void?
        {
            isRefreshing = true

            try
            {
                activity.runOnUiThread {
                    val listFade = ObjectAnimator.ofFloat(listView, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    loadingAnimation.play(listFade).before(progressIndicatorFade)
                    loadingAnimation.start()
                }

                this.results = Wrapper.getEboardResults()
            }
            catch (e: Exception)
            {
                Log.e("GetResults", "e", e)
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?)
        {
            isRefreshing = false

            try
            {
                if (loadingAnimation.isRunning) loadingAnimation.cancel()
                loadingAnimation = AnimatorSet()
                val listFade = ObjectAnimator.ofFloat(listView, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                loadingAnimation.play(listFade).after(progressIndicatorFade)
                loadingAnimation.start()

                if (this.results != null)
                {
                    val eboardResultsAdapter = EboardResultsAdapter(activity, this.results!!)
                    listView.adapter = eboardResultsAdapter
                }

                swipeRefreshLayout.isRefreshing = false
            }
            catch (e: Exception)
            {
                Log.e("Error", "e", e)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val rootView = inflater!!.inflate(R.layout.fragment_results, container, false)

        val swipeRefreshLayout = rootView?.findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { GetResultsTask().execute() }

        GetResultsTask().execute()
        return rootView
    }
}
