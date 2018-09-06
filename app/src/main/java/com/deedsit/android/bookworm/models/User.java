package com.deedsit.android.bookworm.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {
    @Exclude
    public String key;
    @PropertyName("username")
    public String username;
    @PropertyName("email")
    public String email;
    @PropertyName("firstName")
    public String firstName;
    @PropertyName("lastName")
    public String lastName;
    @PropertyName("userType")
    public String userType;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
// [END blog_user_class]
