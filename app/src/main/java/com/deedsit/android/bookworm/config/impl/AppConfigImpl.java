package com.deedsit.android.bookworm.config.impl;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.deedsit.android.bookworm.config.AppConfig;

import javax.inject.Inject;

/**
 * Created by steph on 12/11/2017.
 */

public class AppConfigImpl implements AppConfig {

    @NonNull
    private Context context;

    @Inject
    public AppConfigImpl (@NonNull Application context) {
        this.context = context;
    }

    @Override
    public boolean hasNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
