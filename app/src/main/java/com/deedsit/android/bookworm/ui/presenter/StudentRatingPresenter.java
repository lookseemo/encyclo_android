package com.deedsit.android.bookworm.ui.presenter;

import android.widget.Toast;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.arch.AppGraph;
import com.deedsit.android.bookworm.ui.interfacelisteners.GraphButtonClicked;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.services.impl.DataRepoImpl;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnClassStatusChanged;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.ui.mainactivityfragments.StudentRatingFragment;
import com.deedsit.android.bookworm.util.CustomGraphTask;

import java.util.ArrayList;
import java.util.Timer;

import javax.inject.Inject;

import static com.deedsit.android.bookworm.Constants.liveClass;
import static com.deedsit.android.bookworm.Constants.masterRatingList;


public class StudentRatingPresenter extends BasePresenter implements ServicePresenter,
        GraphPresenter, OnFragmentStateChangeListener, GraphButtonClicked {
    @Inject
    DataRepo dataRepo;

    private final static String TAG = "STUDENT_RATING_PRESENTER";

    private FragmentView fragmentView;
    private CustomGraphTask graphTask;
    private Timer timer;
    private OnClassStatusChanged onClassStatusChanged;
    private ArrayList<Rating> ratingArrayList;
    private boolean registeredClassService;
    private boolean registeredRatingService;

    public StudentRatingPresenter(FragmentView fragmentView, AppGraph appGraph) {
        super(fragmentView, appGraph);
        appGraph.inject(this);
        this.fragmentView = fragmentView;
        registeredClassService = false;
        registeredRatingService = false;
        onClassStatusChanged = (StudentRatingFragment) fragmentView;
        graphTask = new CustomGraphTask(((StudentRatingFragment) fragmentView).getHandler(), this);
        if (masterRatingList == null)
            masterRatingList = new ArrayList<>();
        ratingArrayList = new ArrayList<>();
    }

    @Override
    public void onSuccess(Object data) {

    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onDataSuccessfullyAdded(Object data) {
        if (data instanceof Rating) {
            Rating rating = (Rating) data;
            masterRatingList.add(rating);
        } else if (data instanceof CourseClass) {
            // initial point of class;
            if (liveClass == null) {
                liveClass = (CourseClass) data;
                processClassStatusChanged(liveClass.classStatus);
            }
            if (!registeredRatingService) {
                dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Rating, this,
                        fragmentView.getFragmentTag());
                dataRepo.queryDatabase(DataRepoImpl.queryPath.Rating, Constants.courseSelected.code,
                        fragmentView.getFragmentTag(), liveClass.id);
                registeredRatingService = true;
            }
            Toast.makeText(fragmentView.getFragmentActivity(), "CLASS ID: "+liveClass.id,Toast
                    .LENGTH_LONG).show();
        }
    }

    @Override
    public void onDataSuccessfullyChanged(Object data) {
        if (data instanceof CourseClass) {
            CourseClass courseClass = (CourseClass) data;
            if(liveClass != null) {
                if (courseClass.id.equalsIgnoreCase(liveClass.id)) {
                    if (courseClass.classStatus != liveClass.classStatus) {
                        liveClass.classStatus = courseClass.classStatus;
                        processClassStatusChanged(liveClass.classStatus);
                        if (onClassStatusChanged != null)
                            onClassStatusChanged.classStatusChanged(liveClass.classStatus);
                    }
                }
            }
        }
    }

    @Override
    public void onDataSuccessfullyRemoved(Object data) {

    }

    @Override
    public void processData() {
        if (masterRatingList.size() > 0) {
            int pos = 0;
            for (Rating rating : masterRatingList) {
                if (rating.value > 0) {
                    pos++;
                }
            }
            float posVal = pos * 100f / masterRatingList.size();
            float negVal = 0f - (100f - posVal);
            // we create 2 new ones that hold the values for the graph
            Rating posRating = new Rating();
            Rating negRating = new Rating();
            posRating.value = posVal;
            negRating.value = negVal;
            ratingArrayList.add(posRating);
            ratingArrayList.add(negRating);
        }
    }

    @Override
    public String getPresenterTag() {
        return TAG;
    }

    @Override
    public void onFragmentResume() {
        if (!registeredClassService) {
            dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Class, this, fragmentView
                    .getFragmentTag());
            dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Rating, this, fragmentView.getFragmentTag());
            registeredClassService = true;
        }
        dataRepo.queryDatabase(DataRepoImpl.queryPath.Class, Constants.courseSelected.code,
                fragmentView.getFragmentTag(), null);
        if (timer == null)
            timer = new Timer();
        if(graphTask == null)
            graphTask = new CustomGraphTask(((StudentRatingFragment) fragmentView).getHandler(), this);
        timer.schedule(graphTask, 1000, 1000);
    }

    @Override
    public void onFragmentPause() {
        if (registeredClassService && registeredRatingService) {
            dataRepo.unregisterCallBack(fragmentView.getFragmentTag());
            registeredRatingService = false;
            registeredClassService = false;
        }

        if(graphTask != null)
            graphTask.cancel();
        graphTask = null;
        if(timer != null)
            timer.cancel();
        timer = null;
    }

    @Override
    public ArrayList<Rating> getRatingList() {
        return ratingArrayList;
    }

    @Override
    public void clearRatingList() {
        ratingArrayList.clear();
    }

    @Override
    public void onFabButtonClicked(boolean paused) {
        //Blank for student
    }

    @Override
    public void onEndClassButtonClicked(double averageRating) {
        //Blank for student
    }

    @Override
    public void onRatingButtonClicked(boolean isLikeBtn) {
        Rating rating = new Rating();
        rating.classId = Constants.liveClass.id;
        rating.rateTime = System.currentTimeMillis();
        rating.username = Constants.user.username;
        if(isLikeBtn)
            rating.value = 1;
        else
            rating.value = 0;
        dataRepo.addRating(rating);
    }

    private void processClassStatusChanged(CourseClass.classStatusInd classStatusInd){
        if(classStatusInd != CourseClass.classStatusInd.LIVE){
            if(graphTask != null)
                graphTask.cancel();
            graphTask = null;
            timer.cancel();
            timer = null;
        }else{
            graphTask = new CustomGraphTask(((StudentRatingFragment) fragmentView).getHandler(), this);
            if (timer == null)
                timer = new Timer();
            timer.schedule(graphTask, 1000, 1000);
        }
    }
}
