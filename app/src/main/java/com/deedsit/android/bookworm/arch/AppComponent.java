package com.deedsit.android.bookworm.arch;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.arch.modules.AppModule;
import com.deedsit.android.bookworm.arch.modules.SecurityModule;
import com.deedsit.android.bookworm.arch.modules.ServiceModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by steph on 11/11/2017.
 */

@Singleton
@Component (modules = {AppModule.class, SecurityModule.class, ServiceModule.class})
public interface AppComponent extends AppGraph {

    final class Initializer {
        private Initializer() {}

        public static AppComponent init(BookWormApplication app) {
            return init(new AppModule(app), new SecurityModule(), new ServiceModule());
        }

        public static AppComponent init(AppModule appModule, SecurityModule securityModule, ServiceModule serviceModule) {
            return DaggerAppComponent.builder()
                    .appModule(appModule)
                    .securityModule(securityModule)
                    .serviceModule(serviceModule)
                    .build();
        }
    }
}
