package com.deedsit.android.bookworm.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.ui.presenter.GraphPresenter;
import com.deedsit.android.bookworm.ui.presenter.ServicePresenter;

import java.util.ArrayList;
import java.util.TimerTask;

import static com.deedsit.android.bookworm.Constants.CLOCK_HOUR;
import static com.deedsit.android.bookworm.Constants.CLOCK_MINUTE;
import static com.deedsit.android.bookworm.Constants.CLOCK_SECOND;
import static com.deedsit.android.bookworm.Constants.GRAPH_DATA;
import static com.deedsit.android.bookworm.Constants.UPDATE_CLOCK_MSG;
import static com.deedsit.android.bookworm.Constants.UPDATE_GRAPH_MSG;
import static com.deedsit.android.bookworm.Constants.isPaused;
import static com.deedsit.android.bookworm.Constants.masterRatingList;

/**
 * Created by Jack on 3/8/2018.
 */

public class CustomGraphTask extends TimerTask {
    private Handler handler;
    private ServicePresenter servicePresenter;
    private int graphInterval = 0, second = 0, minute = 0, hour = 0;

    public CustomGraphTask(Handler handler, ServicePresenter servicePresenter) {
        this.handler = handler;
        this.servicePresenter = servicePresenter;
        second = Constants.second;
        minute = Constants.minute;
        hour = Constants.hour;
    }

    @Override
    public void run() {
        if (!isPaused) {
            graphInterval++;
            second++;
            if (second == 60) {
                second = 0;
                minute++;
            }
            if (minute == 60) {
                minute = 0;
                hour++;
            }
            Constants.second = second;
            Constants.minute = minute;
            Constants.hour = hour;
            if (handler != null) {
                String secondStr, minuteStr, hourStr;
                if (second < 10)
                    secondStr = "0" + second;
                else
                    secondStr = "" + second;
                if (minute < 10)
                    minuteStr = "0" + minute;
                else
                    minuteStr = "" + minute;
                hourStr = "" + hour;
                //build message
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString(CLOCK_SECOND, secondStr);
                bundle.putString(CLOCK_MINUTE, minuteStr);
                bundle.putString(CLOCK_HOUR, hourStr);
                message.setData(bundle);
                message.what = UPDATE_CLOCK_MSG;
                handler.sendMessage(message);
                if (graphInterval == 3) {
                    graphInterval = 0;
                    if (servicePresenter != null) {
                        if (masterRatingList != null && Constants.masterListSize != masterRatingList.size()) {
                            Constants.masterListSize = Constants.masterRatingList.size();
                            ((GraphPresenter) servicePresenter).clearRatingList();
                            servicePresenter.processData();
                            if (servicePresenter instanceof GraphPresenter) {
                                ArrayList<Rating> ratingList = ((GraphPresenter) servicePresenter)
                                        .getRatingList();
                                if (ratingList.size() > 0) {
                                    message = handler.obtainMessage();
                                    bundle = new Bundle();
                                    bundle.putSerializable(GRAPH_DATA, ratingList);
                                    message.setData(bundle);
                                    message.what = UPDATE_GRAPH_MSG;
                                    handler.sendMessage(message);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
