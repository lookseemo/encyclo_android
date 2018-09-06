package com.deedsit.android.bookworm.security;

import android.app.Application;
import android.support.annotation.NonNull;

import com.deedsit.android.bookworm.config.AppConfig;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.DataRepo;

import javax.inject.Inject;

/**
 * Created by steph on 11/11/2017.
 */

public class SessionManagerImpl implements SessionManager {
    @NonNull
    private AppConfig appConfig;

    @NonNull
    private AuthRepo authRepo;

    @NonNull
    private DataRepo dataRepo;

    @Inject
    public SessionManagerImpl(@NonNull AppConfig appConfig,
                              @NonNull AuthRepo authRepo,
                              @NonNull DataRepo dataRepo) {
        this.appConfig = appConfig;
        this.authRepo = authRepo;
        this.dataRepo = dataRepo;
    }

    @Override
    public boolean isLoggedIn() {
        return authRepo.getCurrentUser() != null;
    }
}
