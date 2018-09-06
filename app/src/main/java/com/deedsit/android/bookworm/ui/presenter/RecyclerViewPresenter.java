package com.deedsit.android.bookworm.ui.presenter;

import com.deedsit.android.bookworm.services.ServiceCallback;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRecyclerViewRowClickedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRowAddedListener;

/**
 * Created by Jack on 1/31/2018.
 */

public interface RecyclerViewPresenter extends ServiceCallback, OnFragmentStateChangeListener, OnRowAddedListener,
                                                OnRecyclerViewRowClickedListener {
    void registerCallBackListener();
    void processDataChanged();
}
