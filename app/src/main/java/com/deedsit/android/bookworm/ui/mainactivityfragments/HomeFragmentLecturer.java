package com.deedsit.android.bookworm.ui.mainactivityfragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.factory.AbstractPresenterFactory;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.ListItem;
import com.deedsit.android.bookworm.ui.MainActivity;
import com.deedsit.android.bookworm.ui.adapter.CustomRecyclerViewAdapter;
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.dialog.ErrorDialog;
import com.deedsit.android.bookworm.ui.dialog.InputDialog;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnCourseAddedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnCourseClickListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRecyclerViewRowClickedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRowAddedListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.deedsit.android.bookworm.Constants.SAVED_FRAGMENT_TAG;

public class HomeFragmentLecturer extends Fragment implements OnCourseAddedListener,
        OnCourseClickListener, FragmentRecyclerView {

    //TODO: implement the building presenter inside this fragment instead of in navigation
    //TODO: controller

    public static final String TAG = "HOME_FRAGMENT";

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.recycler_main_view)
    RecyclerView recyclerView;
    @BindView(R.id.total_number_courses)
    TextView totalCoursesTV;
    @BindView(R.id.no_courses_tv)
    TextView noCoursesTV;

    private ArrayList<ListItem> adapterList;
    private CustomRecyclerViewAdapter customRecyclerViewAdapter;
    private OnRecyclerViewRowClickedListener onRecyclerViewRowClickedListener;
    private OnFragmentStateChangeListener onFragmentStateChangeListener;
    private OnRowAddedListener onRowAddedListener;
    private MasterHomeFragment masterHomeFragment;

    public HomeFragmentLecturer() {
        // Required empty public constructor
    }

    public static HomeFragmentLecturer newInstance() {
        return new HomeFragmentLecturer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapterList = new ArrayList<>();
        // dialog
        masterHomeFragment.displayProgressBar("Loading User Data");
        //timer for dialog
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                masterHomeFragment.hideProgressDialog();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(progressRunnable,5000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_lecturer, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Adapter
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        customRecyclerViewAdapter = new CustomRecyclerViewAdapter(this, adapterList);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(customRecyclerViewAdapter);
        updateTotalCoursesText();
        buildPresenter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ImageView logo = getFragmentActivity().findViewById(R.id
                .logo_toolbar_iv);
        logo.setVisibility(View.VISIBLE);
        TextView toolBarTitle = getFragmentActivity().findViewById(R.id
                .toolbar_title);
        TextView toolBarSubTitle = getFragmentActivity().findViewById(R.id
                .toolbar_sub_title);
        toolBarTitle.setVisibility(View.GONE);
        toolBarSubTitle.setVisibility(View.GONE);

        ((AppCompatActivity)getFragmentActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((AppCompatActivity)getFragmentActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getFragmentActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getFragmentActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        if (onFragmentStateChangeListener != null)
            onFragmentStateChangeListener.onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (onFragmentStateChangeListener != null)
            onFragmentStateChangeListener.onFragmentPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCourseAddedListener(final Course course) {
        rowAdded(course);
    }

    @Override
    public void onCourseClicked(Course course) {
        rowClicked(course);
    }

    @OnClick(R.id.fab)
    void onFabClicked() {
        InputDialog inputDialog = InputDialog.newInstance(this, HomeFragmentLecturer.TAG);
        inputDialog.show(getChildFragmentManager(), InputDialog.TAG);
    }

    private void updateTotalCoursesText() {
        String courseString = (this.adapterList.size() > 1) || (this.adapterList.size() == 0) ?
                this.adapterList.size() + " courses" :
                this.adapterList
                .size() + " course";
        totalCoursesTV.setText(courseString);
    }

    @Override
    public void displayData(ArrayList adapterList) {
        clearDisplayData();
        this.adapterList.addAll(adapterList);
        customRecyclerViewAdapter.swap(adapterList);
        updateTotalCoursesText();
        noCoursesTV.setVisibility(View.GONE);
        masterHomeFragment.hideProgressDialog();
    }

    @Override
    public void rowAdded(Object object) {
        if (onRowAddedListener != null)
            onRowAddedListener.onRowAddedListener(object);
    }

    @Override
    public void rowClicked(Object object) {
        if (onRecyclerViewRowClickedListener != null)
            onRecyclerViewRowClickedListener.OnRecyclerViewRowClicked(object);
    }

    @Override
    public void clearDisplayData() {
        this.adapterList.clear();
        customRecyclerViewAdapter.swap(adapterList);
    }

    @Override
    public void setOnRowClickedListener(OnRecyclerViewRowClickedListener onRowClickedListener) {
        //set fragment to hide flag
        this.onRecyclerViewRowClickedListener = onRowClickedListener;
    }

    @Override
    public void setOnFragmentStateChangeListener(OnFragmentStateChangeListener onFragmentStateChangeListener) {
        this.onFragmentStateChangeListener = onFragmentStateChangeListener;
    }

    @Override
    public void setOnRowAddedListener(OnRowAddedListener onRowAddedListener) {
        this.onRowAddedListener = onRowAddedListener;
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
    public void displayErrorMeesage(int erroCode) {
        ErrorDialog alertDialog = null;
        switch (erroCode) {
            case Constants.CREATE_COURSE_ERR:
                alertDialog = ErrorDialog.newInstance(getResources().getString(R.string
                        .cannot_create_course_title), getResources().getString(R.string
                        .cannot_create_course_err_message));
                break;
        }
        alertDialog.show(getChildFragmentManager(), TAG);
    }

    @Override
    public Activity getFragmentActivity() {
        return masterHomeFragment.getActivity();
    }

    @Override
    public FragmentManager getParentFragmentManager() {
        return masterHomeFragment.getChildFragmentManager();
    }

    public void setMasterHomeFragment(MasterHomeFragment masterHomeFragment){
        this.masterHomeFragment = masterHomeFragment;
    }

    public MasterHomeFragment getMasterHomeFragment(){
        return masterHomeFragment;
    }
}
