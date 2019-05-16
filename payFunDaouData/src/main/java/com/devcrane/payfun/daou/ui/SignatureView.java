package com.devcrane.payfun.daou.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.devcrane.payfun.daou.MainActivity;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.utility.BHelper;

public class SignatureView extends View {

	private static final float STROKE_WIDTH = 8f;

	/** Need to track this so the dirty region can accommodate the stroke. **/
	private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

	private Paint mPaint = new Paint();
	private Path mPath = new Path();
	private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Paint   mBitmapPaint;

	/**
	 * Optimizes painting by invalidating the smallest possible area.
	 */
	
	public boolean isTouch = false;// used to prevent request payment if have
									// not been sign

	@SuppressWarnings("deprecation")
	public SignatureView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		 Bitmap bm =BitmapFactory.decodeResource(getResources(),R.drawable.bar_payment_signature2);
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.txt_white_shape);
		if (bm != null) {
			BHelper.db("456");
			setBackgroundDrawable(new BitmapDrawable(bm));
		}
//		setBackgroundColor(Color.TRANSPARENT);
		mPaint.setAntiAlias(true);
//		paint.setColor(Color.parseColor("#a6988d"));
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeJoin(Paint.Join.ROUND);
//		paint.setStrokeWidth(STROKE_WIDTH);
		
		Log.d("SignView","init()");
		mPaint.setDither(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(4);
		
//		mBitmap = Bitmap.createBitmap(800,500, Bitmap.Config.ARGB_8888);	    
//		mCanvas = new Canvas(mBitmap);
//		mPath = new Path();
//		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
//		mCanvas.drawColor (0xFFFFFFFF);	//background color
	      
	}

	/**
	 * Erases the signature.
	 */
	public void clear() {
		mPath.reset();

		// Repaints the entire view.
		invalidate();
	}
	private float mX, mY;
	  private static final float TOUCH_TOLERANCE = 4;
	  
	/**
	*/
	  private void touch_start(float x, float y) {
	      mPath.reset();
	      mPath.moveTo(x, y);
	      mX = x;
	      mY = y;
	  }
	/**
	*/
	  private void touch_move(float x, float y) {
	      float dx = Math.abs(x - mX);
	      float dy = Math.abs(y - mY);
	      if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
	          mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
	          mX = x;
	          mY = y;
	      }
	  }
	/**
	*/
	  private void touch_up() {
	      mPath.lineTo(mX, mY);
	      mCanvas.drawPath(mPath, mPaint);
	      mPath.reset();
	  }
	@Override
	protected void onDraw(Canvas canvas) {
		try{
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			canvas.drawPath(mPath, mPaint);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		int width = this.getWidth();
		int height = this.getHeight();
		
		
		BHelper.db("Signature size: ("+ width+ ","+ height+")");
		if(width*height==0)
			return;
		mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mCanvas.drawColor (0xFFFFFFFF);	//background color
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction(); 
		float x= event.getX();
		float y= event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			MainActivity.lockLeftRightMenu(true);
			touch_start(x, y);
			isTouch = true;
			BHelper.db("touch is true");
			// There is no end point yet, so don't waste cycles invalidating.
			return true;

		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			if(!StaticData.getIsCalled())
				MainActivity.lockLeftRightMenu(false);
			break;

		default:
			BHelper.db("Ignored touch event: " + event.toString());
			return false;
		}
		invalidate();
		
		return true;
	}

	public Bitmap getBitmap()
	{
		  return mBitmap;
	}
}