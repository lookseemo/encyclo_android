package com.deedsit.android.bookworm.arch.modules;

import com.deedsit.android.bookworm.security.SessionManager;
import com.deedsit.android.bookworm.security.SessionManagerImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by steph on 11/11/2017.
 */
@Module
public class SecurityModule {

    @Provides
    @Singleton
    SessionManager providesSessionManager(SessionManagerImpl sessionMananger) {
        return sessionMananger;
    }
}
