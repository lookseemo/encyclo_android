package com.deedsit.android.bookworm.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.deedsit.android.bookworm.Constants;
import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.Rating;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentLecturer;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnClassAddedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnCourseAddedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnDataAddedListener;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentStudent;
import com.deedsit.android.bookworm.ui.mainactivityfragments.ViewClassesFragment;
import com.deedsit.android.bookworm.util.DataFormatedHelper;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jack on 11/27/2017.
 */

public class InputDialog extends BaseDialog implements View.OnClickListener {
    private static final String FROM_TAG = "FROM_TAG";
    public static final String TAG = "INPUT_DIALOG_FRAGMENT";
    private static final String LISTENER = "LISTENER";
    private OnDataAddedListener onDataAddedListener;
    AlertDialog dialog;
    //Component for course
    private EditText courseCodeET;
    private EditText courseTitleET;
    private TextInputLayout courseCodeLayout;
    private TextInputLayout courseTitleLayout;
    private Button generateButton;
    private Button createCourseButton;
    private ImageView closeIcon;
    //Component for class
    private EditText classTitleET;
    private TextInputLayout classTitleLayout;
    private Button startClassBtn;
    private String fragmentTag;

    public static InputDialog newInstance(OnDataAddedListener onDataAddedListener, String tag) {
        InputDialog inputDialog = new InputDialog();
        Bundle args = new Bundle();
        args.putString(FROM_TAG, tag);
        args.putSerializable(LISTENER, onDataAddedListener);
        inputDialog.setArguments(args);
        return inputDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        onDataAddedListener = (OnDataAddedListener) getArguments().getSerializable(LISTENER);
        View view = null;
        fragmentTag = getArguments().getString(FROM_TAG);
        switch (fragmentTag) {
            case HomeFragmentLecturer.TAG: // add course
                view = inflater.inflate(R.layout.course_add_dialog, null);
                courseCodeET = view.findViewById(R.id.course_code_input);
                courseCodeLayout = view.findViewById(R.id.course_code_input_layout);
                courseTitleET = view.findViewById(R.id.course_title_input);
                courseTitleLayout = view.findViewById(R.id.course_title_input_layout);
                generateButton = view.findViewById(R.id.generate_button);
                createCourseButton = view.findViewById(R.id.create_course_button);
                closeIcon = view.findViewById(R.id.close_ic);
                //clicks listener
                generateButton.setOnClickListener(this);
                createCourseButton.setOnClickListener(this);
                closeIcon.setOnClickListener(this);
                break;
            case ViewClassesFragment.TAG:
                //class fragment
                view = inflater.inflate(R.layout.dialog_lecturer_add_class, null);
                classTitleET = view.findViewById(R.id.class_title_input);
                classTitleLayout = view.findViewById(R.id.class_title_input_layout);
                startClassBtn = view.findViewById(R.id.start_class_button);
                closeIcon = view.findViewById(R.id.close_ic);
                //listeners
                startClassBtn.setOnClickListener(this);
                closeIcon.setOnClickListener(this);
                break;
            case HomeFragmentStudent.TAG:
                //student fragment
                view = inflater.inflate(R.layout.dialog_student_add_course,null);
                courseCodeET = view.findViewById(R.id.course_code_input);
                courseCodeLayout = view.findViewById(R.id.course_code_input_layout);
                createCourseButton = view.findViewById(R.id.create_course_button);
                closeIcon = view.findViewById(R.id.close_ic);
                createCourseButton.setOnClickListener(this);
                closeIcon.setOnClickListener(this);
                break;
        }
        builder.setView(view);
        dialog = builder.create();
        return dialog;
    }

    private boolean validateCourseForm() {
        if (!TextUtils.isEmpty(courseCodeET.getText().toString())) {
            if(courseTitleET != null) {
                if (!TextUtils.isEmpty(courseTitleET.getText().toString())) {
                    return true;
                } else {
                    courseTitleLayout.setErrorEnabled(true);
                    courseTitleLayout.setError(getString(R.string.course_name_missing_error_text));
                }
            }else
                return true;
        } else {
            courseCodeLayout.setErrorEnabled(true);
            courseCodeLayout.setError(getString(R.string.course_code_missing_error_text));
        }
        return false;
    }

    private boolean validateClassForm() {
        if (!TextUtils.isEmpty(classTitleET.getText().toString())) {
            return true;
        } else {
            classTitleLayout.setErrorEnabled(true);
            classTitleLayout.setError(getString(R.string.course_code_missing_error_text));
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.generate_button:
                String unitCode = DataFormatedHelper.generateUnitCode(courseTitleET.getText()
                        .toString());
                if(!TextUtils.isEmpty(unitCode))
                    courseCodeET.setText(unitCode);
                else {
                    courseCodeLayout.setErrorEnabled(true);
                    courseCodeLayout.setError(getString(R.string.enter_course_name_error));
                }
                break;
            case R.id.create_course_button:
                if(fragmentTag.equalsIgnoreCase(HomeFragmentLecturer.TAG)) {
                    if (createCourse())
                        dialog.dismiss();
                }else if(fragmentTag.equalsIgnoreCase(HomeFragmentStudent.TAG)){
                    if(addCourse())
                        dialog.dismiss();
                    else{
                        courseCodeLayout.setErrorEnabled(true);
                        courseCodeLayout.setError("Course Code Invalid");
                    }
                }
                break;
            case R.id.close_ic:
                dialog.dismiss();
                break;
            case R.id.start_class_button:
                dialog.dismiss();
                startClassNow();
                break;
        }
    }

    private boolean createCourse() {
        if (onDataAddedListener != null) {
            if (onDataAddedListener instanceof OnCourseAddedListener) {
                if (!validateCourseForm())
                    return false;
                String courseCode = courseCodeET.getText().toString();
                String courseTitle = courseTitleET.getText().toString();
                Course course = new Course(courseCode, courseTitle, new
                        ArrayList<CourseClass>());
                ((OnCourseAddedListener) onDataAddedListener).onCourseAddedListener(course);
                return true;
            }
        }
        return false;
    }

    private boolean addCourse(){
        if(!validateCourseForm())
            return false;
        String code = courseCodeET.getText().toString();
        for(Course course : Constants.courseList){
            if(code.equalsIgnoreCase(course.code)){
                if(onDataAddedListener != null && onDataAddedListener instanceof OnCourseAddedListener){
                    ((OnCourseAddedListener) onDataAddedListener).onCourseAddedListener(course);
                    return true;
                }
            }
        }
        return false;
    }

    private void startClassNow() {
        if (onDataAddedListener != null) {
            if (onDataAddedListener instanceof OnClassAddedListener) {
                if (!validateClassForm())
                    return;
                String classTitle = classTitleET.getText().toString();
                CourseClass courseClass = new CourseClass();
                courseClass.title = classTitle;
                courseClass.classStatus = CourseClass.classStatusInd.LIVE;
                //random ID with length of 4
                Random random = new Random();
                courseClass.id = ""+0;
                while(courseClass.id.length() < 4){
                    courseClass.id += random.nextInt(10)+"";
                }
                courseClass.startTime = System.currentTimeMillis();
                ((OnClassAddedListener) onDataAddedListener).onClassAdded(courseClass);
            }
        }
    }


}
