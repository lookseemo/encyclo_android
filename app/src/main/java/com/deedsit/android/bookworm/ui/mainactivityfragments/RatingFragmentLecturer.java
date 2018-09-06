package com.deedsit.android.bookworm.ui.mainactivityfragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.factory.AbstractPresenterFactory;
import com.deedsit.android.bookworm.ui.interfacelisteners.GraphButtonClicked;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.base.GraphFragmentView;
import com.deedsit.android.bookworm.ui.component.JTextView;
import com.deedsit.android.bookworm.ui.component.JTextViewClickListener;
import com.deedsit.android.bookworm.ui.dialog.MAlertDialog;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.util.GraphHandler;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import learn.self.aus.com.graphchartmodule.LineGraph;
import learn.self.aus.com.graphchartmodule.LinePoint;

import static com.deedsit.android.bookworm.Constants.liveClass;


public class RatingFragmentLecturer extends Fragment implements GraphFragmentView,
        JTextViewClickListener, GraphButtonClicked {

    //TODO: implement the building presenter inside this fragment instead of in navigation
    //TODO: controller

    public static final String TAG = "RATING_FRAGMENT";
    @BindView(R.id.stop_button)
    Button stopButton;
    @BindView(R.id.graph_view)
    LineGraph lineChart;
    @BindView(R.id.page_graph_layout)
    RelativeLayout pageGraphLayout;
    @BindView(R.id.paused_text)
    TextView pausedText;
    @BindView(R.id.duration_tv)
    TextView durationTV;
    @BindView(R.id.liked_text_tv)
    TextView likedTV;
    @BindView(R.id.disliked_text_tv)
    TextView dislikedTV;

    @Inject
    DataRepo dataRepo;
    private GraphButtonClicked graphButtonClicked;
    private OnFragmentStateChangeListener onFragmentStateChangeListener;
    private ArrayList<JTextView> alertDialogButtonSet;
    private int alertDialogLayout;
    private MAlertDialog alertDialog;
    private GraphHandler handler;
    private FragmentView masterFragment;
    private double roundedPosPercentage;
    private double roundedNegPercentage;
    private boolean classEnded = false;

    public RatingFragmentLecturer() {
        // Required empty public constructor
    }

    public static RatingFragmentLecturer newInstance() {
        RatingFragmentLecturer ratingFragmentLecturer = new RatingFragmentLecturer();
        return ratingFragmentLecturer;
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
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        ButterKnife.bind(this, view);
        ((AppCompatActivity) masterFragment.getFragmentActivity()).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.onFragmentStateChangeListener != null)
            onFragmentStateChangeListener.onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(this.onFragmentStateChangeListener != null)
            onFragmentStateChangeListener.onFragmentPause();
    }

    @OnClick(R.id.stop_button)
    void onFragmentButtonClicked() {
        displayAlertDialog();
    }

    @Override
    public void onTextViewClicked(View view) {
        int id = -1;
        for (JTextView button : alertDialogButtonSet) {
            if (button.getId() == view.getId())
                id = button.getId();
        }

        switch (id) {
            case R.id.al_cancel:
                alertDialog.dismiss();
                break;
            case R.id.al_accept:
                alertDialog.dismiss();
                graphButtonClicked.onEndClassButtonClicked(roundedPosPercentage);
                masterFragment.getParentFragmentManager().popBackStack();
                break;
        }
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

    }

    @Override
    public Activity getFragmentActivity() {
        return masterFragment.getFragmentActivity();
    }

    @Override
    public FragmentManager getParentFragmentManager() {
        return masterFragment.getParentFragmentManager();
    }

    @Override
    public void UpdateClock(String second, String minute, String hour) {
        updateDisplayTimer(second, minute, hour);
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
        lineChart.appendData(points);
        //update percentage
        roundedPosPercentage = Math.round(posLinePoint.value * 100.0) /100.0;
        roundedNegPercentage = Math.round(negLinePoint.value * 100.0) /100.0;
        likedTV.setText(roundedPosPercentage+"%");
        dislikedTV.setText(roundedNegPercentage+"%");
    }

    public void setGraphButtonClicked(GraphButtonClicked graphButtonClicked) {
        this.graphButtonClicked = graphButtonClicked;
    }

    private void updateDisplayTimer(final String second, final String minute, final String hour) {
        if (hour.equalsIgnoreCase("0"))
            durationTV.setText(minute + ":" + second);
        else
            durationTV.setText(hour + ":" + minute + ":" + second);
    }

    private void displayAlertDialog() {
        //get the layout
        this.alertDialogLayout = R.layout.end_class_dialog_layout;
        //configure buttons
        this.alertDialogButtonSet = new ArrayList<>();
        JTextView buttonCancel = new JTextView(this.getFragmentActivity(), R.id.al_cancel, this);
        JTextView buttonAccept = new JTextView(this.getFragmentActivity(), R.id.al_accept, this);
        buttonCancel.setVisibility(View.VISIBLE);
        buttonAccept.setVisibility(View.VISIBLE);
        buttonAccept.setText(R.string.end_class_now_dialog);
        buttonCancel.setText(R.string.cancel_dialog_label);
        alertDialogButtonSet.add(buttonAccept);
        alertDialogButtonSet.add(buttonCancel);
        alertDialog = MAlertDialog.newInstance(getString(R.string.end_class_al_title), getString
                (R.string.end_class_al_msg), this
                .alertDialogLayout, this.alertDialogButtonSet);
        alertDialog.show(this.getChildFragmentManager(), MAlertDialog.TAG);
    }

    @Override
    public void onFabButtonClicked(boolean paused) {
        if (paused) {
            pausedText.setVisibility(View.VISIBLE);
            stopButton.setClickable(false);
            graphButtonClicked.onFabButtonClicked(paused);
        } else {
            pageGraphLayout.setBackgroundColor(ContextCompat.getColor(getFragmentActivity(), R.color
                    .background_rating_color));
            pausedText.setVisibility(View.GONE);
            stopButton.setClickable(true);
            graphButtonClicked.onFabButtonClicked(paused);
        }
    }

    @Override
    public void onEndClassButtonClicked(double averageRating) {
        // leave it blank
    }

    @Override
    public void onRatingButtonClicked(boolean isLikeBtn) {
        //blank for lecturer
    }

    public GraphHandler getHandler() {
        if (handler == null)
            this.handler = new GraphHandler(this);
        return handler;
    }

    public FragmentView getMasterFragment() {
        return masterFragment;
    }

    public void setMasterFragment(FragmentView masterFragment) {
        this.masterFragment = masterFragment;
    }

    public void setOnFragmentStateChangeListener(OnFragmentStateChangeListener
                                                         onFragmentStateChangeListener){
        this.onFragmentStateChangeListener = onFragmentStateChangeListener;
    }
}
