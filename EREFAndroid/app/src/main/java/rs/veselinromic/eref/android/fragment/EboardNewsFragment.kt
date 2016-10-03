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
import rs.veselinromic.eref.android.adapter.EboardNewsAdapter
import rs.veselinromic.eref.wrapper.Wrapper
import rs.veselinromic.eref.wrapper.model.EboardNewsItem

import kotlinx.android.synthetic.main.fragment_eboard_news.*

class EboardNewsFragment : Fragment(), RefreshableFragment
{
    internal var isRefreshing = false

    override fun refresh()
    {
        if (!isRefreshing) GetEboardNewsTask().execute()
    }

    internal inner class GetEboardNewsTask : AsyncTask<Void, Void, Void>()
    {
        var eboardNewsItems: List<EboardNewsItem>? = null
        var loadingAnimation = AnimatorSet()

        override fun doInBackground(vararg params: Void?): Void?
        {
            isRefreshing = true

            try
            {
                activity.runOnUiThread {
                    val listFade = ObjectAnimator.ofFloat(eboardNewsListView, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    loadingAnimation.play(listFade).before(progressIndicatorFade)
                    loadingAnimation.start()
                }

                this.eboardNewsItems = Wrapper.getEboardNews()
            }
            catch (e: Exception)
            {
                Log.e("GetEboardNews", "e", e)
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
                val listFade = ObjectAnimator.ofFloat(eboardNewsListView, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                loadingAnimation.play(listFade).after(progressIndicatorFade)
                loadingAnimation.start()

                if (this.eboardNewsItems != null)
                {
                    val eboardNewsAdapter = EboardNewsAdapter(activity, this.eboardNewsItems!!)
                    eboardNewsListView.adapter = eboardNewsAdapter
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
        val rootView = inflater!!.inflate(R.layout.fragment_eboard_news, container, false)

        val swipeRefreshLayout = rootView?.findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { GetEboardNewsTask().execute() }

        GetEboardNewsTask().execute()
        return rootView
    }
}
