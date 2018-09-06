package com.deedsit.android.bookworm.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jack on 11/26/2017.
 */

public class DateItem extends ListItem implements Serializable{

    String date;
    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }
    @Override
    public int getType() {
        return ListItem.TYPE_SECTION;
    }
}
