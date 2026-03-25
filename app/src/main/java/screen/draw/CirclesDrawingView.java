package screen.draw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOverlay;


public class CirclesDrawingView extends View {

    private static final String TAG = "CirclesDrawingView";

    /** Main bitmap */
    private Bitmap mBitmap = null;

    private Rect mMeasuredRect;

    /** Stores data about single circle */
    public static class CircleArea {
        int radius;
        int centerX;
        int centerY;
        static int num = -1;

        CircleArea(int centerX, int centerY, int radius) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;
            num++;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }

    /** Paint to draw circles */
    private Paint mCirclePaint;

    private final Random mRadiusGenerator = new Random();
    // Radius limit in pixels
    private final static int RADIUS_LIMIT = 30;

    private static final int CIRCLES_LIMIT = 6;

    /** All available circles */
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    private ArrayList<CircleArea> mCircles1 = new ArrayList<CircleArea>(CIRCLES_LIMIT);
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);
    
    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public CirclesDrawingView(final Context ct) {
        super(ct);
        
        init(ct);
    }
    
    public CirclesDrawingView(final Context ct, int back) {
        super(ct);
        
        init(ct, back);
    }
    
    public CirclesDrawingView(final Context ct, View back) {
        super(ct);
        
        init(ct, back);
    }

    public CirclesDrawingView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);

        init(ct);
    }

    public CirclesDrawingView(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);

        init(ct);
    }

    private void init(final Context ct) {
        // Generate bitmap used for background
        mBitmap = BitmapFactory.decodeResource(ct.getResources(), FingerPaintActivity.fieldPicture);

        mCirclePaint = new Paint();

        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStrokeWidth(40);
        mCirclePaint.setStyle(Paint.Style.FILL);
    }
    
    private void init(final Context ct, int back) {
        // Generate bitmap used for background
    	
    	
    	
        mBitmap = BitmapFactory.decodeResource(ct.getResources(), back);

        mCirclePaint = new Paint();
        
        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStrokeWidth(40);
        mCirclePaint.setStyle(Paint.Style.FILL);
    }
    
    private void init(final Context ct, View v) {
        // Generate bitmap used for background
    	
        //mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.aerialassistfield1);
        
    	View e = FingerPaintActivity.mv.getRootView();
        
    	//mBitmap = ViewOverlay.this;//BitmapFactory.decodeResource(ct.getResources(), v);
    	
    	mBitmap = e.getDrawingCache();
    	
        //mBitmap = back;

        mCirclePaint = new Paint();
        
        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStrokeWidth(40);
        mCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(final Canvas canv) {
        // background bitmap to cover all area
        canv.drawBitmap(mBitmap, null, mMeasuredRect, null);
        
       int zx = -1;
        
        for(CircleArea circle : mCircles1) {
        	zx++;
        	if(zx>=3) {
        		mCirclePaint.setColor(Color.RED);
        		
        		canv.drawCircle(circle.centerX, circle.centerY, circle.radius, mCirclePaint);
        	} else {
        		mCirclePaint.setColor(Color.BLUE);
        		canv.drawCircle(circle.centerX, circle.centerY, circle.radius, mCirclePaint);
        	}
        	
        	
           
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);

                mCirclePointer.put(pointerId, touchedCircle);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                Log.w(TAG, "Move");

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearCirclePointer() {
        Log.w(TAG, "clearCirclePointer");

        mCirclePointer.clear();
    }

    /**
     * Search and creates new (if needed) circle based on touch area
     *
     * @param xTouch int x of touch
     * @param yTouch int y of touch
     *
     * @return obtained {@link CircleArea}
     */
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        /*if(mCircles.size()>=7) {
        	
        		mCircles.remove(mCircles.size());
        		
        }*/
        
        
        
        if (null == touchedCircle) {
            touchedCircle = new CircleArea(xTouch, yTouch, RADIUS_LIMIT);
            while(mCircles1.size() >= 7) {
            //if(mCircles1.size() >= 7) { 
            	mCircles1.remove(mCircles1.size()-1);
            //}
        }
            
            if (mCircles1.size() == CIRCLES_LIMIT) {
                Log.w(TAG, "Clear all circles, size is " + mCircles.size());
                // remove first circle
               // mCircles.clear();
            } else if(mCircles1.size() == CIRCLES_LIMIT/2) {
            	
            	 mCircles.add(touchedCircle);
            	 mCircles1.add(touchedCircle);
            	 Log.w(TAG, "Clear all circles, size is " + mCircles.size());
            	
            } else {

	            Log.w(TAG, "Added circle " + touchedCircle);
	            mCircles.add(touchedCircle);
	            mCircles1.add(touchedCircle);
	            Log.w(TAG, "Clear all circles, size is " + mCircles.size());
            
            }
        }

        return touchedCircle;
    }

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     *
     * @return {@link CircleArea} touched circle or null if no circle has been touched
     */
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;
        						//mCircles
        for (CircleArea circle : mCircles1) {
            if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
                touched = circle;
                break;
            }
        }

        return touched;
    }
    
    public HashSet<CircleArea> getList() {
    	return mCircles;
    }
    
    public ArrayList<CircleArea> getList1() {
    	return mCircles1;
    }
    
    public ArrayList<Integer> getCX() {
    	
    	ArrayList<Integer> zxc = new ArrayList<Integer>();
    	
    	for(CircleArea cirles : mCircles1) {
    		
    		zxc.add(cirles.centerX);
    		//zxc.add(cirles.centerY);
    		//zxc.add(cirles.radius);
    		
    	}
    	
    	
    	
    	return zxc;
    }
    
    public ArrayList<Integer> getCY() {
    	
    	ArrayList<Integer> zxc = new ArrayList<Integer>();
    	
    	for(CircleArea cirles : mCircles1) {
    		
    		//zxc.add(cirles.centerX);
    		zxc.add(cirles.centerY);
    		//zxc.add(cirles.radius);
    		
    	}
    	
    	
    	
    	return zxc;
    }
    
    
    public ArrayList<Integer> getRadius() {
    	
    	ArrayList<Integer> zxc = new ArrayList<Integer>();
    	
    	for(CircleArea cirles : mCircles1) {
    		
    		//zxc.add(cirles.centerX);
    		//zxc.add(cirles.centerY);
    		zxc.add(cirles.radius);
    		
    	}
    	
    	
    	
    	return zxc;
    }
    
    public void setEvery(ArrayList<Integer> x, ArrayList<Integer> y, ArrayList<Integer> r) {
    	
    	int index = 0;
    	mCircles1.clear();
    	for(int a : x) {
    		mCircles.add(new CircleArea(a, y.get(index), r.get(index)));
    		mCircles1.add(new CircleArea(a, y.get(index), r.get(index)));
    		index++;
    	}
    	
    	
    	
    }
    
    public void setImage(Drawable myImg) {
    	//mBitmap = BitmapFactory.decodeResource(ct.getResources(), myImg);
    	invalidate();
    }
    
    
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
    
    
    public static Bitmap getBitmapFromView(View v) {
    	
    	Bitmap returnedBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
    	
    	Canvas canvas = new Canvas(returnedBitmap);
    	
    	Drawable bg = v.getBackground();
    	if(bg != null) {
    		bg.draw(canvas);
    	} else {
    		canvas.drawColor(Color.BLUE);
    	}
    	
    	v.draw(canvas);
    	
    	return returnedBitmap;
    	
    }
    
    
    
}