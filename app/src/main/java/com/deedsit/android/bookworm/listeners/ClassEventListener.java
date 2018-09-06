package com.deedsit.android.bookworm.listeners;

import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.services.ServiceCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.deedsit.android.bookworm.Constants.LECTURER_USER;

/**
 * Created by Jack on 12/20/2017.
 */

public class ClassEventListener implements ChildEventListener {

    private Map<String, ServiceCallback> callbackMap;
    public String courseCode;
    private String userType;

    public ClassEventListener(String userType) {
        if (callbackMap == null)
            callbackMap = new HashMap<>();
        this.userType = userType;
    }

    public boolean removeServiceCallBack(String tag) {
        if (callbackMap.containsKey(tag)) {
            callbackMap.remove(tag);
            return true;
        }
        return false;
    }

    public void addCallBack(String tag, ServiceCallback callback) {
        callbackMap.put(tag, callback);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.exists()) {
            CourseClass courseClass = dataSnapshot.getValue(CourseClass.class);
            if (userType.equalsIgnoreCase(LECTURER_USER)) {
                for (ServiceCallback callback : callbackMap.values()) {
                    callback.onDataSuccessfullyAdded(courseClass);
                }
            } else {
                // we only give student live class
                if (courseClass.getClassStatus() == CourseClass.classStatusInd.LIVE || courseClass
                        .getClassStatus() == CourseClass.classStatusInd.PAUSED) {
                    for (ServiceCallback callback : callbackMap.values()) {
                        callback.onDataSuccessfullyAdded(courseClass);
                    }
                }
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot.exists()) {
            CourseClass courseClass = dataSnapshot.getValue(CourseClass.class);
            for (ServiceCallback callback : callbackMap.values()) {
                callback.onDataSuccessfullyChanged(courseClass);
            }
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            for (ServiceCallback callback : callbackMap.values()) {
                callback.onDataSuccessfullyRemoved(dataSnapshot.child
                        ("class_id").getValue());
            }
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    /**
     *
     * @param dataSnapshot
     * @return
     */
    private CourseClass getCourseClass(DataSnapshot dataSnapshot){
        CourseClass courseClass = null;
        if(dataSnapshot.exists()){
            courseClass = new CourseClass();
            courseClass.id = (String) dataSnapshot.child("class_id").getValue();
            courseClass.averageRating = (long) dataSnapshot.child("average_rating").getValue();
            courseClass.startTime = (long) dataSnapshot.child("start_time").getValue();
            courseClass.finishTime = (long) dataSnapshot.child("finish_time").getValue();
            courseClass.title      = (String) dataSnapshot.child("class_title").getValue();
            switch ((String)dataSnapshot.child("status").getValue()){
                case "LIVE": courseClass.setClassStatus(CourseClass.classStatusInd.LIVE);
                    break;
                case "PAUSED": courseClass.setClassStatus(CourseClass.classStatusInd.PAUSED);
                    break;
                case "ENDED": courseClass.setClassStatus(CourseClass.classStatusInd.ENDED);
                    break;
            }
        }
        return courseClass;
    }
}
