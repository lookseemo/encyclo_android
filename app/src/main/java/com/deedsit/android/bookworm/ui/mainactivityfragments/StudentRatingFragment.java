package com.deedsit.android.bookworm.ui.mainactivityfragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.factory.AbstractPresenterFactory;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.component.JTextView;
import com.deedsit.android.bookworm.ui.component.JTextViewClickListener;
import com.deedsit.android.bookworm.ui.dialog.MAlertDialog;
import com.deedsit.android.bookworm.ui.interfacelisteners.GraphButtonClicked;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.ui.base.GraphFragmentView;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnClassStatusChanged;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.util.GraphHandler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import learn.self.aus.com.graphchartmodule.LineGraph;
import learn.self.aus.com.graphchartmodule.LinePoint;

import static com.deedsit.android.bookworm.Constants.CLASS_ENDED;
import static com.deedsit.android.bookworm.Constants.hour;
import static com.deedsit.android.bookworm.Constants.liveClass;
import static com.deedsit.android.bookworm.Constants.masterListSize;
import static com.deedsit.android.bookworm.Constants.masterRatingList;
import static com.deedsit.android.bookworm.Constants.minute;
import static com.deedsit.android.bookworm.Constants.second;

public class StudentRatingFragment extends Fragment implements GraphFragmentView,
        OnClassStatusChanged,JTextViewClickListener {

    //TODO: implement the building presenter inside this fragment instead of in navigation
    //TODO: controller

    @BindView(R.id.duration_tv)
    TextView durationTV;
    @BindView(R.id.liked_text_tv)
    TextView likedTV;
    @BindView(R.id.disliked_text_tv)
    TextView dislikedTV;
    @BindView(R.id.disliked_button)
    ImageButton dislikedButton;
    @BindView(R.id.liked_btn)
    ImageButton likedButton;
    @BindView(R.id.graph_view)
    LineGraph lineGraph;
    @BindView(R.id.paused_text_1)
    TextView pausedTextTV1;
    @BindView(R.id.paused_text_2)
    TextView pausedTextTV2;

    public static final String TAG = "STUDENT_RATING_FRAGMENT";
    private MasterHomeFragment masterHomeFragment;
    private GraphHandler handler;
    private OnFragmentStateChangeListener fragmentStateChangeListener;
    private GraphButtonClicked onGraphButtonClicked;
    private ArrayList<JTextView> alertDialogButtonSet;
    private int alertDialogLayout;
    private MAlertDialog alertDialog;

    public StudentRatingFragment() {
        // Required empty public constructor
    }

    public static StudentRatingFragment newInstance() {
        StudentRatingFragment fragment = new StudentRatingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_rating, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (fragmentStateChangeListener != null)
            fragmentStateChangeListener.onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fragmentStateChangeListener != null)
            fragmentStateChangeListener.onFragmentPause();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void buildPresenter() {
        AbstractPresenterFactory.buildPresenterForFragment(this, ((BookWormApplication)
                this.getFragmentActivity().getApplication()).getComponent(), null);
    }

    @Override
    public void displayErrorMeesage(int errorCode) {
        switch (errorCode){
            case CLASS_ENDED:
                //get the layout
                this.alertDialogLayout = R.layout.end_class_dialog_layout;
                //configure buttons
                this.alertDialogButtonSet = new ArrayList<>();
                JTextView buttonCancelled = new JTextView(this.getFragmentActivity(), R.id
                        .al_cancel,
                        this);
                JTextView buttonAccept = new JTextView(this.getFragmentActivity(), R.id.al_accept, this);
                buttonAccept.setVisibility(View.VISIBLE);
                buttonCancelled.setVisibility(View.GONE);
                buttonAccept.setText(R.string.ok_btn_label);

                alertDialogButtonSet.add(buttonAccept);
                alertDialog = MAlertDialog.newInstance(getString(R.string.end_class_al_title), getString(R.string.class_ended_msg_dialog_student), this
                        .alertDialogLayout, this.alertDialogButtonSet);
                alertDialog.show(this.getChildFragmentManager(), MAlertDialog.TAG);
        }
    }

    @Override
    public Activity getFragmentActivity() {
        return masterHomeFragment.getFragmentActivity();
    }

    @Override
    public FragmentManager getParentFragmentManager() {
        return masterHomeFragment.getParentFragmentManager();
    }

    public void setMasterHomeFragment(MasterHomeFragment masterHomeFragment) {
        this.masterHomeFragment = masterHomeFragment;
    }

    public void setOnFragmentStateChangeListener(OnFragmentStateChangeListener
                                                         onFragmentStateChangeListener) {
        this.fragmentStateChangeListener = onFragmentStateChangeListener;
    }

    @Override
    public void UpdateClock(String second, String minute, String hour) {
        if (hour.equalsIgnoreCase("0"))
            durationTV.setText(minute + ":" + second);
        else
            durationTV.setText(hour + ":" + minute + ":" + second);
    }

    @Override
    public void UpdateGraph(ArrayList<Rating> ratingData) {
        LinePoint posLinePoint = new LinePoint();
        posLinePoint.value = ratingData.get(0).value;
        LinePoint negLinePoint = new LinePoint();
        negLinePoint.value = ratingData.get(1).value;
        ArrayList<LinePoint> points = new ArrayList();
        points.add(posLinePoint);
        points.add(negLinePoint);
        lineGraph.appendData(points);
        //update percentage
        double roundedPosPercentage = Math.round(posLinePoint.value * 100.0) /100.0;
        double roundedNegPercentage = Math.round(negLinePoint.value * 100.0) /100.0;
        likedTV.setText(roundedPosPercentage+"%");
        dislikedTV.setText(roundedNegPercentage+"%");
    }

    @Override
    public void classStatusChanged(CourseClass.classStatusInd statusInd) {
        if (statusInd == CourseClass.classStatusInd.ENDED)
            endClass();
        else if (statusInd == CourseClass.classStatusInd.PAUSED)
            pauseClass();
        else
            resumeClass();
    }

    @OnClick({R.id.disliked_button, R.id.liked_btn})
    void onClicked(View view) {
        boolean isLiked = false;
        if (view.getId() == R.id.liked_btn)
            isLiked = true;

        likedButton.setEnabled(false);
        dislikedButton.setEnabled(false);

        likedButton.setBackgroundTintList(ContextCompat.getColorStateList(this.getFragmentActivity(), R.color
                .ic_unselected_color));
        dislikedButton.setBackgroundTintList(ContextCompat.getColorStateList(this
                .getFragmentActivity(), R.color
                .ic_unselected_color));

        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                likedButton.setBackgroundTintList(ContextCompat.getColorStateList
                        (masterHomeFragment.getFragmentActivity(), R.color.pos_line_fill_90));
                dislikedButton.setBackgroundTintList(ContextCompat.getColorStateList(masterHomeFragment
                        .getFragmentActivity(), R.color.neg_line_fill));

                dislikedButton.setEnabled(true);
                likedButton.setEnabled(true);

            }
        };

        Handler handler = new Handler();
        handler.postDelayed(progressRunnable,3000);

        if (onGraphButtonClicked != null)
            onGraphButtonClicked.onRatingButtonClicked(isLiked);
    }


    public GraphHandler getHandler() {
        if (this.handler == null)
            this.handler = new GraphHandler(this);
        return this.handler;
    }

    public void setOnGraphButtonClicked(GraphButtonClicked onGraphButtonClicked) {
        this.onGraphButtonClicked = onGraphButtonClicked;
    }

    private void pauseClass() {
        Constants.isPaused = true;
        pausedTextTV1.setVisibility(View.VISIBLE);
        pausedTextTV2.setVisibility(View.VISIBLE);
        likedButton.setClickable(false);
        dislikedButton.setClickable(false);
    }

    private void resumeClass() {
        Constants.isPaused = false;
        pausedTextTV1.setVisibility(View.GONE);
        pausedTextTV2.setVisibility(View.GONE);
        likedButton.setClickable(true);
        dislikedButton.setClickable(true);
    }

    private void endClass() {
        Constants.liveClass =  null;
        Constants.courseSelected = null;
        Constants.minute = 0;
        Constants.second = 0;
        Constants.hour = 0;
        Constants.isLive = false;
        Constants.masterListSize = 0;
        Constants.masterRatingList.clear();
        displayErrorMeesage(CLASS_ENDED);
    }

    @Override
    public void onTextViewClicked(View view) {
        switch(view.getId()){
            case R.id.al_accept:
                alertDialog.dismiss();
                masterHomeFragment.getParentFragmentManager().popBackStack();
                break;
        }
    }
}

