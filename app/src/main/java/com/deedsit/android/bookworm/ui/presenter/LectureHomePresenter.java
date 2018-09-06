package com.deedsit.android.bookworm.ui.presenter;

import android.util.Log;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.arch.AppGraph;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.ListItem;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.services.impl.DataRepoImpl;
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentLecturer;
import com.deedsit.android.bookworm.util.NavigationController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;

import static com.deedsit.android.bookworm.Constants.masterRatingList;

public class LectureHomePresenter extends BasePresenter implements RecyclerViewPresenter {

    @Inject
    DataRepo dataRepo;

    private FragmentRecyclerView fragmentRecyclerView;
    private ArrayList<ListItem> adapterList;
    private boolean registeredCallback;

    private static final String TAG = "LECTURER_HOME_PRESENTER";

    public LectureHomePresenter(FragmentView fragmentRecyclerView, AppGraph appGraph) {
        super(fragmentRecyclerView, appGraph);
        this.fragmentRecyclerView = (FragmentRecyclerView) fragmentRecyclerView;
        adapterList = new ArrayList<>();
        registeredCallback = false;
        appGraph.inject(this);
        // remove the course from memory
        Constants.courseSelected = null;
        Constants.liveFragmentTag = null;
    }

    @Override
    public String getPresenterTag() {
        return TAG;
    }

    @Override
    public void onSuccess(Object data) {
    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onDataSuccessfullyAdded(Object data) {
        //check upFront
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
            if (course.isLive) {
                Constants.isLive = true;
            }
            for (ListItem item : adapterList) {
                Course courseInList = (Course) item;
                if (courseInList.code.equalsIgnoreCase(course.code)) {
                    courseInList.title = course.title;
                    courseInList.isLive = course.isLive;
                    courseInList.courseRating = course.courseRating;
                    break;
                }
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
    public void registerCallBackListener() {
        dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Course, this, fragmentRecyclerView
                .getFragmentTag());
        registeredCallback = true;
        dataRepo.queryDatabase(DataRepoImpl.queryPath.Course, null, fragmentRecyclerView
                .getFragmentTag(), null);
    }

    @Override
    public void processDataChanged() {
        Collections.sort(adapterList);
        Log.d("JACKIE", "Process Data, Size: "+adapterList.size()+" Object ID:"+this.hashCode());
        fragmentRecyclerView.displayData(adapterList);
    }

    @Override
    public void onFragmentResume() {
        //same as fragment resume now
        if (!registeredCallback) {
            dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Course, this, fragmentRecyclerView.getFragmentTag());
            adapterList.clear();
        }
        dataRepo.queryDatabase(DataRepoImpl.queryPath.Course, null, fragmentRecyclerView
                .getFragmentTag(), Constants.user.username);
    }

    @Override
    public void onFragmentPause() {
        dataRepo.unregisterCallBack(fragmentRecyclerView.getFragmentTag());
        registeredCallback = false;
    }


    @Override
    public void onRowAddedListener(Object data) {
        if (data instanceof Course) {
            Course course = (Course) data;
            if (!Constants.isLive)
                dataRepo.addCourse(course);
            else
                fragmentRecyclerView.displayErrorMeesage(Constants.CREATE_COURSE_ERR);
        }
    }

    @Override
    public void OnRecyclerViewRowClicked(Object object) {
        if (object instanceof Course) {
            Constants.courseSelected = (Course) object;
            NavigationController.navigateToClassLecturerFragment(((HomeFragmentLecturer)
                    fragmentRecyclerView).getMasterHomeFragment(), true, true);
        }
    }
}
