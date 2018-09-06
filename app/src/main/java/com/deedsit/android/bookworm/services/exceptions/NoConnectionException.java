package com.deedsit.android.bookworm.services.exceptions;

/**
 * Created by steph on 12/11/2017.
 */

public class NoConnectionException extends Exception {
    @Override
    public String getMessage() {
        return "No connection exception";
    }
}
