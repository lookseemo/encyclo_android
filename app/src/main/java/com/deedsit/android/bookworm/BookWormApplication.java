package com.deedsit.android.bookworm;

import android.app.Application;
import android.content.Context;

import com.deedsit.android.bookworm.arch.AppComponent;
import com.deedsit.android.bookworm.arch.AppGraph;

/**
 * Created by steph on 11/11/2017.
 */

public class BookWormApplication extends Application {
    static AppGraph component;
    static BookWormApplication _application;

    public static BookWormApplication getInstance() {
        return _application;
    }

    public BookWormApplication() {
        _application = this;
    }

    public AppGraph getComponent() {
        return component;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        if(component == null) {
            component = AppComponent.Initializer.init(this);
        }

        component.inject(this);
    }
}
