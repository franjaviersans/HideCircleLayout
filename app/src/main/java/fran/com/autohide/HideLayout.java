package fran.com.autohide;

/**
 * Created by Francisco on 31/8/2016.
 */

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Francisco on 1/9/2016.
 */
public class HideLayout extends LinearLayout {
    private int deviceWidth =1;
    private int mHideDistance;
    private int mNormalDistance;
    private boolean mOpen = false;
    private ObjectAnimator mAnimator = null;

    //to maage auto hide
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //setup all children disable
            mOpen = false;
            layoutClose((ViewGroup)getChildAt(0));


            if(mAnimator != null)
                mAnimator.end();


            mAnimator = ObjectAnimator.ofFloat(getChildAt(0), "translationX", mNormalDistance, mHideDistance);

            mAnimator.setDuration(250);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.start();
        }
    };

    //constructors
    public HideLayout(Context context) {
        this(context, null, 0);
    }

    public HideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;
        mNormalDistance = deviceDisplay.x / 2;
        mHideDistance = deviceDisplay.x * 3 / 4;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
        translateChild(mHideDistance);
    }


    //Intercept some touch events
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if(mOpen){
            //reset autohide
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 3000);
        }
        onTouchEvent(ev);
        return false; //don't consume the event. It is not for this view
    }

    //get events on touch. Hide, unhide and passing the touch event if necessary
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        if(mOpen){
            //reset autohide
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 3000);
        }


        switch(action)
        {
            case(MotionEvent.ACTION_DOWN):
                int x = (int) motionEvent.getX();

                if(mOpen){
                    //if it is open, and is touch outside the position of the menu, close it
                    if(x < mNormalDistance){

                        //stops autohide to close it
                        handler.removeCallbacks(runnable);

                        //setup all children disable
                        mOpen = false;
                        layoutClose((ViewGroup)getChildAt(0));


                        if(mAnimator != null)
                            mAnimator.end();


                        mAnimator = ObjectAnimator.ofFloat(getChildAt(0), "translationX", mNormalDistance, mHideDistance);

                        mAnimator.setDuration(250);
                        mAnimator.setInterpolator(new AccelerateInterpolator());
                        mAnimator.start();

                        return true;

                    }else{
                        //if touch near menu, let the menu take charge
                        return false;
                    }

                }else{
                    //if it is not open, only open it if touch near menu
                    if(x > mHideDistance) {

                        //set all childes enable
                        mOpen = true;
                        layoutOpen((ViewGroup)getChildAt(0));

                        if(mAnimator != null)
                            mAnimator.end();


                        mAnimator = ObjectAnimator.ofFloat(getChildAt(0), "translationX", mHideDistance, mNormalDistance);

                        mAnimator.setDuration(250);
                        mAnimator.setInterpolator(new AccelerateInterpolator());
                        mAnimator.start();

                        //indicate autohide in 3 seconds
                        handler.postDelayed(runnable, 3000);
                        return true;
                    }else{
                        //if touch outside menu, you don't do anything
                        return false; //don't consume the event. It is not for this view
                    }

                }




            case(MotionEvent.ACTION_UP):

                return false; //don't consume the event. It is not for this view

            case(MotionEvent.ACTION_OUTSIDE):
                return false; //don't consume the event. It is not for this view

            default:
                return false; //don't consume the event. It is not for this view
        }
    }

    //function to unhide the layout
    void layoutOpen(ViewGroup group){

        final int count = group.getChildCount();

        for (int i = 0; i < count; i++) {
            View child = group.getChildAt(i);

            if (child instanceof ViewGroup){
                layoutOpen((ViewGroup) child);
            }else if(child instanceof Button){
                Button B = (Button) child;

                B.setAlpha(1.0f);
                B.setEnabled(true);
                B.setClickable(true);
            }
        }
    }

    //function to hide the layout
    void layoutClose(ViewGroup group){

        final int count = group.getChildCount();

        for (int i = 0; i < count; i++) {
            View child = group.getChildAt(i);

            if (child instanceof ViewGroup){
                layoutClose((ViewGroup) child);
            }else if(child instanceof Button){
                Button B = (Button) child;

                B.setAlpha(0.5f);
                B.setEnabled(false);
                B.setClickable(false);
            }
        }
    }

    //function to translate all childs
    void translateChild(int trans){
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.setTranslationX(trans);
        }
    }

}

