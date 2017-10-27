package info.talkalert.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import info.talkalert.R;
import info.talkalert.data.sqllite.ExcludedPhoneNumbersPersistenceService;
import info.talkalert.fragments.AboutFragment;
import info.talkalert.fragments.EditSettingsFragment;
import info.talkalert.fragments.ExcludedPhoneNumberFragment;
import info.talkalert.fragments.StatusFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment excludedListFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        displaySelectedScreen(R.id.nav_status);
    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    private void applyExit() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(this,"Press Again to exit",Toast.LENGTH_LONG).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (fm.getFragments().size() <= 1) {
            applyExit();
        } else {
            for (Fragment frag : fm.getFragments()) {
                if (frag == null) {
                    applyExit();
                    return;
                }
                if (frag.isVisible()) {
                    FragmentManager childFm = frag.getChildFragmentManager();
                    if (childFm.getFragments() == null) {
                        super.onBackPressed();
                        return;
                    }
                    if (childFm.getBackStackEntryCount() > 0) {
                        childFm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return;
                    } else {
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id){

        Fragment fragment = null;

        boolean shouldAddToBackStack = false;

        switch (id){
            case R.id.nav_status:
                fragment = new StatusFragment();
                break;
            case R.id.nav_excluded_phones:
                fragment = new ExcludedPhoneNumberFragment();
                shouldAddToBackStack = true;
                excludedListFragment = fragment;
                break;
            case R.id.settings:
                fragment = new EditSettingsFragment();
                shouldAddToBackStack = true;
                break;
            case R.id.about:
                fragment = new AboutFragment();
                shouldAddToBackStack = true;
                break;
        }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,fragment);
            if(shouldAddToBackStack)ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        return true;
    }

    public ExcludedPhoneNumbersPersistenceService getPersistenceService(){
        return ExcludedPhoneNumbersPersistenceService.getInstance(this);
    }


}
