package com.deedsit.android.bookworm.ui.mainactivityfragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
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
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.ListItem;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.ui.adapter.CustomRecyclerViewAdapter;
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView;
import com.deedsit.android.bookworm.ui.base.FragmentView;
import com.deedsit.android.bookworm.ui.dialog.ErrorDialog;
import com.deedsit.android.bookworm.ui.dialog.InputDialog;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnClassAddedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnClassClickedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRecyclerViewRowClickedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRowAddedListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.deedsit.android.bookworm.Constants.SAVED_FRAGMENT_TAG;
import static com.deedsit.android.bookworm.Constants.courseSelected;


public class ViewClassesFragment extends Fragment implements OnClassAddedListener,
        OnClassClickedListener, FragmentRecyclerView {

    //TODO: implement the building presenter inside this fragment instead of in navigation
    //TODO: controller

    @Inject
    DataRepo dataRepo;
    @Inject
    AuthRepo authRepo;

    private CustomRecyclerViewAdapter customRecyclerViewAdapter;
    private ArrayList<ListItem> adapterList;

    public static final String TAG = "VIEW_CLASSES_FRAGMENT";

    @BindView(R.id.recycler_view_classes)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.no_classes_tv)
    TextView noClassTV;

    private OnRecyclerViewRowClickedListener onRecyclerViewRowClickedListener;
    private OnRowAddedListener onRowAddedListener;
    private OnFragmentStateChangeListener onFragmentStateChangeListener;
    private MasterHomeFragment masterHomeFragment;
    public ViewClassesFragment() {
        // Required empty public constructor
    }

    public static ViewClassesFragment newInstance() {
        ViewClassesFragment fragment = new ViewClassesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);
        adapterList = new ArrayList<>();
        ImageView logo = getFragmentActivity().findViewById(R.id
                .logo_toolbar_iv);
        logo.setVisibility(View.GONE);
        ActionBar toolbar = ((AppCompatActivity) getFragmentActivity())
                .getSupportActionBar();
        toolbar.setDisplayShowTitleEnabled(true);
        toolbar.setDisplayShowCustomEnabled(false);
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
        toolbar.setHomeButtonEnabled(true);

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_classes, container, false);
        ButterKnife.bind(this, view);
        //enable action bar
        ((AppCompatActivity)masterHomeFragment.getFragmentActivity()).getSupportActionBar().show();
        recyclerView.setLayoutManager(new LinearLayoutManager(getFragmentActivity()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);

        this.customRecyclerViewAdapter = new CustomRecyclerViewAdapter(this, adapterList);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(customRecyclerViewAdapter);
        buildPresenter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(customRecyclerViewAdapter == null)
            this.customRecyclerViewAdapter = new CustomRecyclerViewAdapter(this, adapterList);
        if(onFragmentStateChangeListener != null){
            onFragmentStateChangeListener.onFragmentResume();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        customRecyclerViewAdapter = null;
        recyclerView = null;
        if(onFragmentStateChangeListener != null){
            onFragmentStateChangeListener.onFragmentPause();
        }
    }

    @OnClick(R.id.fab)
    void onButtonClicked() {
        InputDialog inputDialog = InputDialog.newInstance(this, TAG);
        inputDialog.show(getChildFragmentManager(), InputDialog.TAG);
    }

    @Override
    public void onClassAdded(final CourseClass courseClass) {
        rowAdded(courseClass);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void buildPresenter() {
        AbstractPresenterFactory.buildPresenterForFragment(this, ((BookWormApplication)
                this.getFragmentActivity().getApplication()).getComponent(), courseSelected);
    }

    @Override
    public void displayErrorMeesage(int errorCode) {
        ErrorDialog alertDialog = null;
        switch(errorCode){
            case Constants.CREATE_CLASS_ERR:
                alertDialog = ErrorDialog.newInstance(getString(R.string
                        .cant_create_class_err_dialog_title), getString(R.string.create_class_err_message));
                break;
            case Constants.LOAD_CLASS_ERR:
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
        return masterHomeFragment.getParentFragmentManager();
    }

    @Override
    public void displayData(ArrayList adapterList) {
        this.adapterList.clear();
        this.adapterList.addAll(adapterList);
        customRecyclerViewAdapter.swap(this.adapterList);
        noClassTV.setVisibility(View.GONE);
        masterHomeFragment.hideProgressDialog();
    }

    @Override
    public void rowAdded(Object object) {
        if(onRowAddedListener != null)
            onRowAddedListener.onRowAddedListener(object);
    }

    @Override
    public void rowClicked(Object object) {
        if(onRecyclerViewRowClickedListener != null) {
            onRecyclerViewRowClickedListener.OnRecyclerViewRowClicked(object);
        }
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

    @Override
    public void onClassClicked(CourseClass courseClass) {
        rowClicked(courseClass);
    }

    public void setMasterHomeFragment(MasterHomeFragment masterHomeFragment) {
        this.masterHomeFragment = masterHomeFragment;
    }

    public MasterHomeFragment getMasterHomeFragment() {
        return masterHomeFragment;
    }
}
