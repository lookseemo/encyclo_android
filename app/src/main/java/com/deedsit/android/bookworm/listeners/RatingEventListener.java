package com.deedsit.android.bookworm.listeners;

import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.services.ServiceCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jack on 3/24/2018.
 */

public class RatingEventListener implements ChildEventListener {
    private Map<String, ServiceCallback> serviceCallbackMap;

    public RatingEventListener(){
        if(serviceCallbackMap == null)
            serviceCallbackMap = new HashMap<>();
    }

    public void addCallBack(String tag, ServiceCallback callback){
        serviceCallbackMap.put(tag, callback);
    }

    public boolean removeServiceCallBack(String tag){
        if(serviceCallbackMap.containsKey(tag)) {
            serviceCallbackMap.remove(tag);
            return true;
        }
        return false;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if(dataSnapshot.exists()){
            Rating rating = dataSnapshot.getValue(Rating.class);
            for (ServiceCallback callback : serviceCallbackMap.values()) {
                callback.onDataSuccessfullyAdded(rating);
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
