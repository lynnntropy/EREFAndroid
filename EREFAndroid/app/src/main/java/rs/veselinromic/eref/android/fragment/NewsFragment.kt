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
import rs.veselinromic.eref.android.adapter.NewsAdapter
import rs.veselinromic.eref.wrapper.Wrapper
import rs.veselinromic.eref.wrapper.model.NewsItem

import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : Fragment(), RefreshableFragment
{
    internal var isRefreshing = false

    override fun refresh()
    {
        if (!isRefreshing) GetNewsTask().execute()
    }

    internal inner class GetNewsTask : AsyncTask<Void, Void, Void>()
    {
        var newsItemList: List<NewsItem>? = null
        var loadingAnimation = AnimatorSet()

        override fun doInBackground(vararg params: Void?): Void?
        {
            isRefreshing = true

            try
            {
                activity.runOnUiThread {
                    val listFade = ObjectAnimator.ofFloat(newsListView, "alpha", 1f, 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f, 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    loadingAnimation.play(listFade).before(progressIndicatorFade)
                    loadingAnimation.start()
                }

                this.newsItemList = Wrapper.getNews()
            }
            catch (e: Exception)
            {
                Log.e("GetNewsTask", "e", e)
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?)
        {
            isRefreshing = false

            try
            {
                swipeRefreshLayout.isRefreshing = false

                if (loadingAnimation.isRunning) loadingAnimation.cancel()
                loadingAnimation = AnimatorSet()
                val listFade = ObjectAnimator.ofFloat(newsListView, "alpha", 0f, 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f, 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                loadingAnimation.play(progressIndicatorFade).before(listFade)
                loadingAnimation.start()

                if (this.newsItemList != null)
                {
                    val newsAdapter = NewsAdapter(activity, this.newsItemList!!)
                    newsListView.adapter = newsAdapter
                }
            }
            catch (e: Exception)
            {
                Log.e("Error", "e", e)
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val rootView = inflater!!.inflate(R.layout.fragment_news, container, false)

        val swipeRefreshLayout = rootView?.findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { if (!isRefreshing) GetNewsTask().execute() }

        GetNewsTask().execute()

        return rootView
    }
}
