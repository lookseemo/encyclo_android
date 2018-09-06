package com.deedsit.android.bookworm.ui.interfacelisteners;

import com.deedsit.android.bookworm.models.Course;

/**
 * Created by Jack on 11/26/2017.
 */

public interface OnCourseClickListener extends OnDataClickedListener {
    void onCourseClicked(Course course);
}
