package com.deedsit.android.bookworm.ui.presenter;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.arch.AppGraph;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.ListItem;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.services.impl.DataRepoImpl;
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentStudent;
import com.deedsit.android.bookworm.util.DataFormatedHelper;
import com.deedsit.android.bookworm.util.NavigationController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;

import static com.deedsit.android.bookworm.Constants.COURSE_DUP;
import static com.deedsit.android.bookworm.Constants.LOAD_CLASS_ERR;
import static com.deedsit.android.bookworm.Constants.courseSelected;


public class StudentHomePresenter extends BasePresenter implements RecyclerViewPresenter {
    @Inject
    DataRepo dataRepo;
    @Inject
    AuthRepo authRepo;
    private static String TAG = "STUDENT_HOME_PRESENTER";

    private FragmentRecyclerView fragmentRecyclerView;
    private ArrayList<ListItem> adapterList;
    private boolean registeredCallBack;

    public StudentHomePresenter(FragmentView fragmentView, AppGraph appGraph) {
        super(fragmentView, appGraph);
        this.fragmentRecyclerView = (FragmentRecyclerView) fragmentView;
        adapterList = new ArrayList<>();
        registeredCallBack = false;
        appGraph.inject(this);
    }

    @Override
    public void onSuccess(Object data) {

    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onDataSuccessfullyAdded(Object data) {
        if (data instanceof Course) {
            Course course = (Course) data;
            if (course.isLive) {
                Constants.isLive = true;
            }
            adapterList.add(course);
            processDataChanged();
        }
    }

    @Override
    public void onDataSuccessfullyChanged(Object data) {
        if (data instanceof Course) {
            Course course = (Course) data;
            for (ListItem listItem : adapterList) {
                Course courseInList = (Course) listItem;
                if (course.code.equalsIgnoreCase(courseInList.code)) {
                    courseInList.title = course.title;
                    courseInList.courseRating = course.courseRating;
                    courseInList.isLive = course.isLive;
                    break;
                }
            }
            if (course.isLive) {
                Constants.isLive = true;
            }
            processDataChanged();
        }
    }

    @Override
    public void onDataSuccessfullyRemoved(Object data) {
        if (data instanceof Course) {
            Course course = (Course) data;
            Iterator iterator = adapterList.iterator();
            while (iterator.hasNext()) {
                ListItem item = (ListItem) iterator.next();
                if (item instanceof Course) {
                    Course courseIT = (Course) item;
                    if (courseIT.code.equalsIgnoreCase(course.code)) {
                        iterator.remove();
                        break;
                    }
                }
            }
            processDataChanged();
        }
    }

    @Override
    public void onRowAddedListener(Object data) {
        //blank
        if (data instanceof Course) {
            Course course = (Course) data;
            for (ListItem item : adapterList) {
                if (item instanceof Course) {
                    if (course.code.equalsIgnoreCase(((Course) item).code)) {
                        fragmentRecyclerView.displayErrorMeesage(COURSE_DUP);
                        return;
                    }
                }
            }
            //add the course in
            dataRepo.addCourse(course);
        }
    }

    @Override
    public void onFragmentResume() {
        //we download the whole course back
        if (Constants.courseList == null || Constants.courseList.size() == 0) {
            dataRepo.queryDatabase(DataRepoImpl.queryPath.Course, null, fragmentRecyclerView
                    .getFragmentTag(), null);
        }
        if (!registeredCallBack) {
            dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Course, this, fragmentRecyclerView.getFragmentTag());
            adapterList.clear();
        }
        dataRepo.queryDatabase(DataRepoImpl.queryPath.Course, null, fragmentRecyclerView
                .getFragmentTag(), authRepo.getCurrentUser().username);
    }

    @Override
    public void onFragmentPause() {
        dataRepo.unregisterCallBack(fragmentRecyclerView.getFragmentTag());
        registeredCallBack = false;
    }

    @Override
    public void OnRecyclerViewRowClicked(Object object) {
        if (object instanceof Course) {
            Course course = (Course) object;
            if (course.isLive) {
                //navigate to rating
                DataFormatedHelper.stopClock();
                courseSelected = course;
                NavigationController.navigateToRatingStudentFragment(((HomeFragmentStudent)
                        fragmentRecyclerView).getMasterHomeFragment(), true, true);
            } else {
                fragmentRecyclerView.displayErrorMeesage(LOAD_CLASS_ERR);
            }
        }
    }

    @Override
    public String getPresenterTag() {
        return TAG;
    }

    @Override
    public void registerCallBackListener() {
        dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Course, this, fragmentRecyclerView
                .getFragmentTag());
        registeredCallBack = true;
        dataRepo.queryDatabase(DataRepoImpl.queryPath.Course, null, fragmentRecyclerView
                .getFragmentTag(), this.authRepo.getCurrentUser().username);
    }

    @Override
    public void processDataChanged() {
        Collections.sort(adapterList);
        fragmentRecyclerView.displayData(adapterList);
    }
}
