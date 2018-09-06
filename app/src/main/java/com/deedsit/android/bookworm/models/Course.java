package com.deedsit.android.bookworm.models;

/**
 * Created by Jack Nguyen
 * Course POJO
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;


public class Course extends ListItem implements Serializable {
    @PropertyName("course_id")
    public String code;
    @PropertyName("course_title")
    public String title;
    @PropertyName("course_rating")
    public double courseRating;
    @PropertyName("is_live")
    public boolean isLive = false;
    @Exclude
    private ArrayList<CourseClass> classes = new ArrayList<>();

    public Course() {
        // Default constructor required for calls to DataSnapshot.getValue(Course.class)
    }

    public Course(String code, String title, ArrayList<CourseClass> classes) {
        this.code = code;
        this.title = title;
        this.classes = classes;
    }

    @Exclude
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    public ArrayList<CourseClass> getClasses() {
        return classes;
    }

    @Exclude
    public void setClasses(ArrayList<CourseClass> classes) {
        this.classes = classes;
    }

    @Exclude
    public double getCourseRating() {
        return courseRating;
    }

    @Exclude
    public void setCourseRating(double courseRating) {
        this.courseRating = courseRating;
    }

    @Exclude
    public boolean isLive() {
        return isLive;
    }

    @Exclude
    public void setLive(boolean live) {
        isLive = live;
    }

    @Exclude


    @Override
    public int getType() {
        return ListItem.TYPE_COURSE;
    }
}
