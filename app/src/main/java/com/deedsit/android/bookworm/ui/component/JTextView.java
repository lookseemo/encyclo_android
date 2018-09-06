package com.deedsit.android.bookworm.ui.component;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;

/**
 * Created by Jack on 3/9/2018.
 */

public class JTextView extends AppCompatTextView {

    private int id;
    private JTextViewClickListener listener;
    public JTextView(Context context, int id, JTextViewClickListener listener) {
        super(context);
        this.id = id;
        this.listener = listener;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public JTextViewClickListener getListener() {
        return listener;
    }

    public void setListener(JTextViewClickListener listener) {
        this.listener = listener;
    }
}
