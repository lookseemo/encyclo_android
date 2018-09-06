package com.deedsit.android.bookworm.ui.presenter;

import com.deedsit.android.bookworm.models.Rating;

import java.util.ArrayList;

/**
 * Created by Jack on 3/29/2018.
 */

public interface GraphPresenter {
    ArrayList<Rating> getRatingList();
    void clearRatingList();
}
