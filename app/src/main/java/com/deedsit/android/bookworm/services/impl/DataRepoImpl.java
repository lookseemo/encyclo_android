package com.deedsit.android.bookworm.services.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.StudentListingManager;
import com.deedsit.android.bookworm.config.AppConfig;
import com.deedsit.android.bookworm.listeners.AllCourseDataEventListener;
import com.deedsit.android.bookworm.listeners.ClassEventListener;
import com.deedsit.android.bookworm.listeners.CourseEventListener;
import com.deedsit.android.bookworm.listeners.RatingEventListener;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.models.User;
import com.deedsit.android.bookworm.services.AuthRepo;
import com.deedsit.android.bookworm.services.DataRepo;
import com.deedsit.android.bookworm.services.ServiceCallback;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentStudent;
import com.deedsit.android.bookworm.ui.mainactivityfragments.StudentListingFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import static com.deedsit.android.bookworm.Constants.CLASSES_KEY_REF;
import static com.deedsit.android.bookworm.Constants.COURSE_KEY_REF;
import static com.deedsit.android.bookworm.Constants.RATING_KEY_REF;
import static com.deedsit.android.bookworm.Constants.USER_REF;

public class DataRepoImpl implements DataRepo {
    @NonNull
    private AppConfig appConfig;
    @NonNull
    private AuthRepo authRepo;

    private DatabaseReference cloudDatabase;
    private ClassEventListener classEventListener;
    private CourseEventListener courseEventListener;
    private RatingEventListener ratingEventListener;
    private AllCourseDataEventListener allCourseDataEventListener;
    private StudentListingManager  studentListingManager;

    public enum queryPath {
        Course,
        Class,
        Rating,
        StudentListing,
        User
    }

    @Inject
    public DataRepoImpl(@NonNull AppConfig appConfig, @NonNull AuthRepo authRepo) {
        this.appConfig = appConfig;
        this.authRepo = authRepo;
        cloudDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void getUser(String email, final ServiceCallback<User> callback) {
        Query query = cloudDatabase.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        User user = null;
                        for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                            user = dataSnapshots.getValue(User.class);
                            user.key = dataSnapshots.getKey();
                            callback.onSuccess(user);
                            break;
                        }
                        if (user == null)
                            callback.onFailure(new Exception("User does not exist"));
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(new Exception("User does not exist"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(new Exception(databaseError.toException()));
            }
        });
    }


    @Override
    public void queryDatabase(queryPath path, String courseCode, String fragmentTag, String
            filterKey) {
        switch (path) {
            case Course:
                if (filterKey != null) {
                    //remove current one
                    cloudDatabase.child(COURSE_KEY_REF).removeEventListener(courseEventListener);
                    //add a new one
                    cloudDatabase.child(COURSE_KEY_REF).orderByChild(USER_REF + "/" + Constants.user
                            .key).equalTo(true).addChildEventListener
                            (courseEventListener);
                } else if (filterKey == null && fragmentTag.equalsIgnoreCase(HomeFragmentStudent.TAG)) {
                    // all courses listener
                    if (allCourseDataEventListener == null)
                        allCourseDataEventListener = new AllCourseDataEventListener();
                    //remove current one
                    cloudDatabase.child(COURSE_KEY_REF).removeEventListener(allCourseDataEventListener);
                    //add new one
                    cloudDatabase.child(COURSE_KEY_REF).addChildEventListener
                            (allCourseDataEventListener);
                }
                break;
            case Class:
                classEventListener.courseCode = courseCode;
                //remove current one
                cloudDatabase.child(CLASSES_KEY_REF).removeEventListener(classEventListener);
                //add new one
                cloudDatabase.child(CLASSES_KEY_REF).orderByChild(COURSE_KEY_REF + "/" + courseCode).equalTo
                        (true).addChildEventListener(classEventListener);
                break;
            case Rating:
                //remove current one
                cloudDatabase.child(RATING_KEY_REF).removeEventListener(ratingEventListener);
                // add new one
                cloudDatabase.child(RATING_KEY_REF).orderByChild("class_id").equalTo(filterKey)
                        .addChildEventListener(ratingEventListener);
                break;
            case StudentListing:
                studentListingManager.initilizeDataReceiving();
                break;
        }
    }

    @Override
    public void registerCallBackEvent(DataRepoImpl.queryPath path, ServiceCallback callback, String
            fragmentTag) {
        if (courseEventListener == null && classEventListener == null && ratingEventListener ==
                null) {
            courseEventListener = new CourseEventListener();
            classEventListener = new ClassEventListener(Constants.user.userType);
            ratingEventListener = new RatingEventListener();
            //user id
            courseEventListener.currentUserID = Constants.user.username;
        }
        switch (path) {
            case Course:
                courseEventListener.addCallBack(fragmentTag, callback);
                break;
            case Class:
                classEventListener.addCallBack(fragmentTag, callback);
                break;
            case Rating:
                ratingEventListener.addCallBack(fragmentTag, callback);
                break;
            case StudentListing:
                if(studentListingManager == null)
                    studentListingManager = new StudentListingManager(cloudDatabase);
                studentListingManager.registerServiceCallBack(fragmentTag, callback);
        }
    }

    @Override
    public void unregisterCallBack(String tag) {
        courseEventListener.removeServiceCallBack(tag);
        classEventListener.removeServiceCallBack(tag);
        ratingEventListener.removeServiceCallBack(tag);
        if(studentListingManager != null)
            studentListingManager.unregisterServiceCallback(tag);
    }

    @Override
    public void addCourse(Course course) {
        //add course first
        boolean courseExisted = false;
        if(Constants.courseList != null) {
            for (Course courseInList : Constants.courseList) {
                if (course.code.equalsIgnoreCase(courseInList.code)) {
                    courseExisted = true;
                    break;
                }
            }
        }
        if (!courseExisted) {
            cloudDatabase.child(COURSE_KEY_REF + "/" + course.code).setValue(course);
        }
        //add users
        cloudDatabase.child(COURSE_KEY_REF + "/" + course.code + "/" + USER_REF + "/" +
                Constants.user.key).setValue(true);
    }

    @Override
    public void addClass(CourseClass courseClass, String code) {
        //add class first
        cloudDatabase.child(CLASSES_KEY_REF + "/" + courseClass.id).setValue(courseClass);
        //add course code
        cloudDatabase.child(CLASSES_KEY_REF + "/" + courseClass.id + "/" + COURSE_KEY_REF + "/" +
                code).setValue(true);
        //change the status of course to live
        cloudDatabase.child(COURSE_KEY_REF + "/" + code + "/" + "is_live").setValue(true);
    }

    @Override
    public void updateClassStatus(CourseClass courseClass, String courseCode) {
        // update class status
        cloudDatabase.child(CLASSES_KEY_REF + "/" + courseClass.id + "/" + "status").setValue
                (courseClass.classStatus);
        if (courseClass.classStatus == CourseClass.classStatusInd.ENDED && courseCode != null) {
            //update class rating and finish time
            cloudDatabase.child(CLASSES_KEY_REF + "/" + courseClass.id + "/" + "finish_time")
                    .setValue(courseClass.finishTime);
            cloudDatabase.child(CLASSES_KEY_REF + "/" + courseClass.id + "/" + "average_rating")
                    .setValue(courseClass.averageRating);
            //update course
            cloudDatabase.child(COURSE_KEY_REF + "/" + courseCode + "/" + "is_live").setValue(false);
        }
    }

    @Override
    public void addRating(Rating rating) {
        cloudDatabase.child(RATING_KEY_REF);
        String key = cloudDatabase.push().getKey();
        rating.ratingID = key;
        cloudDatabase.child(RATING_KEY_REF + "/" + rating.ratingID).setValue(rating);
    }

    //<----------------------- Private method for repo ---------------------------------------->
}
