package com.deedsit.android.bookworm.ui.presenter;

import android.view.View;

import com.deedsit.android.bookworm.services.ServiceCallback;

/**
 * Created by Jack on 3/6/2018.
 */

public interface ServicePresenter extends ServiceCallback {
    void processData();
}
