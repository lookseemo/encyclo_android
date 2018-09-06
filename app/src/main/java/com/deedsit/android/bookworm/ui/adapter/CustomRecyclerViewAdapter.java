package com.deedsit.android.bookworm.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.models.Course;
import com.deedsit.android.bookworm.models.CourseClass;
import com.deedsit.android.bookworm.models.DateItem;
import com.deedsit.android.bookworm.models.ListItem;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnClassClickedListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnCourseClickListener;
import com.deedsit.android.bookworm.ui.interfacelisteners.OnDataClickedListener;
import com.deedsit.android.bookworm.util.DataFormatedHelper;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jack on 11/26/2017
 * Customer recycler view adapter
 */

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ListItem> consoditatedList = new ArrayList();
    private OnDataClickedListener onDataClickedListener;

    public CustomRecyclerViewAdapter(OnDataClickedListener onDataClickedListener, ArrayList<ListItem> list) {
        consoditatedList.addAll(list);
        this.onDataClickedListener = onDataClickedListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListItem.TYPE_SECTION:
                view = layoutInflater.inflate(R.layout
                        .fragment_class_view_section, parent, false);
                viewHolder = new SectionViewHolder(view);
                break;
            case ListItem.TYPE_CLASS:
                view = layoutInflater.inflate(R.layout
                        .fragment_class_view_row, parent, false);
                viewHolder = new ClassesViewHolder(view);
                break;
            case ListItem.TYPE_COURSE:
                view = layoutInflater.inflate(R.layout
                        .fragment_home_lecturer_recycler_view_course_row, parent, false);
                viewHolder = new CourseViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void swap(ArrayList<ListItem> data) {
        if (data == null || data.size() == 0)
            return;
        if (data.size() > 0) {
            consoditatedList.clear();
            consoditatedList.addAll(data);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ListItem.TYPE_SECTION:
                DateItem dateItem = (DateItem) consoditatedList.get(position);
                ((SectionViewHolder) holder).bindView(dateItem);
                break;
            case ListItem.TYPE_CLASS:
                CourseClass courseClass = (CourseClass) consoditatedList.get(position);
                OnClassClickedListener onClassClickedListener = (OnClassClickedListener)
                        onDataClickedListener;
                ((ClassesViewHolder) holder).bindView(courseClass, onClassClickedListener);
                break;
            case ListItem.TYPE_COURSE:
                Course course = (Course) consoditatedList.get(position);
                OnCourseClickListener onCourseClickListener = (OnCourseClickListener)
                        onDataClickedListener;
                ((CourseViewHolder) holder).bindView(course, onCourseClickListener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return consoditatedList != null ? consoditatedList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return consoditatedList.get(position).getType();
    }

    //<--------------------SECTION VIEW HOLDER ----------------------------->
    static class SectionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dateTextView)
        TextView dateTextView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(DateItem dateItem) {
            String date = DataFormatedHelper.getDayFromStringTime(dateItem.getDate(), false) + ", " +
                    "" + dateItem.getDate();
            dateTextView.setText(date);
        }
    }

    //<--------------------CLASSES VIEW HOLDER ------------------------------>
    static class ClassesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.class_title)
        TextView classTitle;
        @BindView(R.id.class_time)
        TextView classTime;
        @BindView(R.id.class_status)
        TextView classStatus;
        @BindView(R.id.rating_label)
        TextView ratingTextView;

        public ClassesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(final CourseClass courseClass, final OnClassClickedListener onClassClickedListener) {
            classTitle.setText(courseClass.title);
            switch (courseClass.classStatus) {
                case LIVE:
                    classStatus.setVisibility(View.VISIBLE);
                    classStatus.setText(R.string.class_status_live_text);
                    classStatus.setBackgroundColor(Color.parseColor("#24ae20"));
                    break;
                case PAUSED:
                    classStatus.setVisibility(View.VISIBLE);
                    classStatus.setText(R.string.class_status_pause_text);
                    classStatus.setBackgroundColor(Color.parseColor("#72767C"));
                    break;
                case ENDED:
                    classStatus.setVisibility(View.VISIBLE);
                    classStatus.setText(R.string.class_status_ended_text);
                    classStatus.setBackgroundColor(Color.parseColor("#72767C"));
                    break;
                default:
                    classStatus.setVisibility(View.GONE);
            }
            if (courseClass.averageRating > 0) {
                ratingTextView.setVisibility(View.VISIBLE);
                ratingTextView.setText(courseClass.averageRating+"%");
                //calculate rating
            }
            if (courseClass.startTime > 0 && courseClass.finishTime > 0) {
                String startTime = DataFormatedHelper.convertTimeOrDateToString(new Date
                        (courseClass.startTime), false);
                String finishTime = DataFormatedHelper.convertTimeOrDateToString(new Date
                        (courseClass.finishTime), false);
                String time = startTime + " - " + finishTime;
                classTime.setText(time);
            } else if (courseClass.startTime > 0 && (courseClass.classStatus == CourseClass
                    .classStatusInd.LIVE || courseClass.classStatus == CourseClass.classStatusInd
                    .PAUSED)) {
                String startTime = DataFormatedHelper.convertTimeOrDateToString(new Date
                        (courseClass.startTime), false);
                classTime.setText("STARTED " + startTime);
            } else {
                classTime.setText("");
            }
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClassClickedListener.onClassClicked(courseClass);
                }
            });
        }
    }

    //<--------------------COURSE VIEW HOLDER ------------------------------>
    static class CourseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.course_title)
        TextView courseTitle;
        @BindView(R.id.class_status)
        TextView classStatusTV;
        @BindView(R.id.course_code_tv)
        TextView courseCodeTV;

        public CourseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(final Course course, final OnCourseClickListener onClickListener) {
            courseTitle.setText(course.title);
            courseCodeTV.setText(course.code);
            if (course.isLive) {
                classStatusTV.setVisibility(View.VISIBLE);
            } else
                classStatusTV.setVisibility(View.INVISIBLE);
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onCourseClicked(course);
                }
            });
        }
    }

}
