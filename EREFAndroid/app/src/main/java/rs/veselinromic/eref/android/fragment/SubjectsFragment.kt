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
import rs.veselinromic.eref.android.adapter.SubjectsAdapter
import rs.veselinromic.eref.wrapper.SubjectsManager
import rs.veselinromic.eref.wrapper.model.Subject

import kotlinx.android.synthetic.main.fragment_subjects.*

class SubjectsFragment : Fragment(), RefreshableFragment
{
    internal var isRefreshing = false

    override fun refresh()
    {
        if (!isRefreshing) GetSubjectsTask().execute()
    }

    internal inner class GetSubjectsTask : AsyncTask<Void, Void, Void>()
    {
        var subjectList: List<Subject>? = null
        var loadingAnimation = AnimatorSet()

        override fun doInBackground(vararg params: Void): Void?
        {
            isRefreshing = true

            try
            {
                activity.runOnUiThread {
                    val listFade = ObjectAnimator.ofFloat(listView, "alpha", 1f, 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f, 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    loadingAnimation.play(listFade).before(progressIndicatorFade)
                    loadingAnimation.start()
                }

                this.subjectList = SubjectsManager.getSubjects()
            }
            catch (e: Exception)
            {
                Log.e("GetUserProfile", "e", e)
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
                val listFade = ObjectAnimator.ofFloat(listView, "alpha", 0f, 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f, 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                loadingAnimation.play(progressIndicatorFade).before(listFade)
                loadingAnimation.start()

                if (subjectList != null)
                {
                    if (subjectsAdapter == null)
                    {
                        subjectsAdapter = SubjectsAdapter(activity, subjectList as MutableList<Subject>)
                        listView.adapter = subjectsAdapter
                    }
                    else
                    {
                        subjectsAdapter!!.clear()
                        subjectsAdapter!!.addAll(subjectList!!)
                        subjectsAdapter!!.notifyDataSetChanged()
                    }
                }
            }
            catch (e: Exception)
            {
                Log.e("Error", "e", e)
            }
        }
    }

    internal var subjectsAdapter: SubjectsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val rootView = inflater!!.inflate(R.layout.fragment_subjects, container, false)

        val swipeRefreshLayout = rootView?.findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { GetSubjectsTask().execute() }

        GetSubjectsTask().execute()
        return rootView
    }
}
