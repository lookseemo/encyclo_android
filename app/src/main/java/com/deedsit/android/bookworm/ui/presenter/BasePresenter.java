package com.deedsit.android.bookworm.ui.presenter;

import com.deedsit.android.bookworm.arch.AppGraph;
import com.deedsit.android.bookworm.ui.base.FragmentView;

/**
 * Created by Jack on 3/6/2018.
 */

public abstract class BasePresenter {

    public BasePresenter(FragmentView fragmentView, AppGraph appGraph){}
    public abstract String getPresenterTag();

}
