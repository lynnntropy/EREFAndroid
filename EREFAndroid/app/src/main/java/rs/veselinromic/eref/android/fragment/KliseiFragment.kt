package rs.veselinromic.eref.android.fragment


import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import rs.veselinromic.eref.android.MainActivity
import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.android.adapter.EboardExamplesAdapter
import rs.veselinromic.eref.wrapper.Wrapper
import rs.veselinromic.eref.wrapper.model.EboardExampleItem

import kotlinx.android.synthetic.main.fragment_klisei.*

class KliseiFragment : Fragment(), RefreshableFragment
{
    internal var isRefreshing = false

    override fun refresh()
    {
        if (!isRefreshing) GetExamplesTask().execute()
    }

    internal inner class GetExamplesTask : AsyncTask<Void, Void, Void>()
    {
        var exampleItemList: List<EboardExampleItem>? = null
        var loadingAnimation = AnimatorSet()

        override fun doInBackground(vararg params: Void?): Void?
        {
            isRefreshing = true

            try
            {
                activity.runOnUiThread {
                    val listFade = ObjectAnimator.ofFloat(list, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    loadingAnimation.play(listFade).before(progressIndicatorFade)
                    loadingAnimation.start()
                }

                this.exampleItemList = Wrapper.getEboardExamples()
            }
            catch (e: Exception)
            {
                Log.e("GetExamples", "e", e)
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
                val listFade = ObjectAnimator.ofFloat(list, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                loadingAnimation.play(listFade).after(progressIndicatorFade)
                loadingAnimation.start()

                if (this.exampleItemList != null)
                {
                    val eboardExamplesAdapter = EboardExamplesAdapter(activity, this.exampleItemList!!)
                    list.adapter = eboardExamplesAdapter
                }

                swipeRefreshLayout.isRefreshing = false
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
        val rootView = inflater!!.inflate(R.layout.fragment_klisei, container, false)

        val swipeRefreshLayout = rootView?.findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { GetExamplesTask().execute() }

        if (ContextCompat.checkSelfPermission(activity,
                                              Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,
                                              arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                              MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        }

        GetExamplesTask().execute()
        return rootView
    }
}
