package com.deedsit.android.bookworm.ui.base;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import java.io.Serializable;

public interface FragmentView extends Serializable{
    String getFragmentTag();
    void buildPresenter();
    void displayErrorMeesage(int errorCode);
    Activity getFragmentActivity();
    FragmentManager getParentFragmentManager();
}
