package com.deedsit.android.bookworm.ui.interfacelisteners;

import com.deedsit.android.bookworm.models.CourseClass;

import java.io.Serializable;

/**
 * Created by Jack on 11/28/2017.
 */

public interface OnClassStopListener extends Serializable{
    void onClassStopListener(CourseClass courseClass);
}
