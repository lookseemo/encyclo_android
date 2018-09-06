package com.deedsit.android.bookworm.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Jack on 11/26/2017.
 */

public abstract class ListItem implements Serializable, Comparable<ListItem> {
    public static final int TYPE_SECTION = 0;
    public static final int TYPE_CLASS = 1;
    public static final int TYPE_COURSE = 2;

    abstract public int getType();

    /**
     * This Method sort for both Course list and Course Classes List.
     * This method ensures that the Course classes list will be sorted based on their status and
     * date that is closest to today.
     * @param listItem
     * @return -1 for being top, 1 below and 0 equal.
     */
    @Override
    public int compareTo(@NonNull ListItem listItem) {
    //<------------ COURSE SORT HERE ---------------->
        if (listItem instanceof Course) {
            Course course1 = (Course) this;
            Course course2 = (Course) listItem;
            /**
             * We sort the course by it status first, course with live will go on top
             * If both courses are not live, we sort by alphabet.
             */
            if (course1.isLive) {
                return -1;
            } else if (course2.isLive) {
                return 1;
            } else {
                return course1.title.compareToIgnoreCase(course2.getTitle());
            }
     //<------------ COURSE SORT ENDS ---------------->
        } else {
            /**
             * There are few scenarios with this sort since we have 2 different objects in list
             * CourseClass to CourseClass OR CourseClass to DateItem
             * DateItem to DateItem OR DateItem to CourseClass
             *
             * We starts by this instance is a Courseclass
             */
            if (this instanceof CourseClass) {
                if (listItem instanceof CourseClass) {
                    /**
                     * if list item is a CourseClass, We first compare their status, any classes
                     * that are live or paused will be put on top.
                     * The secondary sort will be based on start time.
                     */
                    CourseClass courseClass1 = (CourseClass) this;
                    CourseClass courseClass2 = (CourseClass) listItem;
                    //Compare between classes status.
                    if (courseClass1.classStatus == CourseClass.classStatusInd.LIVE ||
                            courseClass1.classStatus == CourseClass.classStatusInd.PAUSED) {
                        return -1;
                    } else if (courseClass2.classStatus == CourseClass.classStatusInd.LIVE ||
                            courseClass2.classStatus == CourseClass.classStatusInd.PAUSED) {
                        return 1;
                    } else { // if 2 classes are offline, compare start time.
                        Date date1 = new Date(courseClass1.startTime);
                        Date date2 = new Date(courseClass2.startTime);
                        return date1.compareTo(date2);
                    }
                } else {
                    /**
                     * if list item is a DateTime, we have to compare the date of this
                     * Courseclass and Date in DateItem, if same date, we want DateItem on top,
                     * hence 1 is returned. If not we compared both of the date with today's date
                     */
                    DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
                    Date nowDate = new Date(); //today
                    try{
                        Date targetDate = df.parse(((DateItem) listItem).getDate());
                        Date thisDate = new Date(((CourseClass) this).startTime);
                        // we need this to compare dates
                        Calendar thisCalendar = Calendar.getInstance();
                        Calendar targetCalendar = Calendar.getInstance();
                        thisCalendar.setTime(thisDate);
                        targetCalendar.setTime(targetDate);

                        if(thisCalendar.get(Calendar.DATE) == targetCalendar.get(Calendar.DATE) &&
                                thisCalendar.get(Calendar.MONTH) == targetCalendar.get(Calendar
                                        .MONTH) && thisCalendar.get(Calendar.YEAR) ==
                                targetCalendar.get(Calendar.YEAR)){
                            // Dates are the same between 2 objects
                                return 1;
                        }else {
                            // Compare the date between 2 objects, which date closer to today
                            // will be on top.
                            long diffThis = nowDate.getTime() - thisDate.getTime();
                            long diffTarget = nowDate.getTime() - targetDate.getTime();
                            if (diffThis < diffTarget)
                                return -1;
                            else {
                                return 1;
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return 1;
                }
            } else {
                /**
                 * This instance is a DateTime object, we first compare if the listItem is
                 * DateTime Object. The result will be that the one closed to today's date will
                 * be on top
                 */
                DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
                try {
                    Date nowDate    = new Date();
                    Date thisDate   = df.parse(((DateItem) this).getDate());
                    Date targetDate = null;

                    if (listItem instanceof DateItem) {
                        targetDate = df.parse(((DateItem) listItem).getDate());
                    } else {
                        /**
                         * The list item now is CourseClass. We compare the date between them.
                         * Same logic as above.
                         */
                        targetDate = new Date(((CourseClass) listItem).startTime);
                    }
                        long diffThis = nowDate.getTime() - thisDate.getTime();
                        long diffTarget = nowDate.getTime() - targetDate.getTime();
                        if (diffThis < diffTarget)
                            return -1;
                        else
                            return 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
    }
}
