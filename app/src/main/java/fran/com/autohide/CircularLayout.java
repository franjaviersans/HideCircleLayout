package fran.com.autohide;

/**
 * Created by Francisco on 1/9/2016.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


/**
 * Created by Nilanchala on 8/3/15.
 */
public class CircularLayout extends ViewGroup  {
    private int radius = 1;
    private int circleHeight = 0;
    private int circleWidth = 0;

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
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutWidth = r - l;
        int layoutHeight = b - t;

        radius = (layoutWidth <= layoutHeight) ? layoutWidth / 3
                : layoutHeight / 3;

        circleHeight = getHeight();
        circleWidth = getWidth();



        //set every child around the corner
        int num = getChildCount();
        float angle = 0.0f;
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
            int x = Math.round((float) (((circleWidth / 2.0) - width / 2.0) + radius
                    * Math.cos(Math.toRadians(angle))));
            int y = Math.round((float) (((circleHeight / 2.0) - height / 2.0) + radius
                    * Math.sin(Math.toRadians(angle))));

            //set the layout of the children
            child.layout(x, y, x + width, y + height);

            //increment angle
            angle  += angleDif;
        }
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
}

