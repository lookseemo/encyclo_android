package com.deedsit.android.bookworm.ui.presenter;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.arch.AppGraph;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.services.impl.DataRepoImpl;
import com.deedsit.android.bookworm.ui.MainActivity;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.base.GraphFragmentView;
import com.deedsit.android.bookworm.ui.interfacelisteners.GraphButtonClicked;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.ui.mainactivityfragments.RatingFragmentLecturer;
import com.deedsit.android.bookworm.util.CustomGraphTask;

import java.util.ArrayList;
import java.util.Timer;

import javax.inject.Inject;

import static com.deedsit.android.bookworm.Constants.hour;
import static com.deedsit.android.bookworm.Constants.isLive;
import static com.deedsit.android.bookworm.Constants.liveClass;
import static com.deedsit.android.bookworm.Constants.masterListSize;
import static com.deedsit.android.bookworm.Constants.masterRatingList;
import static com.deedsit.android.bookworm.Constants.minute;
import static com.deedsit.android.bookworm.Constants.second;


public class LectureRatingPresenter extends BasePresenter implements ServicePresenter,
        GraphPresenter, GraphButtonClicked, OnFragmentStateChangeListener {

    @Inject
    DataRepo dataRepo;

    private static final int TASK_INTERVAL = 1000;
    private static final String TAG = "LECTURE_RATING_PRESENTER";

    private GraphFragmentView fragmentView;
    private ArrayList<Rating> ratingArrayList;
    private Timer timer;
    private CustomGraphTask graphTask;
    private boolean registeredRatingService;


    public LectureRatingPresenter(FragmentView fragmentView, AppGraph appGraph) {
        super(fragmentView, appGraph);
        appGraph.inject(this);

        ratingArrayList = new ArrayList<>();
        if (masterRatingList == null)
            masterRatingList = new ArrayList<>();

        this.fragmentView = (GraphFragmentView) fragmentView;

        // init graph handler
        timer = new Timer();
        if (graphTask == null) {
            MainActivity mainActivity = (MainActivity) fragmentView.getFragmentActivity();
            if (mainActivity != null)
                graphTask = new CustomGraphTask(((RatingFragmentLecturer) fragmentView).getHandler
                        (), this);

            timer.schedule(graphTask, TASK_INTERVAL, TASK_INTERVAL); // delay 1 second.
        }

        registeredRatingService = false;
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
            // clear the rating list, we create 2 new ones that hold the values for the graph
            Rating posRating = new Rating();
            Rating negRating = new Rating();
            posRating.value = posVal;
            negRating.value = negVal;
            ratingArrayList.add(posRating);
            ratingArrayList.add(negRating);
        }
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
            if (masterRatingList == null)
                masterRatingList = new ArrayList<>();
            masterRatingList.add((Rating) data);
        }
    }

    @Override
    public void onDataSuccessfullyChanged(Object data) {

    }

    @Override
    public void onDataSuccessfullyRemoved(Object data) {

    }

    @Override
    public String getPresenterTag() {
        return TAG;
    }

    @Override
    public void onFabButtonClicked(boolean paused) {
        Constants.isPaused = paused;
        Constants.liveClass.classStatus = paused ? CourseClass.classStatusInd.PAUSED :
                CourseClass.classStatusInd.LIVE;
        dataRepo.updateClassStatus(Constants.liveClass, null);
    }

    @Override
    public void onEndClassButtonClicked(double averageRating) {
        graphTask.cancel();
        liveClass.classStatus = CourseClass.classStatusInd.ENDED;
        liveClass.averageRating = averageRating;
        liveClass.finishTime = System.currentTimeMillis();

        dataRepo.updateClassStatus(Constants.liveClass, Constants.courseSelected.code);
        liveClass = null;
        isLive = false;
        second = 0;
        minute = 0;
        hour = 0;
        masterListSize = 0;
        masterRatingList.clear();
    }

    @Override
    public void onRatingButtonClicked(boolean isLikeBtn) {
        // Blank for lecturer
    }

    @Override
    public ArrayList<Rating> getRatingList() {
        return ratingArrayList;
    }

    public void clearRatingList() {
        ratingArrayList.clear();
    }

    @Override
    public void onFragmentResume() {
        if (!registeredRatingService) {
            dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.Rating, this, fragmentView.getFragmentTag());
            registeredRatingService = true;
        }
        dataRepo.queryDatabase(DataRepoImpl.queryPath.Rating, Constants.courseSelected.code,
                fragmentView.getFragmentTag(), liveClass.id);
    }

    @Override
    public void onFragmentPause() {
        if (registeredRatingService) {
            dataRepo.unregisterCallBack(fragmentView.getFragmentTag());
            registeredRatingService = false;
        }
    }

}
