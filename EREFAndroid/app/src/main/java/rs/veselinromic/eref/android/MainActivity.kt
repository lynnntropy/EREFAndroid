package rs.veselinromic.eref.android

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import java.io.IOException

import rs.veselinromic.eref.android.fragment.AboutFragment
import rs.veselinromic.eref.android.fragment.EboardNewsFragment
import rs.veselinromic.eref.android.fragment.KliseiFragment
import rs.veselinromic.eref.android.fragment.NewsFragment
import rs.veselinromic.eref.android.fragment.RefreshableFragment
import rs.veselinromic.eref.android.fragment.ResultsFragment
import rs.veselinromic.eref.android.fragment.ScheduleFragment
import rs.veselinromic.eref.android.fragment.SubjectsFragment
import rs.veselinromic.eref.android.fragment.UserProfileFragment
import rs.veselinromic.eref.wrapper.SessionManager
import rs.veselinromic.eref.wrapper.Wrapper
import rs.veselinromic.eref.wrapper.model.UserProfile

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    internal var currentFragment: Fragment? = null

    internal var selectedFragmentNavigationId = R.id.nav_vtsvesti

    internal inner class GetUserProfileTask : AsyncTask<Void, Void, Void>()
    {
        var userProfile: UserProfile? = null

        override fun doInBackground(vararg params: Void?): Void?
        {
            try
            {
                this.userProfile = Wrapper.getUserProfile()
            }
            catch (e: Exception)
            {
                Log.e("GetUserProfileTask", "e", e)
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?)
        {
            if (userProfile != null)
            {
                try
                {
                    (findViewById(R.id.name) as TextView).text = String.format("%s %s", userProfile!!.userData[1].value, userProfile!!.userData[3].value)

                    (findViewById(R.id.nameSubtitle) as TextView).text = "Indeks: " + userProfile!!.userData[0].value
                }
                catch (e: NullPointerException)
                {
                    Log.w("GetUserProfileTask", "GetUserProfileTask encountered a NullPointerException when trying to set the text.")
                }
            }
        }
    }

    internal inner class CheckCookieTask : AsyncTask<Void, Void, Void>()
    {
        var validCookie = false

        override fun doInBackground(vararg params: Void?): Void?
        {
            try
            {
                validCookie = SessionManager.isAuthenticated()
            }
            catch (e: IOException)
            {
                Log.e("e", "e", e)
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?)
        {
            if (!validCookie)
            {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer!!.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView?
        navigationView!!.setNavigationItemSelectedListener(this)

        GetUserProfileTask().execute()

        if (savedInstanceState != null)
        {
            selectFragmentFromItemId(savedInstanceState.getInt("selectedFragmentId"))
        }
        else
        {
            currentFragment = NewsFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, currentFragment)
            transaction.commit()
            title = "VTŠ Vesti"
        }

        // if (!BackgroundService.isRunning) startService(new Intent(this, BackgroundService.class));
    }

    override fun onResume()
    {
        super.onResume()
        if (connectedToNetwork()) CheckCookieTask().execute()
    }

    private fun connectedToNetwork(): Boolean
    {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    override fun onBackPressed()
    {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        if (drawer!!.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        selectFragmentFromItemId(item.itemId)
        return true
    }

    internal fun selectFragmentFromItemId(id: Int)
    {
        selectedFragmentNavigationId = id

        val transaction = supportFragmentManager.beginTransaction()

        when (id)
        {
            R.id.nav_vtsvesti ->
            {
                currentFragment = NewsFragment()
                title = "VTŠ Vesti"
            }
            R.id.nav_profil ->
            {
                currentFragment = UserProfileFragment()
                title = "Profil"
            }
            R.id.nav_predmeti ->
            {
                currentFragment = SubjectsFragment()
                title = "Predmeti"
            }
            R.id.nav_schedule ->
            {
                currentFragment = ScheduleFragment()
                title = "Raspored"
            }


            R.id.nav_etablavesti ->
            {
                currentFragment = EboardNewsFragment()
                title = "E-tabla - Vesti"
            }
            R.id.nav_klisei ->
            {
                currentFragment = KliseiFragment()
                title = "E-tabla - Klišei"
            }
            R.id.nav_rezultati ->
            {
                currentFragment = ResultsFragment()
                title = "E-tabla - Rezultati"
            }

            R.id.nav_about ->
            {
                currentFragment = AboutFragment()
                title = "O aplikaciji"
            }

            R.id.nav_logout ->
            {
                val loginPreferences = getSharedPreferences("loginDetails", 0)
                val editor = loginPreferences.edit()
                editor.remove("username")
                editor.remove("password")
                editor.commit()

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        transaction.replace(R.id.fragmentContainer, currentFragment).commit()

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        drawer!!.closeDrawer(GravityCompat.START)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE ->
            {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted
                }
                else
                {
                    // permission denied
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId

        when (id)
        {
            R.id.action_refresh -> if (currentFragment is RefreshableFragment) (currentFragment as RefreshableFragment).refresh()
        }

        return true
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putInt("selectedFragmentId", selectedFragmentNavigationId)
        super.onSaveInstanceState(outState)
    }

    companion object
    {
        val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100
    }
}
