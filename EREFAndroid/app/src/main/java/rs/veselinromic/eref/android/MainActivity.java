package rs.veselinromic.eref.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import rs.veselinromic.eref.android.fragment.AboutFragment;
import rs.veselinromic.eref.android.fragment.EboardNewsFragment;
import rs.veselinromic.eref.android.fragment.KliseiFragment;
import rs.veselinromic.eref.android.fragment.NewsFragment;
import rs.veselinromic.eref.android.fragment.RefreshableFragment;
import rs.veselinromic.eref.android.fragment.ResultsFragment;
import rs.veselinromic.eref.android.fragment.SubjectsFragment;
import rs.veselinromic.eref.android.fragment.UserProfileFragment;
import rs.veselinromic.eref.wrapper.SessionManager;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.UserProfile;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewsFragment.OnFragmentInteractionListener,
        EboardNewsFragment.OnFragmentInteractionListener,
        ResultsFragment.OnFragmentInteractionListener

{

    public final static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    Fragment currentFragment;

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    class GetUserProfileTask extends AsyncTask<Void, Void, Void>
    {
        UserProfile userProfile;

        protected Void doInBackground(Void... params)
        {
            try
            {
                this.userProfile = Wrapper.getUserProfile();
            }
            catch (IOException e)
            {
                Log.e("GetUserProfileTask", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (userProfile != null)
            {
                ((TextView) findViewById(R.id.name)).setText(
                        String.format("%s %s", userProfile.userData.get(1).value, userProfile.userData.get(3).value));

                ((TextView) findViewById(R.id.nameSubtitle)).setText("Indeks: " + userProfile.userData.get(0).value);
            }
        }
    }

    class CheckCookieTask extends AsyncTask<Void, Void, Void>
    {
        boolean validCookie = false;

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                validCookie = SessionManager.isAuthenticated();
            }
            catch (IOException e)
            {
                Log.e("e", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (!validCookie)
            {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    RelativeLayout fragmentContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new GetUserProfileTask().execute();

        currentFragment = new NewsFragment();
        this.fragmentContainerLayout = (RelativeLayout) findViewById(R.id.fragmentContainer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, currentFragment);
        transaction.commit();

        setTitle("VTŠ Vesti");

        if (!BackgroundService.isRunning) startService(new Intent(this, BackgroundService.class));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (connectedToNetwork()) new CheckCookieTask().execute();
    }

    private boolean connectedToNetwork()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // setTitle(item.getTitle());

        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch(id)
        {
            case R.id.nav_vtsvesti:
                currentFragment = new NewsFragment();
//                transaction.replace(R.id.fragmentContainer, currentFragment).commit();
                setTitle("VTŠ Vesti");
                break;
            case R.id.nav_profil:
                currentFragment = new UserProfileFragment();
//                transaction.replace(R.id.fragmentContainer, new UserProfileFragment()).commit();
                setTitle("Profil");
                break;
            case R.id.nav_predmeti:
                currentFragment = new SubjectsFragment();
//                transaction.replace(R.id.fragmentContainer, new SubjectsFragment()).commit();
                setTitle("Predmeti");
                break;
            case R.id.nav_etablavesti:
                currentFragment = new EboardNewsFragment();
//                transaction.replace(R.id.fragmentContainer, new EboardNewsFragment()).commit();
                setTitle("E-tabla - Vesti");
                break;
            case R.id.nav_klisei:
                currentFragment = new KliseiFragment();
//                transaction.replace(R.id.fragmentContainer, new KliseiFragment()).commit();
                setTitle("E-tabla - Klišei");
                break;
            case R.id.nav_rezultati:
                currentFragment = new ResultsFragment();
//                transaction.replace(R.id.fragmentContainer, new ResultsFragment()).commit();
                setTitle("E-tabla - Rezultati");
                break;

            case R.id.nav_about:
                currentFragment = new AboutFragment();
//                transaction.replace(R.id.fragmentContainer, new AboutFragment()).commit();
                setTitle("O aplikaciji");
                break;

            case R.id.nav_logout:
                SharedPreferences loginPreferences = getSharedPreferences("loginDetails", 0);
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.remove("username");
                editor.remove("password");
                editor.commit();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }

        transaction.replace(R.id.fragmentContainer, currentFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted
                }
                else
                {
                    // permission denied
                }

                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_refresh:
                if (currentFragment instanceof RefreshableFragment) ((RefreshableFragment) currentFragment).refresh();
                break;
        }

        return true;
    }
}
