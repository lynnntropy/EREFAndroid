package rs.veselinromic.eref.android.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.android.adapter.UserProfileListAdapter
import rs.veselinromic.eref.wrapper.Wrapper
import rs.veselinromic.eref.wrapper.model.UserProfile

import kotlinx.android.synthetic.main.fragment_user_profile.*

class UserProfileFragment : Fragment(), RefreshableFragment
{
    internal var isRefreshing = false

    override fun refresh()
    {
        if (!isRefreshing) GetUserProfileTask().execute()
    }

    internal inner class GetUserProfileTask : AsyncTask<Void, Void, Void>()
    {
        var userProfile: UserProfile? = null
        var loadingAnimation = AnimatorSet()

        override fun doInBackground(vararg params: Void): Void?
        {
            isRefreshing = true

            try
            {
                activity.runOnUiThread {
                    val listFade = ObjectAnimator.ofFloat(listView, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    val tableFade = ObjectAnimator.ofFloat(tableLayout, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                    loadingAnimation.play(listFade).with(tableFade).before(progressIndicatorFade)
                    loadingAnimation.start()
                }

                userProfile = Wrapper.getUserProfile()
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
                if (loadingAnimation.isRunning) loadingAnimation.cancel()
                loadingAnimation = AnimatorSet()
                val listFade = ObjectAnimator.ofFloat(listView, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val tableFade = ObjectAnimator.ofFloat(tableLayout, "alpha", 1f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                val progressIndicatorFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f).setDuration(resources.getInteger(R.integer.loading_fade_duration).toLong())
                loadingAnimation.play(listFade).with(tableFade).after(progressIndicatorFade)
                loadingAnimation.start()

                if (userProfile != null)
                {
                    generalCreditView.text = userProfile!!.generalCredit
                    tuitionCreditView.text = userProfile!!.tuitionCredit

                    val userProfileListAdapter = UserProfileListAdapter(activity, userProfile!!.userData)
                    listView.adapter = userProfileListAdapter
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
        val rootView = inflater!!.inflate(R.layout.fragment_user_profile, container, false)

        GetUserProfileTask().execute()
        return rootView
    }
}
