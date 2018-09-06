package com.deedsit.android.bookworm.ui.interfacelisteners;

import com.deedsit.android.bookworm.models.CourseClass;

/**
 * Created by Jack on 11/26/2017.
 */

public interface OnClassAddedListener extends OnDataAddedListener {
    void onClassAdded(CourseClass courseClass);
}
