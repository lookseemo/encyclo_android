package com.deedsit.android.bookworm.models;

/**
 * Created by Jack
 * CourseClass POJO
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class CourseClass extends ListItem implements Serializable{
    @PropertyName("class_id")
    public String id;
    @PropertyName("class_title")
    public String title;
    @PropertyName("start_time")
    public long startTime;
    @PropertyName("finish_time")
    public long finishTime;
    @PropertyName("average_rating")
    public double averageRating;

    public enum classStatusInd{
        PAUSED,
        LIVE,
        ENDED
    }
    @PropertyName("status")
    public classStatusInd classStatus = classStatusInd.LIVE;


    public CourseClass() {
        // Default constructor required for calls to DataSnapshot.getValue(CourseClass.class)
    }

    public CourseClass(String id, String title){
        this.id = id;
        this.title = title;
    }

    @Exclude
    public String getTitle() {
        return title;
    }
    @Exclude
    public void setTitle(String title) {
        this.title = title;
    }
    @Exclude
    public long getStartTime() {
        return startTime;
    }
    @Exclude
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    @Exclude
    public long getFinishTime() {
        return finishTime;
    }
    @Exclude
    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
    @Exclude
    public double getAverageRating() {
        return averageRating;
    }
    @Exclude
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    @Exclude
    public void setClassStatus(classStatusInd classStatus){
        this.classStatus = classStatus;
    }
    @Exclude
    public classStatusInd getClassStatus(){
        return classStatus;
    }

    @Override
    @Exclude
    public int getType() {
        return ListItem.TYPE_CLASS;
    }
}
