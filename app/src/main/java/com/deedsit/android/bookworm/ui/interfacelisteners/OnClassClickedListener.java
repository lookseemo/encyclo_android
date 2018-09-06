package com.deedsit.android.bookworm.ui.interfacelisteners;

import com.deedsit.android.bookworm.models.CourseClass;

/**
 * Created by Jack on 12/17/2017.
 */

public interface OnClassClickedListener extends OnDataClickedListener {
    void onClassClicked(CourseClass courseClass);
}
