package com.deedsit.android.bookworm.ui.navigationFragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.ui.base.FragmentView;

public class ProfileFragment extends Fragment implements FragmentView {

    public static final String TAG = "PROFILE_FRAGMENT";

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void buildPresenter() {

    }

    @Override
    public void displayErrorMeesage(int errorCode) {

    }

    @Override
    public Activity getFragmentActivity() {
        return getActivity();
    }

    @Override
    public FragmentManager getParentFragmentManager() {
        return this.getActivity().getSupportFragmentManager();
    }
}
