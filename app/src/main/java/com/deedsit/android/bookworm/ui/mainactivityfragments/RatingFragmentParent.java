package com.deedsit.android.bookworm.ui.mainactivityfragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.ui.interfacelisteners.GraphButtonClicked;
import com.deedsit.android.bookworm.ui.adapter.RatingPagerAdapter;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.google.SlidingTabLayout;
import com.deedsit.android.bookworm.util.DataFormatedHelper;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.deedsit.android.bookworm.Constants.MASTER_FRAG_TAG;
import static com.deedsit.android.bookworm.Constants.liveClass;


public class RatingFragmentParent extends Fragment implements FragmentView {


    public static final String TAG = "RATING_PARENT_FRAG";
    @BindView(R.id.rating_view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.sup_title_rating_parent)
    TextView supTitle;
    @BindView(R.id.title_rating_parent)
    TextView title;
    @BindView(R.id.fab_paused)
    FloatingActionButton fabPauseBtn;

    private GraphButtonClicked graphButtonClicked;
    private ArrayList<FragmentView> mFragmentList;
    private MasterHomeFragment masterHomeFragment;


    public static RatingFragmentParent newInstance(MasterHomeFragment masterHomeFragment) {
        RatingFragmentParent ratingFragmentParent = new RatingFragmentParent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MASTER_FRAG_TAG, masterHomeFragment);
        ratingFragmentParent.setArguments(bundle);
        return ratingFragmentParent;
    }

    public RatingFragmentParent() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getArguments() != null)
            this.masterHomeFragment = (MasterHomeFragment) this.getArguments().getSerializable(MASTER_FRAG_TAG);
        this.mFragmentList = new ArrayList<>();
        buildFragments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rating_parent_layout, container, false);
        ButterKnife.bind(this, view);
        viewPager.setAdapter(new RatingPagerAdapter(this.getFragmentActivity(),
                getChildFragmentManager(), mFragmentList));

        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingTabLayout.setDividerColors(R.drawable.side_nav_bar);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setmDistributeEvenly(true);

        String date = DataFormatedHelper.convertTimeOrDateToString(new Date(liveClass.startTime),
                true);
        String day = DataFormatedHelper.getDayFromStringTime(date, true);
        String startTime = DataFormatedHelper.convertTimeOrDateToString(new Date(liveClass.startTime),
                false);
        String now = "NOW";
        title.setText(liveClass.title);
        supTitle.setText(day + " " + date + " " + startTime + " - " + now);
        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void buildPresenter() {
        //Leave this blank
    }

    @Override
    public void displayErrorMeesage(int errorCode) {

    }

    @Override
    public Activity getFragmentActivity() {
        return masterHomeFragment.getActivity();
    }

    @Override
    public FragmentManager getParentFragmentManager() {
        return masterHomeFragment.getChildFragmentManager();
    }

    private void buildFragments() {
        RatingFragmentLecturer ratingFragmentLecturer = RatingFragmentLecturer.newInstance();
        ratingFragmentLecturer.setMasterFragment(this);
        this.graphButtonClicked = ratingFragmentLecturer;

        StudentListingFragment studentListFragment = StudentListingFragment.Companion.newInstance();
        studentListFragment.setMasterFragment(this);

        //add to list
        mFragmentList.add(ratingFragmentLecturer);
        mFragmentList.add(studentListFragment);
    }

    @OnClick(R.id.fab_paused)
    void onFabPauseClicked() {
        Constants.isPaused = !Constants.isPaused;
        if (Constants.isPaused)
            fabPauseBtn.setImageResource(R.drawable.ic_play_arrow);
        else
            fabPauseBtn.setImageResource(R.drawable.ic_pause_fab);
        graphButtonClicked.onFabButtonClicked(Constants.isPaused);
    }
}
