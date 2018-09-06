package com.deedsit.android.bookworm.ui.interfacelisteners;

/**
 * Created by Jack on 3/6/2018.
 */

public interface GraphButtonClicked {
    void onFabButtonClicked(boolean paused);
    void onEndClassButtonClicked(double averageRating);
    void onRatingButtonClicked(boolean isLikeBtn);
}
