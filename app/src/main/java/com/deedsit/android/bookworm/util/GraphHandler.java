package com.deedsit.android.bookworm.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.ui.MainActivity;
import com.deedsit.android.bookworm.ui.base.GraphFragmentView;
import com.deedsit.android.bookworm.ui.mainactivityfragments.MasterHomeFragment;
import com.deedsit.android.bookworm.ui.mainactivityfragments.RatingFragmentLecturer;

import java.util.ArrayList;

import static com.deedsit.android.bookworm.Constants.CLOCK_HOUR;
import static com.deedsit.android.bookworm.Constants.CLOCK_MINUTE;
import static com.deedsit.android.bookworm.Constants.CLOCK_SECOND;
import static com.deedsit.android.bookworm.Constants.GRAPH_DATA;
import static com.deedsit.android.bookworm.Constants.UPDATE_CLOCK_MSG;
import static com.deedsit.android.bookworm.Constants.UPDATE_GRAPH_MSG;

/**
 * Created by Jack on 3/8/2018.
 */

public class GraphHandler extends Handler {
    private GraphFragmentView ratingFragment;

    public GraphHandler(GraphFragmentView ratingFragment) {
        this.ratingFragment = ratingFragment;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (ratingFragment == null)
            return;
        switch (msg.what) {
            case UPDATE_GRAPH_MSG:
                ArrayList<Rating> ratings = (ArrayList<Rating>) msg.getData().get(GRAPH_DATA);
                if (ratings != null && ratings.size() > 0) {
                    ratingFragment.UpdateGraph(ratings);
                }
                break;
            case UPDATE_CLOCK_MSG:
                String second = (String) msg.getData().get(CLOCK_SECOND);
                String minute = (String) msg.getData().get(CLOCK_MINUTE);
                String hour = (String) msg.getData().get(CLOCK_HOUR);
                if (this.ratingFragment != null)
                    ratingFragment.UpdateClock(second, minute, hour);
        }
    }
}
