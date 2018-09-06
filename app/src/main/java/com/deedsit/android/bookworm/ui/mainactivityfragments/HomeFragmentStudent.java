package com.deedsit.android.bookworm.ui.mainactivityfragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.factory.AbstractPresenterFactory;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.ListItem;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.ui.adapter.CustomRecyclerViewAdapter;
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.component.JTextView;
import com.deedsit.android.bookworm.ui.component.JTextViewClickListener;
import com.deedsit.android.bookworm.ui.dialog.InputDialog;
import com.deedsit.android.bookworm.ui.dialog.MAlertDialog;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnCourseAddedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnCourseClickListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRecyclerViewRowClickedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRowAddedListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.deedsit.android.bookworm.Constants.COURSE_DUP;
import static com.deedsit.android.bookworm.Constants.LOAD_CLASS_ERR;

public class HomeFragmentStudent extends Fragment implements FragmentRecyclerView,
        JTextViewClickListener, OnCourseAddedListener, OnCourseClickListener {
    //TODO: implement the building presenter inside this fragment instead of in navigation
    //TODO: controller

    @BindView(R.id.recycler_main_view)
    RecyclerView recyclerView;
    @BindView(R.id.total_number_courses)
    TextView totalCoursesTV;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.no_courses_tv)
    TextView noCoursesTV;

    public static final String TAG = "HOME_FRAGMENT_STUDENT";

    private ArrayList<ListItem> adapterList;
    private CustomRecyclerViewAdapter customRecyclerViewAdapter;
    private OnRecyclerViewRowClickedListener onRecyclerViewRowClickedListener;
    private OnFragmentStateChangeListener onFragmentStateChangeListener;
    private OnRowAddedListener onRowAddedListener;
    private int alertDialogLayout;
    private MAlertDialog alertDialog;
    private ArrayList<JTextView> alertDialogButtonSet;
    private MasterHomeFragment masterFragment;

    @Inject
    DataRepo dataRepo;
    @Inject
    AuthRepo authRepo;

    public HomeFragmentStudent() {
        // Required empty public constructor
    }

    public static HomeFragmentStudent newInstance() {
        return new HomeFragmentStudent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildPresenter();
        ((BookWormApplication) getActivity().getApplication()).getComponent().inject(this);
        masterFragment.displayProgressBar("Loading Data...");
        //timer for dialog
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                masterFragment.hideProgressDialog();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(progressRunnable,5000);
        //get data
        if (Constants.courseList == null)
            Constants.courseList = new ArrayList<>();
        adapterList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_fragment_student, container, false);
        ButterKnife.bind(this, view);
        updateTotalCoursesText();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        customRecyclerViewAdapter = new CustomRecyclerViewAdapter
                (this, adapterList);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(customRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.onFragmentStateChangeListener != null)
            this.onFragmentStateChangeListener.onFragmentPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.onFragmentStateChangeListener != null)
            this.onFragmentStateChangeListener.onFragmentResume();
        Constants.masterListSize = 0;
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
        this.alertDialogLayout = R.layout.end_class_dialog_layout;
        this.alertDialogButtonSet = new ArrayList<>();
        JTextView buttonCancel = new JTextView(this.getFragmentActivity(), R.id.al_cancel, this);
        JTextView buttonAccept = new JTextView(this.getFragmentActivity(), R.id.al_accept, this);
        buttonCancel.setVisibility(View.GONE);
        buttonAccept.setText(R.string.ok_btn_label);
        alertDialogButtonSet.add(buttonAccept);
        alertDialogButtonSet.add(buttonCancel);
        switch (errorCode) {
            case LOAD_CLASS_ERR:
                //get the layout
                alertDialog = MAlertDialog.newInstance(getString(R.string.no_class_live_title),
                        getString(R.string.no_class_live_msg) +
                                "later", this
                                .alertDialogLayout, this.alertDialogButtonSet);
                alertDialog.show(this.getChildFragmentManager(), MAlertDialog.TAG);
                break;
            case COURSE_DUP:
                alertDialog = MAlertDialog.newInstance("Class Already Added", "This class " +
                        "already added in your list", this.alertDialogLayout, this.alertDialogButtonSet);
                alertDialog.show(this.getChildFragmentManager(), MAlertDialog.TAG);

        }
    }

    @Override
    public Activity getFragmentActivity() {
        return this.getActivity();
    }

    @Override
    public FragmentManager getParentFragmentManager() {
        return this.getActivity().getSupportFragmentManager();
    }

    @Override
    public void displayData(ArrayList adapterList) {
        if (adapterList.size() > 0) {
            clearDisplayData();
            this.adapterList.addAll(adapterList);
            customRecyclerViewAdapter.swap(this.adapterList);
            updateTotalCoursesText();
            masterFragment.hideProgressDialog();
            noCoursesTV.setVisibility(View.GONE);
        }
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
        this.customRecyclerViewAdapter.swap(adapterList);
    }

    @Override
    public void setOnRowClickedListener(OnRecyclerViewRowClickedListener onRowClickedListener) {
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

    @OnClick(R.id.fab)
    void onFabClicked() {
        InputDialog inputDialog = InputDialog.newInstance(this, TAG);
        inputDialog.show(getChildFragmentManager(), InputDialog.TAG);
    }


    private void updateTotalCoursesText() {
        String courseString = (adapterList.size() > 1) ? adapterList.size() + " courses" :
                adapterList.size() + " course";
        totalCoursesTV.setText(courseString);
    }

    @Override
    public void onTextViewClicked(View view) {
        switch (view.getId()) {
            case R.id.al_accept:
                this.alertDialog.dismiss();
        }
    }

    @Override
    public void onCourseAddedListener(Course course) {
        rowAdded(course);
    }

    @Override
    public void onCourseClicked(Course course) {
        rowClicked(course);
    }

    public void setMasterFragment(MasterHomeFragment masterFragment){this.masterFragment =
            masterFragment;}
    public MasterHomeFragment getMasterHomeFragment(){return this.masterFragment;}
}
