package com.deedsit.android.bookworm.ui.mainactivityfragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.ui.MainActivity;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnMainActivityBackPressListener;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.util.DataFormatedHelper;
import com.deedsit.android.bookworm.util.NavigationController;

import static com.deedsit.android.bookworm.Constants.LECTURER_USER;
import static com.deedsit.android.bookworm.Constants.courseSelected;
import static com.deedsit.android.bookworm.Constants.liveClass;
import static com.deedsit.android.bookworm.Constants.liveFragmentTag;
import static com.deedsit.android.bookworm.Constants.user;

public class MasterHomeFragment extends Fragment implements FragmentView, OnMainActivityBackPressListener {

    //TODO: put on resume to the fragment that was showing when app resumed from background

    public final static String TAG = "MASTER_HOME_FRAGMENT";
    private final static String USER_TYPE = "USER_TYPE";

    public MasterHomeFragment() {
        // Required empty public constructor
    }

    public static MasterHomeFragment newInstance() {
        MasterHomeFragment fragment = new MasterHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_master_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstantState) {
        super.onActivityCreated(savedInstantState);
        if (user.userType.equalsIgnoreCase(LECTURER_USER))
            NavigationController.navigateToHomeLecturerFragment(this,
                    this.getChildFragmentManager(), false, false);

        else
            NavigationController.navigateToHomeStudentFragment(this, false, false);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void buildPresenter() {
        // no presenter needed
    }

    @Override
    public void displayErrorMeesage(int errorCode) {

    }

    @Override
    public Activity getFragmentActivity() {
        return this.getActivity();
    }

    @Override
    public FragmentManager getParentFragmentManager() {
        return this.getChildFragmentManager();
    }

    @Override
    public void onBackPressListener() {
        if (this.getChildFragmentManager().getBackStackEntryCount() > 0) {
            runRatingDataClock();
            this.getChildFragmentManager().popBackStack();
        } else {
            this.getActivity().finish();
        }
    }

    private void runRatingDataClock() {
        if (user.userType.equalsIgnoreCase(LECTURER_USER)) {
            RatingFragmentParent ratingFragmentParent = (RatingFragmentParent) this.getChildFragmentManager()
                    .findFragmentByTag(RatingFragmentParent.TAG);
            if (ratingFragmentParent != null) { // user pressed back while class still live
                DataFormatedHelper.runClockBackThread();
            }
        } else {
            StudentRatingFragment studentRatingFragment = (StudentRatingFragment) this
                    .getChildFragmentManager().findFragmentByTag(StudentRatingFragment.TAG);
            if (studentRatingFragment != null) {
                DataFormatedHelper.runClockBackThread();
            }
        }
    }

    public void displayProgressBar(String msg){
        if(this.getFragmentActivity() != null){
            ((MainActivity)this.getFragmentActivity()).displayProgressDialog(msg);
        }
    }

    public void hideProgressDialog(){
        if(this.getFragmentActivity() != null){
            ((MainActivity)this.getFragmentActivity()).hideProgressDialog();
        }
    }
}
