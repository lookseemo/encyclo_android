package com.deedsit.android.bookworm.arch.modules;

import com.deedsit.android.bookworm.security.SessionManager;
import com.deedsit.android.bookworm.security.SessionManagerImpl;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.services.impl.AuthRepoImpl;
import com.deedsit.android.bookworm.services.impl.DataRepoImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by steph on 11/11/2017.
 */
@Module
public class ServiceModule {

    @Provides
    @Singleton
    AuthRepo provideAuthRepo(AuthRepoImpl authRepo) {
        return authRepo;
    }

    @Provides
    @Singleton
    DataRepo provideDataRepo(DataRepoImpl dataRepo) {
        return dataRepo;
    }
}
