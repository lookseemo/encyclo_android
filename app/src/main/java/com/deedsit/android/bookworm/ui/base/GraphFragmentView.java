package com.deedsit.android.bookworm.ui.base;

import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;

import java.util.ArrayList;

/**
 * Created by Jack on 3/6/2018.
 */

public interface GraphFragmentView extends FragmentView {
    void UpdateClock(String second, String minute, String hour);
    void UpdateGraph(ArrayList<Rating> ratingData);

}
