package com.deedsit.android.bookworm.services.exceptions;

/**
 * Created by Jack on 12/11/2017.
 */

public class CoursesException extends Exception {
    @Override
    public String getMessage() {
        return "Course already added";
    }

}
