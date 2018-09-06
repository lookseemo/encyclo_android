package com.deedsit.android.bookworm.ui.presenter;

import android.view.View;
import android.widget.TextView;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.arch.AppGraph;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.ListItem;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.services.impl.DataRepoImpl;
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.mainactivityfragments.ViewClassesFragment;
import com.deedsit.android.bookworm.util.DataFormatedHelper;
import com.deedsit.android.bookworm.util.NavigationController;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import static com.deedsit.android.bookworm.Constants.courseSelected;


public class LecturerViewClassPresenter extends BasePresenter implements RecyclerViewPresenter {

    @Inject
    DataRepo dataRepo;

    private static final String TAG = "VIEW_CLASS_PRESENTER";

    private FragmentRecyclerView fragmentRecyclerView;
    private ArrayList<ListItem> adapterList;
    private boolean registeredCallback;

    public LecturerViewClassPresenter(FragmentView fragmentView, AppGraph appGraph) {
        super(fragmentView, appGraph);
        appGraph.inject(this);
        this.fragmentRecyclerView = (FragmentRecyclerView) fragmentView;
        adapterList = new ArrayList<>();
        registeredCallback = false;

    }

    private boolean classIdExisted(String id) {
        for(CourseClass courseClass : courseSelected.getClasses()){
            if(id.equalsIgnoreCase(courseClass.id))
                return true;
        }
        return false;
    }

    @Override
    public void onSuccess(Object data) {

    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onDataSuccessfullyAdded(Object data) {
        CourseClass courseClass = (CourseClass) data;
        boolean classExisted = classIdExisted(courseClass.id);
        if (!classExisted) {
            courseSelected.getClasses().add(courseClass);
        }
        processDataChanged();
    }

    @Override
    public void onDataSuccessfullyChanged(Object data) {
        CourseClass courseClass = (CourseClass) data;
        for (CourseClass classInList : courseSelected.getClasses()) {
            if (courseClass.id.equalsIgnoreCase(classInList.id)) {
                //set all the stuffs
                classInList.title = courseClass.title;
                classInList.setClassStatus(courseClass.getClassStatus());
                classInList.averageRating = courseClass.averageRating;
                classInList.startTime = courseClass.startTime;
                classInList.finishTime = courseClass.finishTime;
                break;
            }
        }
        processDataChanged();
    }

    @Override
    public void onDataSuccessfullyRemoved(Object data) {

    }

    @Override
    public void registerCallBackListener() {
        if (!registeredCallback) {
            dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Class, this,
                    fragmentRecyclerView.getFragmentTag());
        }
        dataRepo.queryDatabase(DataRepoImpl.queryPath.Class, courseSelected.code,
                fragmentRecyclerView
                .getFragmentTag(), null);
    }

    @Override
    public void processDataChanged() {
        if(courseSelected != null) {
            if (adapterList.size() == 0) {
                adapterList = DataFormatedHelper.prepareListItem(courseSelected);
            } else
                DataFormatedHelper.classAddedToList(courseSelected.getClasses().get(courseSelected.getClasses().size
                        () - 1), adapterList);
            fragmentRecyclerView.displayData(adapterList);
        }
    }

    /**
     *  Start the class as soon as the row added.
     * @param data
     */
    @Override
    public void onRowAddedListener(Object data) {
        // check if any other class is live
        if (!Constants.isLive) {
            // update the class status
            CourseClass courseClass = (CourseClass) data;
            courseClass.classStatus = CourseClass.classStatusInd.LIVE;

            //generate an id for this class
            while (classIdExisted(courseClass.id)) {
                Random random = new Random();
                courseClass.id = "";
                while (courseClass.id.length() < 4) {
                    courseClass.id += random.nextInt(10) + "";
                }
            }

            // add class into memory
            Constants.liveClass = courseClass;
            dataRepo.addClass(courseClass, courseSelected.code);

            //navigate to rating
            NavigationController.navigateToRatingMasterFragment(((ViewClassesFragment)
                    fragmentRecyclerView).getMasterHomeFragment(), true, true);
        } else {
            fragmentRecyclerView.displayErrorMeesage(Constants.CREATE_CLASS_ERR);
        }
    }

    @Override
    public void onFragmentResume() {
        registerCallBackListener();
        TextView toolBarTitle = fragmentRecyclerView.getFragmentActivity().findViewById(R.id
                .toolbar_title);
        TextView toolBarSubTitle = fragmentRecyclerView.getFragmentActivity().findViewById(R.id
                .toolbar_sub_title);
        toolBarTitle.setVisibility(View.VISIBLE);
        toolBarSubTitle.setVisibility(View.VISIBLE);
        toolBarTitle.setText(courseSelected.title);
        toolBarSubTitle.setText(courseSelected.code);
    }

    @Override
    public void onFragmentPause() {
        adapterList.clear();
        dataRepo.unregisterCallBack(fragmentRecyclerView.getFragmentTag());
        registeredCallback = false;
    }

    /**
     * Handle row clicked in recycler view, we want to start the class right away if the class
     * status is live or paused. Started class will be added to memory.
     *
     * @param object: course class selected to start class
     */
    @Override
    public void OnRecyclerViewRowClicked(Object object) {
        CourseClass courseClass = (CourseClass) object;
        // check if class is on
        if (courseClass.classStatus == CourseClass.classStatusInd.LIVE || courseClass.classStatus
                == CourseClass.classStatusInd.PAUSED) {
            courseClass.classStatus = CourseClass.classStatusInd.LIVE;

            // add class to constant
            if (Constants.liveClass == null) {
                Constants.liveClass = courseClass;
                Constants.second = 0;
                Constants.minute = 0;
                Constants.hour = 0;
            }

            // stop the background clock if its running
            DataFormatedHelper.stopClock();

            //navigate to rating fragment
            NavigationController.navigateToRatingMasterFragment(((ViewClassesFragment)
                    fragmentRecyclerView).getMasterHomeFragment(), true, true);
            // up date class live flag and course status
            if (!Constants.isLive || !courseSelected.isLive) {
                Constants.isLive = true;
                courseSelected.isLive = true;
            }

            dataRepo.updateClassStatus(courseClass, courseSelected.code);
        }
    }

    @Override
    public String getPresenterTag() {
        return TAG;
    }
}
