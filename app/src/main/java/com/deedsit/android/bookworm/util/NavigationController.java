package com.deedsit.android.bookworm.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentLecturer;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentStudent;
import com.deedsit.android.bookworm.ui.mainactivityfragments.MasterHomeFragment;
import com.deedsit.android.bookworm.ui.mainactivityfragments.RatingFragmentLecturer;
import com.deedsit.android.bookworm.ui.mainactivityfragments.RatingFragmentParent;
import com.deedsit.android.bookworm.ui.mainactivityfragments.StudentRatingFragment;
import com.deedsit.android.bookworm.ui.mainactivityfragments.ViewClassesFragment;

import java.util.ArrayList;

public class NavigationController {

    public static void navigateToMasterHomeFragment(String userType, FragmentManager
            fragmentManager, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MasterHomeFragment masterHomeFragment = MasterHomeFragment.newInstance();
        if (!addToBackStack)
            fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.replace(R.id.frame_layout_content, masterHomeFragment,
                MasterHomeFragment.TAG).addToBackStack(null).commit();
    }


    public static void navigateToHomeLecturerFragment(MasterHomeFragment masterHomeFragment,
                                                      FragmentManager fragmentManager, boolean
                                                              addToBackStack, boolean animation) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (animation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exist_to_left, R.anim.enter_from_left, R.anim.exist_to_right);

        Fragment homeFragmentLecturer = HomeFragmentLecturer.newInstance();
        ((HomeFragmentLecturer) homeFragmentLecturer).setMasterHomeFragment(masterHomeFragment);

        if (!addToBackStack)
            fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.replace(R.id.master_home_layout_content, homeFragmentLecturer,
                HomeFragmentLecturer.TAG).commit();
        Constants.liveFragmentTag = null;
    }


    public static void navigateToClassLecturerFragment(MasterHomeFragment masterHomeFragment,
                                                       boolean addToBackStack, boolean animation) {
        FragmentManager fragmentManager = masterHomeFragment.getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (animation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exist_to_left,
                    R.anim.enter_from_left, R.anim.exist_to_right);

        Fragment viewClassLecturerFragment = ViewClassesFragment.newInstance();
        ((ViewClassesFragment) viewClassLecturerFragment).setMasterHomeFragment(masterHomeFragment);

        if (!addToBackStack) {
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.replace(R.id.master_home_layout_content, viewClassLecturerFragment).commit();
        } else {
            fragmentTransaction.replace(R.id.master_home_layout_content, viewClassLecturerFragment,
                    ViewClassesFragment.TAG).addToBackStack(null).commit();
        }
        Constants.liveFragmentTag = ViewClassesFragment.TAG;
    }


    public static void navigateToRatingMasterFragment(MasterHomeFragment masterHomeFragment, boolean
            addToBackStack, boolean animation) {
        FragmentManager fragmentManager = masterHomeFragment.getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (animation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exist_to_left,
                    R.anim.enter_from_left, R.anim.exist_to_right);

        Fragment ratingMasterFragment = RatingFragmentParent.newInstance(masterHomeFragment);
        if (!addToBackStack)
            fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.replace(R.id.master_home_layout_content, ratingMasterFragment,
                RatingFragmentParent.TAG).addToBackStack(null).commit();
        Constants.liveFragmentTag = RatingFragmentParent.TAG;
    }


    public static void navigateToRatingLecturerFragment(FragmentView masterFragment, FragmentManager
            fragmentManager, boolean addToBackStack, int layout, boolean animation) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (animation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exist_to_left,
                    R.anim.enter_from_left, R.anim.exist_to_right);

        Fragment ratingLecturerFragment = RatingFragmentLecturer.newInstance();

        if (!addToBackStack)
            fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.replace(layout, ratingLecturerFragment,
                RatingFragmentLecturer.TAG).addToBackStack(null).commit();

    }


    public static void navigateToHomeStudentFragment(MasterHomeFragment masterHomeFragment, boolean
            addToBackStack, boolean animation) {
        FragmentManager fragmentManager = masterHomeFragment.getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (animation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exist_to_left,
                    R.anim.enter_from_left, R.anim.exist_to_right);

        Fragment homeFragmentStudent = HomeFragmentStudent.newInstance();
        ((HomeFragmentStudent) homeFragmentStudent).setMasterFragment(masterHomeFragment);

        if (!addToBackStack) {
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.replace(R.id.master_home_layout_content, homeFragmentStudent,
                    HomeFragmentStudent.TAG).commit();
        } else {
            fragmentTransaction.replace(R.id.master_home_layout_content, homeFragmentStudent,
                    HomeFragmentStudent.TAG).addToBackStack(null).commit();
        }
        Constants.liveFragmentTag = null;
    }


    public static void navigateToRatingStudentFragment(MasterHomeFragment masterFragment, boolean
            addToBackStack, boolean animation) {
        FragmentManager fragmentManager = masterFragment.getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (animation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exist_to_left,
                    R.anim.enter_from_left, R.anim.exist_to_right);

        Fragment studentRatingFragment = StudentRatingFragment.newInstance();
        ((StudentRatingFragment) studentRatingFragment).setMasterHomeFragment(masterFragment);


        if (!addToBackStack) {
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.replace(R.id.master_home_layout_content, studentRatingFragment,
                    HomeFragmentStudent.TAG).commit();
        } else {
            fragmentTransaction.replace(R.id.master_home_layout_content, studentRatingFragment,
                    HomeFragmentStudent.TAG).addToBackStack(null).commit();
        }
        Constants.liveFragmentTag = StudentRatingFragment.TAG;
    }
}
