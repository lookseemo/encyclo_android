package com.deedsit.android.bookworm.services;

import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.models.User;
import com.deedsit.android.bookworm.services.impl.DataRepoImpl;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;

/**
 * Created by steph on 12/11/2017.
 */

public interface DataRepo {
    void getUser(String email, ServiceCallback<User> callback);
    void queryDatabase(DataRepoImpl.queryPath path, String courseCode,String fragmentTag, String
            filterKey);
    void registerCallBackEvent(DataRepoImpl.queryPath path, ServiceCallback callback, String
            fragmentTag);
    void unregisterCallBack(String tag);
    void addCourse(Course course);
    void addClass(CourseClass courseClass, String code);
    void updateClassStatus(CourseClass courseClass, String code);
    void addRating(Rating rating);
}
