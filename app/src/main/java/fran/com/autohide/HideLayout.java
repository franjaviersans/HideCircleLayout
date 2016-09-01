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

/**
 * Created by Francisco on 1/9/2016.
 */
public class HideLayout extends ViewGroup implements View.OnTouchListener {
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

        this.setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        System.out.println("LAYOUT!!!!");
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                return;

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();
            //wrap is reach to the end
            if (curLeft + curWidth >= childRight) {
                curLeft = childLeft;
                curTop += maxHeight;
                maxHeight = 0;
            }
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
            //store the max height
            if (maxHeight < curHeight)
                maxHeight = curHeight;
            curLeft += curWidth;
        }

        translateChild(mHideDistance);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        // Measurement will ultimately be computing these values.
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        int mLeftWidth = 0;
        int rowCount = 0;

        if(deviceWidth == 0) deviceWidth = 1;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            // Measure the child.
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            maxWidth += Math.max(maxWidth, child.getMeasuredWidth());
            mLeftWidth += child.getMeasuredWidth();

            if ((mLeftWidth / deviceWidth) > rowCount) {
                maxHeight += child.getMeasuredHeight();
                rowCount++;
            } else {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        System.out.println("AJA");

        if(mOpen){
            //reset autohide
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 3000);
        }


        switch(action)
        {
            case(MotionEvent.ACTION_DOWN):
                System.out.println("Down");
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
                        System.out.println("HELP");
                        //if touch outside menu, you don't do anything
                        return false; //don't consume the event. It is not for this view
                    }

                }




            case(MotionEvent.ACTION_UP):
                System.out.println("UP");

                /*layoutClose();
                view.requestLayout();*/
                return false; //don't consume the event. It is not for this view

            case(MotionEvent.ACTION_OUTSIDE):
                System.out.println("This will help");
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

