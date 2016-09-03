package fran.com.autohide;

/**
 * Created by Francisco on 1/9/2016.
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;


/**
 * Created by Nilanchala on 8/3/15.
 */
public class CircularLayout extends ViewGroup  {
    private int miRadius = 1;
    private int miCircleHeight = 0;
    private int miCircleWidth = 0;
    private boolean mbRotate = false;
    private float miOldX = 0.0f, miOldY = 0.0f;
    //set and get functions necessary for object animation
    private float mfBeginAngle = 0.0f;
    float getMfBeginAngle(){
        return mfBeginAngle;
    }

    void setMfBeginAngle(float angle){
        mfBeginAngle = angle;
        rotateChildes();
    }

    private GestureDetector mGestureDetector;
    private ObjectAnimator mAnimator = null;

    public CircularLayout(Context context) {
        this(context, null, 0);
    }

    public CircularLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        mGestureDetector = new GestureDetector(getContext(), new MyGesture());
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutWidth = r - l;
        int layoutHeight = b - t;

        miRadius = (layoutWidth <= layoutHeight) ? layoutWidth / 3
                : layoutHeight / 3;

        miCircleHeight = getHeight();
        miCircleWidth = getWidth();



        rotateChildes();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure child views first
        int maxChildWidth = 0;
        int maxChildHeight = 0;

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);

            maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
        }

        // Then decide what size we want to be
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(widthSize, heightSize);
        } else {
            //Be whatever you want
            width = maxChildWidth * 3;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(heightSize, widthSize);
        } else {
            //Be whatever you want
            height = maxChildHeight * 3;
        }

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));

    }


    //Intercept some touch events
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        //System.out.println("Intercept");
        onTouchEvent(ev);
        return false; //don't consume the event. It is not for this view
    }


    //get events on touch
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
            mAnimator = null;
        }

        int action = motionEvent.getAction();
        //System.out.println("Circle->AJA");

        if(mGestureDetector.onTouchEvent(motionEvent)) return true;

        switch(action)
        {
            case(MotionEvent.ACTION_DOWN):
                //System.out.println("Circle->Down");

                miOldX = motionEvent.getX();
                miOldY = motionEvent.getY();


                mbRotate = true;

                return true;



            case(MotionEvent.ACTION_MOVE):
               // System.out.println("Circle->MOVE");



                return false; //don't consume the event. It is not for this view


            case(MotionEvent.ACTION_UP):
                mbRotate = false;
                //System.out.println("Circle->UP");

                return false; //don't consume the event. It is not for this view

            case(MotionEvent.ACTION_OUTSIDE):
               // System.out.println("Circle->OUTSIDE");
                return false; //don't consume the event. It is not for this view

            default:
                return false; //don't consume the event. It is not for this view
        }
    }

    void rotateChildes() {
        //set every child around the corner
        int num = getChildCount();
        float angle = mfBeginAngle;
        float angleDif = 360.0f / num;

        //set every child
        for(int i=0;i<num;++i){
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            //set them around a circle
            if (angle > 360) angle -= 360;
            else if (angle < 0) angle += 360;




            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int x = Math.round((float) (((miCircleWidth / 2.0)) + (miRadius)
                    * Math.cos(Math.toRadians(angle))));
            int y = Math.round((float) (((miCircleHeight / 2.0)) + (miRadius)
                    * Math.sin(Math.toRadians(angle))));


            //set the layout of the children
            child.layout(x - width/2, y - height / 2, x + width/2, y  + height/2);

            //increment angle
            angle  += angleDif;
        }
    }


    private class MyGesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //System.out.println("Circle->Fling!!!!");

            //rotate with the velocity
            float rotationAngle;
            float newY = e2.getY();

            float factor = 0.05f;

            if(newY > miOldY)   rotationAngle = velocityY * factor;
            else                rotationAngle = -velocityY * factor;

            int velocity = (int)Math.min(1000, Math.abs(velocityY));
            animateRotation(mfBeginAngle + rotationAngle, 1500);
            return true;

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
            //System.out.println("Circle->Scroll!!!!");

            float newX = e2.getX();
            float newY = e2.getY();

            //get the angle by polar coordinates
            double oldAngle = Math.atan2(miOldY - miCircleHeight/2.0, miOldX - miCircleWidth/2.0) * 180.0 / Math.PI;
            double angle = Math.atan2(newY - miCircleHeight/2.0, newX - miCircleWidth/2.0) * 180.0 / Math.PI;
            double diff = angle - oldAngle;

            //If there is a difference, rotate!!!
            if(Math.abs(diff) > 0.0001){
                mfBeginAngle += diff;
                rotateChildes();
            }


            miOldX = newX;
            miOldY = newY;

            return true;
        }
    }

    private void animateRotation(float endDegree, int duration) {
        if (mAnimator != null && mAnimator.isRunning() || Math.abs(mfBeginAngle - endDegree) < 1) {
            return;
        }

        mAnimator = ObjectAnimator.ofFloat(CircularLayout.this, "mfBeginAngle", mfBeginAngle, endDegree);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        //System.out.println("Empezo " + endDegree+ " "+ mAnimator.getPropertyName());

        mAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                //System.out.println("Empezo "+mfBeginAngle);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //System.out.println("Termin "+mfBeginAngle);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //System.out.println("Cancelado "+mfBeginAngle);
            }
        });
        mAnimator.start();
    }


}

