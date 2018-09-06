package com.deedsit.android.bookworm.ui.adapter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;

/**
 * Created by Jack on 1/1/2018.
 */

public class ItemSwipeController extends ItemTouchHelper.Callback {

    private boolean swipeBack;
    private ButtonState editButtonState = ButtonState.GONE;
    private static final float BUTTON_WIDTH = 300;

    public enum ButtonState {
        GONE,
        VISIBLE
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            if(dX < BUTTON_WIDTH){
                dX = -BUTTON_WIDTH;
                editButtonState = ButtonState.VISIBLE;
                if(editButtonState != ButtonState.GONE){
                    setTouchDownListener(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
                    setItemClickable(recyclerView,false);
                }
            }
//            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void setTouchListener(final Canvas canvas, final RecyclerView recyclerView, final RecyclerView
            .ViewHolder viewHolder, final float dX, final float dY, final int actionState, final
                                  boolean
                                          isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipeBack = motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    if (dX < -BUTTON_WIDTH) {
                        editButtonState = ButtonState.VISIBLE;
                    }
                    if (editButtonState != ButtonState.GONE) {
                        setTouchDownListener(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemClickable(recyclerView, false);
                    }

                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas canvas, final RecyclerView recyclerView, final RecyclerView
            .ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean
                                            isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    ItemSwipeController.super.onChildDraw(canvas, recyclerView, viewHolder, 0F,
                            dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                    setItemClickable(recyclerView, true);
                    swipeBack = false;
                    editButtonState = ButtonState.GONE;
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas canvas, final RecyclerView recyclerView, final RecyclerView
            .ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean
                                              isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });

    }

    private void setItemClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

}
