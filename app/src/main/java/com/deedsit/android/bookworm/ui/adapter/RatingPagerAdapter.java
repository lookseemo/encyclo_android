package com.deedsit.android.bookworm.ui.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.deedsit.android.bookworm.R;
import com.deedsit.android.bookworm.ui.base.FragmentView;

import java.util.ArrayList;

/**
 * Created by Jack on 3/17/2018.
 */

public class RatingPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEM = 2;
    private Activity activity;
    private ArrayList<FragmentView> mFragmentList;
    private int[] iconIds = {R.drawable.ic_show_chart, R.drawable.ic_student_listing};

    public RatingPagerAdapter(Activity activity, FragmentManager fragmentManager,
                              ArrayList<FragmentView> fragmentList) {
        super(fragmentManager);
        this.activity = activity;
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return NUM_ITEM;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable icon = ContextCompat.getDrawable(activity, iconIds[position]);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
//        switch (position) {
//            case 0:
//                return RatingFragmentLecturer.TAG;
//            case 1:
//                return "STUDENT VIEW";
//            default:
//                return null;
//        }
    }
}
