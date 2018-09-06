package com.deedsit.android.bookworm.listeners;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

/**
 * Created by Jack on 3/25/2018.
 */

public class AllCourseDataEventListener implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if(Constants.courseList == null)
            Constants.courseList = new ArrayList<>();
        if(dataSnapshot.exists()){
            Course course = dataSnapshot.getValue(Course.class);
            course.setClasses(new ArrayList<CourseClass>());
            boolean courseExisted = false;
            for(Course courseInList : Constants.courseList){
                if(courseInList.code.equalsIgnoreCase(course.code)){
                    courseExisted = true;
                    break;
                }
            }
            if(!courseExisted) {
                Constants.courseList.add(course);
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
