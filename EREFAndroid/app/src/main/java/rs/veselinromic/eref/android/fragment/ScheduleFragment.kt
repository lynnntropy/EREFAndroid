package rs.veselinromic.eref.android.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rs.veselinromic.eref.android.R

import kotlinx.android.synthetic.main.fragment_schedule.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onUiThread
import rs.veselinromic.eref.android.adapter.ScheduleAdapter
import rs.veselinromic.eref.wrapper.ScheduleManager

class ScheduleFragment : Fragment(), RefreshableFragment
{
    internal var isRefreshing = false

    override fun refresh()
    {
        if (!isRefreshing) getSchedule()
    }

    fun getSchedule()
    {
        isRefreshing = true

        doAsync {

            var loadingAnimation = AnimatorSet()

            onUiThread {
                val listFade = ObjectAnimator.ofFloat(list, "alpha", 1f, 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f, 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                loadingAnimation.play(listFade).before(progressIndicatorFade)
                loadingAnimation.start()
            }

            val schedule = ScheduleManager.getSchedule()

            onUiThread {

                swipeRefreshLayout.isRefreshing = false

                if (loadingAnimation.isRunning) loadingAnimation.cancel()
                loadingAnimation = AnimatorSet()
                val listFade = ObjectAnimator.ofFloat(list, "alpha", 0f, 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f, 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                loadingAnimation.play(progressIndicatorFade).before(listFade)
                loadingAnimation.start()

                list.adapter = ScheduleAdapter(activity, schedule)
            }

            isRefreshing = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        var rootView = inflater?.inflate(R.layout.fragment_schedule, container, false)

        getSchedule()

        val swipeRefreshLayout = rootView?.findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { if(!isRefreshing) getSchedule() }

        return rootView
    }
}