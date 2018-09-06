package com.deedsit.android.bookworm.factory;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.arch.AppGraph;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.ui.interfacelisteners.GraphButtonClicked;
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentLecturer;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentStudent;
import com.deedsit.android.bookworm.ui.mainactivityfragments.RatingFragmentLecturer;
import com.deedsit.android.bookworm.ui.mainactivityfragments.StudentListingFragment;
import com.deedsit.android.bookworm.ui.mainactivityfragments.StudentRatingFragment;
import com.deedsit.android.bookworm.ui.mainactivityfragments.ViewClassesFragment;
import com.deedsit.android.bookworm.ui.presenter.LectureHomePresenter;
import com.deedsit.android.bookworm.ui.presenter.LectureRatingPresenter;
import com.deedsit.android.bookworm.ui.presenter.LecturerViewClassPresenter;
import com.deedsit.android.bookworm.ui.presenter.RecyclerViewPresenter;
import com.deedsit.android.bookworm.ui.presenter.ServicePresenter;
import com.deedsit.android.bookworm.ui.presenter.StudentHomePresenter;
import com.deedsit.android.bookworm.ui.presenter.StudentListingPresenter;
import com.deedsit.android.bookworm.ui.presenter.StudentRatingPresenter;


public class FactoryPresenter extends AbstractPresenterFactory {

    public FactoryPresenter() {
    }

    private void buildPresenter(FragmentView fragmentView, AppGraph appGraph) {
        switch (fragmentView.getFragmentTag()) {
            case HomeFragmentLecturer.TAG:
                RecyclerViewPresenter lectureHomePresenter = new LectureHomePresenter(fragmentView, appGraph);
                FragmentRecyclerView fragmentRecyclerView = (FragmentRecyclerView) fragmentView;
                fragmentRecyclerView.setOnFragmentStateChangeListener(lectureHomePresenter);
                fragmentRecyclerView.setOnRowAddedListener(lectureHomePresenter);
                fragmentRecyclerView.setOnRowClickedListener(lectureHomePresenter);
                break;
            case HomeFragmentStudent.TAG:
                RecyclerViewPresenter studentHomePresenter = new StudentHomePresenter
                        (fragmentView, appGraph);
                fragmentRecyclerView = (FragmentRecyclerView) fragmentView;
                fragmentRecyclerView.setOnFragmentStateChangeListener(studentHomePresenter);
                fragmentRecyclerView.setOnRowAddedListener(studentHomePresenter);
                fragmentRecyclerView.setOnRowClickedListener(studentHomePresenter);
                break;
            case RatingFragmentLecturer.TAG:
                ServicePresenter servicePresenter = new LectureRatingPresenter(fragmentView,
                        appGraph);
                ((RatingFragmentLecturer) fragmentView).setGraphButtonClicked((LectureRatingPresenter) servicePresenter);
                ((RatingFragmentLecturer) fragmentView).setOnFragmentStateChangeListener((LectureRatingPresenter) servicePresenter);
                break;
            case StudentRatingFragment.TAG:
                ServicePresenter studentRatingPresenter = new StudentRatingPresenter
                        (fragmentView, appGraph);
                ((StudentRatingFragment) fragmentView).setOnFragmentStateChangeListener(
                        (OnFragmentStateChangeListener) studentRatingPresenter);
                ((StudentRatingFragment) fragmentView).setOnGraphButtonClicked((GraphButtonClicked)
                        studentRatingPresenter);
                break;
            case StudentListingFragment.TAG:
                RecyclerViewPresenter studentListingPresenter = new StudentListingPresenter
                        (appGraph, fragmentView);
                ((StudentListingFragment)fragmentView).setFragmentStateChangeListener(studentListingPresenter);
        }
    }

    @Override
    public void buildPresenter(FragmentView fragmentView, AppGraph appGraph, Object data) {
        if (data == null) {
                buildPresenter(fragmentView, appGraph);
        } else {
            switch (fragmentView.getFragmentTag()) {
                case ViewClassesFragment.TAG:
                    Constants.courseSelected = (Course) data;
                    RecyclerViewPresenter recyclerViewPresenter = new LecturerViewClassPresenter
                            (fragmentView, appGraph);
                    FragmentRecyclerView fragmentRecyclerView = (FragmentRecyclerView) fragmentView;
                    fragmentRecyclerView.setOnFragmentStateChangeListener(recyclerViewPresenter);
                    fragmentRecyclerView.setOnRowClickedListener(recyclerViewPresenter);
                    fragmentRecyclerView.setOnRowAddedListener(recyclerViewPresenter);
                    break;
            }
        }
    }
}
