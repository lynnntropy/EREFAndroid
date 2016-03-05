package rs.veselinromic.eref.android;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import rs.veselinromic.eref.android.fragment.EboardNewsFragment;
import rs.veselinromic.eref.android.fragment.NewsFragment;
import rs.veselinromic.eref.wrapper.Wrapper;
import rs.veselinromic.eref.wrapper.model.UserProfile;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewsFragment.OnFragmentInteractionListener,
        EboardNewsFragment.OnFragmentInteractionListener

{
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

        this.fragmentContainerLayout = (RelativeLayout) findViewById(R.id.fragmentContainer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new NewsFragment());
        transaction.commit();

        setTitle("VTŠ Vesti");
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
        setTitle(item.getTitle());

        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch(id)
        {
            case R.id.nav_vtsvesti:
                transaction.replace(R.id.fragmentContainer, new NewsFragment()).commit();
                break;
            case R.id.nav_etablavesti:
                transaction.replace(R.id.fragmentContainer, new EboardNewsFragment()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
