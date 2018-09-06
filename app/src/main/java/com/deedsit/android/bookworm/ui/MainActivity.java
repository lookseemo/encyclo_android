package com.deedsit.android.bookworm.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnMainActivityBackPressListener;
import com.deedsit.android.bookworm.models.User;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.services.ServiceCallback;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.mainactivityfragments.MasterHomeFragment;
import com.deedsit.android.bookworm.ui.navigationFragment.ProfileFragment;
import com.deedsit.android.bookworm.ui.navigationFragment.SettingsFragment;
import com.deedsit.android.bookworm.util.DataFormatedHelper;
import com.deedsit.android.bookworm.util.NavigationController;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.deedsit.android.bookworm.Constants.isPaused;
import static com.deedsit.android.bookworm.Constants.masterRatingList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    @Inject
    DataRepo dataRepo;

    @Inject
    AuthRepo authRepo;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_nav)
    BottomNavigationView bottomNavigationView;

    private ProgressDialog progressDialog;
    private static FragmentView masterHomeFragment;
    private static FragmentView profileFragment;
    private static FragmentView settingsFragment;
    private OnMainActivityBackPressListener onBackPressListener;


    //[OVERRIDE METHODS START]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BookWormApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        displayProgressDialog("Getting User Data");
        User user = authRepo.getCurrentUser();
        if (user != null) {
            dataRepo.getUser(user.email, new ServiceCallback<User>() {
                @Override
                public void onSuccess(User data) {
                    Constants.user = data;
                    buildAllFragments(data.userType);
                    hideProgressDialog();
                }

                @Override
                public void onFailure(Exception e) {
                    hideProgressDialog();
                    MainActivity.this.logOut();
                }

                @Override
                public void onDataSuccessfullyAdded(User data) {

                }

                @Override
                public void onDataSuccessfullyChanged(User data) {

                }

                @Override
                public void onDataSuccessfullyRemoved(User data) {

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        if(((Fragment)masterHomeFragment).isVisible()){
            if(onBackPressListener != null)
                onBackPressListener.onBackPressListener();
        }else{ // exit the app if other fragments.
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if(id == android.R.id.home){
            onBackPressed();
       }

        return super.onOptionsItemSelected(item);
    }


    //[OVERRIDE METHODS END]

    public void logOut() {
        if (authRepo.signOut()) {
            Constants.isLive = false;
            Constants.courseSelected = null;
            Constants.liveClass = null;
            DataFormatedHelper.stopClock();
            masterRatingList = null;
            isPaused = false;
            Intent intent = new Intent(this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.my_course:
                fragmentTransaction.show((Fragment) masterHomeFragment).hide((Fragment) profileFragment).hide
                        ((Fragment) settingsFragment).commit();
                break;
            case R.id.my_profile:
                fragmentTransaction.hide((Fragment) masterHomeFragment).show((Fragment)
                        profileFragment).hide
                        ((Fragment) settingsFragment).commit();
                break;
            case R.id.settings_nav:
                fragmentTransaction.hide((Fragment) masterHomeFragment).hide((Fragment)
                        profileFragment).show
                        ((Fragment) settingsFragment).commit();
                break;
        }
        return true;
    }


    private void buildAllFragments(String userType) {
        //Master Fragment
        if(masterHomeFragment == null)
            masterHomeFragment = MasterHomeFragment.newInstance();
        onBackPressListener = (OnMainActivityBackPressListener) masterHomeFragment;
        //Profile
        if(profileFragment == null)
            profileFragment = ProfileFragment.newInstance();
        //Settings
        if(settingsFragment == null)
            settingsFragment = SettingsFragment.newInstance();

        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_content, (Fragment) masterHomeFragment,
                MasterHomeFragment.TAG).addToBackStack(null).commit();

        fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_content, (Fragment) profileFragment,
                ProfileFragment.TAG).addToBackStack(null).commit();

        fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_content, (Fragment) settingsFragment,
                SettingsFragment.TAG).addToBackStack(null).commit();

        //hide the 2 other fragments
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide((Fragment) profileFragment).hide((Fragment) settingsFragment).commit();
    }

    public void displayProgressDialog(String msg){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public void hideProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}