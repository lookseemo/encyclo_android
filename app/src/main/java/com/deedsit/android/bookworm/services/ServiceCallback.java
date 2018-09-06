package com.deedsit.android.bookworm.services;

/**
 * Created by steph on 12/11/2017.
 */

public interface ServiceCallback<T> {
    void onSuccess(final T data);
    void onFailure(Exception e);
    void onDataSuccessfullyAdded(final T data);
    void onDataSuccessfullyChanged(final T data);
    void onDataSuccessfullyRemoved(final T data);
}
