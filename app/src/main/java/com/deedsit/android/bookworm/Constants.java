package com.deedsit.android.bookworm;

import android.content.Context;
import android.content.SharedPreferences;

import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.models.User;

import java.util.ArrayList;

/**
 * Created by Jack on 11/25/2017.
 */

public class Constants {
    public static boolean isLive               = false;
    public static final String COURSE_KEY_REF  = "courses";
    public static final String USER_REF        = "users";
    public static final String CLASSES_KEY_REF = "classes";
    public static final String RATING_KEY_REF = "ratings";
    public static final String SAVED_FRAGMENT_TAG = "SAVED_FRAGMENT_TAG";
    private static final String USER_TYPE = "USER_TYPE";
    public static final String LECTURER_USER = "LECTURER";
    public static final String STUDENT_USER  = "STUDENT";
    public static final String MASTER_FRAG_TAG = "MASTER_FRAGMENT";
    public static User user;
    public static CourseClass liveClass = null;
    public static Course courseSelected = null;
    public static String liveFragmentTag = null;
    public static ArrayList<Rating> masterRatingList = null;
    public static int masterListSize = 0;
    public static boolean isPaused = false;
    public static int second = 0, minute = 0, hour = 0;
    public static ArrayList<Course> courseList;

    //<------------------- ERROR CODES START -------------------------->
    public static final int CREATE_COURSE_ERR = 100;
    public static final int CREATE_CLASS_ERR  = 101;
    public static final int LOAD_CLASS_ERR = 102;
    public static final int COURSE_DUP = 103;
    public static final int CLASS_ENDED = 104;

    //<------------------- HANDLER MESSAGES --------------------------->
    public static final int UPDATE_GRAPH_MSG = 1002;
    public static final int UPDATE_CLOCK_MSG = 1003;
    public static final String CLOCK_SECOND = "CLOCK_SECOND";
    public static final String CLOCK_MINUTE = "CLOCK_MINUTE";
    public static final String CLOCK_HOUR   = "CLOCK_HOUR";
    public static final String GRAPH_DATA   = "GRAPH_DATA";
    private static final String SHARE_PREF_FILE_KEY = "BOOKWORM_SHARE_PREF";
}
