package com.deedsit.android.bookworm.arch.modules;

import android.app.Application;

import com.deedsit.android.bookworm.config.AppConfig;
import com.deedsit.android.bookworm.config.impl.AppConfigImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by steph on 11/11/2017.
 */
@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    AppConfig providesAppConfig(AppConfigImpl appConfig) {
        return appConfig;
    }
}
