package com.deedsit.android.bookworm.util;

import android.text.TextUtils;

import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.DateItem;
import com.deedsit.android.bookworm.models.ListItem;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.logging.Handler;

/**
 * Created by Jack
 * This class is a repository tool to help with data formatting for UI and ect
 *
 * @version 1.0
 */

public class DataFormatedHelper {

    private static HashMap<String, ArrayList<CourseClass>> dateClassMap;
    private static Timer timer;
    private static CustomGraphTask customGraphTask;

    public static ArrayList<ListItem> prepareListItem(Course course) {
        ArrayList<ListItem> result = new ArrayList<>();
        dateClassMap = new HashMap<>();
        for (CourseClass courseClass : course.getClasses()) {
            String date = convertTimeOrDateToString(new Date(courseClass.startTime), true);
            if (dateClassMap.size() == 0 || !dateClassMap.containsKey(date)) {
                ArrayList<CourseClass> classList = new ArrayList<>();
                classList.add(courseClass);
                dateClassMap.put(date, classList);
            } else {
                dateClassMap.get(date).add(courseClass);
            }
        }
        for (String dateInMap : dateClassMap.keySet()) {
            DateItem dateItem = new DateItem();
            dateItem.setDate(dateInMap);
            Collections.sort(dateClassMap.get(dateInMap));
            result.add(dateItem);
            result.addAll(dateClassMap.get(dateInMap));
        }
        return result;
    }

    public static void classAddedToList(CourseClass courseClass, ArrayList<ListItem> adapterList) {
        Date classDate = new Date(courseClass.startTime);
        String dateStr = convertTimeOrDateToString(classDate, true);
        if (dateClassMap.containsKey(dateStr)) {
            //prevent same class from being added multi times
            boolean classExisted = false;
            for (CourseClass classInMap : dateClassMap.get(dateStr)) {
                if (classInMap.id.equalsIgnoreCase(courseClass.id)) {
                    classExisted = true;
                    break;
                }
            }
            if (!classExisted)
                dateClassMap.get(dateStr).add(courseClass);
        } else {//new date
            dateClassMap.put(dateStr, new ArrayList<CourseClass>());
            dateClassMap.get(dateStr).add(courseClass);
        }
        Collections.sort(dateClassMap.get(dateStr));
        //add back to list
        adapterList.clear();
        for (String dateInMap : dateClassMap.keySet()) {
            DateItem dateItem = new DateItem();
            dateItem.setDate(dateInMap);
            Collections.sort(dateClassMap.get(dateInMap));
            adapterList.add(dateItem);
            adapterList.addAll(dateClassMap.get(dateInMap));
        }
        Collections.sort(adapterList);
    }

    public static String convertTimeOrDateToString(Date date, boolean getDate) {
        SimpleDateFormat simpleDateFormat = null;
        String result = null;
        if (getDate) {
            simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
            result = simpleDateFormat.format(date);
        } else {
            simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
            result = simpleDateFormat.format(date);
            String meridiem = result.substring(result.length() - 2, result.length()).toUpperCase();
            result = result.substring(0, result.length() - 2) + meridiem;
        }
        return result;
    }

    public static String getDayFromStringTime(String dateStr, boolean shortDay) {
        Calendar calendar = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        try {
            Date date = simpleDateFormat.parse(dateStr);
            calendar.setTime(date);
            int dayInNum = calendar.get(Calendar.DAY_OF_WEEK);
            int todayNum = today.get(Calendar.DAY_OF_WEEK);
            if (dayInNum == todayNum && today.get(Calendar.DATE) == calendar.get(Calendar.DATE) &&
                    today.get(Calendar.MONTH) ==
                            calendar.get(Calendar
                                    .MONTH) && today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                return "Today";
            }
            switch (dayInNum) {
                case 1:
                    if (shortDay)
                        return "Sun";
                    else
                        return "Sunday";
                case 2:
                    if (shortDay)
                        return "Mon";
                    else
                        return "Monday";
                case 3:
                    if (shortDay)
                        return "Tue";
                    else
                        return "Tuesday";
                case 4:
                    if (shortDay)
                        return "Wed";
                    else
                        return "Wednesday";
                case 5:
                    if (shortDay)
                        return "Thu";
                    else
                        return "Thursday";
                case 6:
                    if (shortDay)
                        return "Fri";
                    else
                        return "Friday";
                case 7:
                    if (shortDay)
                        return "Sat";
                    else
                        return "Saturday";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String generateUnitCode(String courseCode) {
        String code = "";
        char spaceCharacter = ' ';
        if (!TextUtils.isEmpty(courseCode)) {
            for (int i = 0; i < courseCode.length(); i++) {
                if (i == 0) {
                    code += Character.toUpperCase(courseCode.charAt(i));
                } else if (courseCode.charAt(i) == spaceCharacter) {
                    code += Character.toUpperCase(courseCode.charAt(i + 1));
                }
            }
        }
        //fill the rest with random number
        if (code.length() < 6) {
            while (code.length() < 6) {
                Random random = new Random();
                int nextNumer = random.nextInt(1000);
                code += "" + nextNumer;
            }
        }
        return code;
    }

    public static void runClockBackThread(){
        timer = new Timer();
        final int TASK_INTERVAL = 1000;
        customGraphTask = new CustomGraphTask(null,null);
        timer.schedule(customGraphTask,TASK_INTERVAL);
    }

    public static void stopClock(){
        if(customGraphTask != null) {
            customGraphTask.cancel();
            customGraphTask = null;
        }
        if(timer != null){
            timer = null;
        }
    }
}
