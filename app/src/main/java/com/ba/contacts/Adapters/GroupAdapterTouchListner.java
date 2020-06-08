package com.ba.contacts.Adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupAdapterTouchListner implements RecyclerView.OnItemTouchListener {
    private GestureDetector gestureDetector;
    private ClickListner clickListner;

    public GroupAdapterTouchListner(Context context,final RecyclerView recyclerView,final ClickListner clickListner){
        this.clickListner=clickListner;
        gestureDetector=new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(child!=null && clickListner!=null){
                    clickListner.onLongClick(child,recyclerView.getChildPosition(child));
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View child=rv.findChildViewUnder(e.getX(),e.getY());
        if(child !=null && clickListner!=null && gestureDetector.onTouchEvent(e)){
            clickListner.onClick(child,rv.getChildPosition(child));
        }
        return false;
    }


    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
    public interface ClickListner{
        void onClick(View view,int position);
        void onLongClick(View view,int position);
    }
}
