package com.deedsit.android.bookworm.services;

import android.support.annotation.Nullable;

import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.User;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by steph on 12/11/2017.
 */

public interface AuthRepo {
    void signIn(String email, String password, ServiceCallback<User> callback);
    void signUp(User user, String password, ServiceCallback<User> callback);
    boolean signOut();
    @Nullable User getCurrentUser();
}
