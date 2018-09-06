package com.deedsit.android.bookworm.ui.base;

import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRecyclerViewRowClickedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRowAddedListener;

import java.util.ArrayList;

/**
 * Created by Jack on 1/31/2018.
 */

public interface FragmentRecyclerView extends FragmentView{
    void displayData(ArrayList adapterList);
    void rowAdded(Object object);
    void rowClicked(Object object);
    void clearDisplayData();
    void setOnRowClickedListener(OnRecyclerViewRowClickedListener onRowClickedListener);
    void setOnFragmentStateChangeListener(OnFragmentStateChangeListener
                                                onFragmentStateChangeListener);
    void setOnRowAddedListener(OnRowAddedListener onRowAddedListener);
}
