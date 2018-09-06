package com.deedsit.android.bookworm.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class Rating {
    @PropertyName("rating_id")
    public String ratingID;
    @PropertyName("class_id")
    public String classId;
    @PropertyName("value")
    public double value;
    @PropertyName("rate_time")
    public long rateTime;
    @PropertyName("username")
    public String username;

    public Rating(String classId) { // dummy flag to generate the id

    }

    public Rating() {
        //Default constructor for database
    }

    @Exclude
    public String getRatingID() {
        return ratingID;
    }

    @Exclude
    public void setRatingID(String ratingID) {
        this.ratingID = ratingID;
    }

    @Exclude
    public String getClassId() {
        return classId;
    }

    @Exclude
    public void setClassId(String classId) {
        this.classId = classId;
    }

    @Exclude
    public double getValue() {
        return value;
    }

    @Exclude
    public void setValue(double value) {
        this.value = value;
    }

    @Exclude
    public long getRateTime() {
        return rateTime;
    }

    @Exclude
    public void setRateTime(long rateTime) {
        this.rateTime = rateTime;
    }

    @Exclude
    public String getUsername() {
        return username;
    }

    @Exclude
    public void setUsername(String username) {
        this.username = username;
    }
}
