package com.deedsit.android.bookworm.factory;

import com.deedsit.android.bookworm.arch.AppGraph;
import com.deedsit.android.bookworm.ui.base.FragmentView;

/**
 * Created by Jack on 1/31/2018.
 */

public abstract class AbstractPresenterFactory {
    private static AbstractPresenterFactory presenterFactory;
    public static void buildPresenterForFragment(FragmentView fragmentView, AppGraph appGraph,
                                                 Object data){
        if(presenterFactory == null)
            presenterFactory = new FactoryPresenter();
        presenterFactory.buildPresenter(fragmentView,appGraph,data);
    }
    public abstract void buildPresenter(FragmentView fragmentView, AppGraph appGraph, Object data);
}
