package com.deedsit.android.bookworm.listeners;

import android.util.Log;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.services.ServiceCallback;
import com.deedsit.android.bookworm.services.exceptions.NoDataException;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentStudent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jack on 12/20/2017.
 */

public class CourseEventListener implements ChildEventListener {
    private Map<String, ServiceCallback> serviceCallbackMap;
    private static final String USER_REF = "users";

    private boolean studentType = false;
    public String currentUserID = null;

    public CourseEventListener() {
        if (serviceCallbackMap == null)
            serviceCallbackMap = new HashMap<>();
    }

    public boolean removeServiceCallBack(String tag) {
        if (serviceCallbackMap.containsKey(tag)) {
            serviceCallbackMap.remove(tag);
            return true;
        }
        return false;
    }

    public void addCallBack(String tag, ServiceCallback callback) {
        serviceCallbackMap.put(tag, callback);
        if (tag.equalsIgnoreCase(HomeFragmentStudent.TAG))
            studentType = true;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.exists()) {
            Log.d("JACKIE", "Child Added Called");
            Course course = dataSnapshot.getValue(Course.class);
            course.setClasses(new ArrayList<CourseClass>());
            for (ServiceCallback callback : serviceCallbackMap.values()) {
                callback.onDataSuccessfullyAdded(course);
            }
        } else {
            for (ServiceCallback callback : serviceCallbackMap.values()) {
                callback.onFailure(new NoDataException());
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        //first detect if current user is removed the course
        Log.d("JACKIE", "Child Changed Called");
        Course course = dataSnapshot.getValue(Course.class);
        for (ServiceCallback callback : serviceCallbackMap.values()) {
            callback.onDataSuccessfullyChanged(course);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Course course = dataSnapshot.getValue(Course.class);
        for (ServiceCallback callback : serviceCallbackMap.values()) {
            callback.onDataSuccessfullyRemoved(course);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
