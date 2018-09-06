package com.deedsit.android.bookworm.ui.interfacelisteners;

import com.deedsit.android.bookworm.models.CourseClass;

/**
 * Created by Jack on 3/29/2018.
 */

public interface OnClassStatusChanged {
    void classStatusChanged(CourseClass.classStatusInd statusInd);
}
