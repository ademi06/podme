package uk.ac.tees.w9312536.PodMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import uk.ac.tees.w9312536.bukolafatunde.R;
import uk.ac.tees.w9312536.bukolafatunde.databinding.ActivityMainBinding;
import uk.ac.tees.w9312536.PodMe.ui.downloads.DownloadsFragment;
import uk.ac.tees.w9312536.PodMe.ui.editprofile.EditProfileFragment;
import uk.ac.tees.w9312536.PodMe.ui.favorites.FavoritesFragment;
import uk.ac.tees.w9312536.PodMe.ui.podcasts.PodcastsFragment;
import uk.ac.tees.w9312536.PodMe.ui.settings.SettingsFragment;

import static uk.ac.tees.w9312536.PodMe.utilities.Constants.INDEX_ZERO;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /** This field is used for data binding **/
    ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mMainBinding.appBarMain.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mMainBinding.drawerLayout, mMainBinding.appBarMain.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mMainBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mMainBinding.navView.setNavigationItemSelectedListener(this);


        // set pod cast fragment as default fragment when starting the app
        if (savedInstanceState == null) {
            onNavigationItemSelected(mMainBinding.navView.getMenu().getItem(INDEX_ZERO).setChecked(true));
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Replace the fragment using a FragmentManager and Transaction
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_pod_casts) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new PodcastsFragment())
                    .commit();
        } else if (id == R.id.nav_favorites) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FavoritesFragment())
                    .commit();
        }
        else if (id == R.id.nav_downloads) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new DownloadsFragment())
                    .commit();
        } else if (id == R.id.nav_edit_profile) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new EditProfileFragment())
                    .commit();
        } else if (id == R.id.nav_settings) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .commit();
        }

        mMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}