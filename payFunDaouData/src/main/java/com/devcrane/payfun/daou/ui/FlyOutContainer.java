package com.devcrane.payfun.daou.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class FlyOutContainer extends LinearLayout {

	// References to groups contained in this view.
	private View menu;
	private View menuRight;
	private View content;
	public boolean menuRightClick = false;
	public boolean menuLeftClick = false;

	// Layout Constants
	protected static final int menuMargin = 150;

	public enum MenuState {
		CLOSED, OPEN, CLOSING, OPENING
	};
	public enum MenuStateR {
		CLOSED, OPEN, CLOSING, OPENING
	};

	// Position information attributes
	protected int currentContentOffset = 0;
//	protected int currentContentOffsetR = 0;
	protected MenuState menuCurrentState = MenuState.CLOSED;
	protected MenuStateR menuCurrentStateR = MenuStateR.CLOSED;

	// Animation objects
	protected Scroller menuAnimationScroller = new Scroller(this.getContext(),
			new LinearInterpolator());
	protected Scroller menuAnimationScrollerR = new Scroller(this.getContext(),
			new LinearInterpolator());
//	protected Scroller menuAnimationScroller = new Scroller(this.getContext(),
//			new SmoothInterpolator());
	protected Runnable menuAnimationRunnable = new AnimationRunnable();
	protected Runnable menuAnimationRunnableR = new AnimationRunnableR();
	protected Handler menuAnimationHandler = new Handler();

	// Animation constants
	private static final int menuAnimationDuration = 0;
	private static final int menuAnimationPollingInterval = 16;

	public FlyOutContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlyOutContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlyOutContainer(Context context) {
		super(context);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		this.menu = this.getChildAt(0);
		this.content = this.getChildAt(2);
		this.menuRight = this.getChildAt(1);

		this.menu.setVisibility(View.GONE);
		this.menuRight.setVisibility(View.GONE);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (changed){
			this.calculateChildDimensions();
			}

		this.menu.layout(left, top, right - menuMargin, bottom);
		this.menuRight.layout(menuMargin, top, right, bottom);

		this.content.layout(left + this.currentContentOffset, top, right
				+ this.currentContentOffset, bottom);

	}

	public void toggleMenu() {
		switch (this.menuCurrentState) {
		case CLOSED:
			menuLeftClick = true;
			this.menuCurrentState = MenuState.OPENING;
			this.menu.setVisibility(View.VISIBLE);
			this.menuAnimationScroller.startScroll(0, 0, this.getMenuWidth(),
					0, menuAnimationDuration);
			break;
		case OPEN:
			menuLeftClick = false;
			this.menuCurrentState = MenuState.CLOSING;
			this.menuAnimationScroller.startScroll(this.currentContentOffset,
					0, -this.currentContentOffset, 0, menuAnimationDuration);
			break;
		default:
			return;
		}

		this.menuAnimationHandler.postDelayed(this.menuAnimationRunnable,
				menuAnimationPollingInterval);

		this.invalidate();
	}
	public void toggleMenuR() {
		switch (this.menuCurrentStateR) {
		case CLOSED:
			menuRightClick = true;
			this.menuCurrentStateR = MenuStateR.OPENING;
			this.menuRight.setVisibility(View.VISIBLE);
			this.menuAnimationScrollerR.startScroll(menuMargin, 0, this.getMenuWidthR()-menuMargin,
					0, menuAnimationDuration);			
			break;
		case OPEN:
			menuRightClick = false;
			this.menuRight.setVisibility(View.INVISIBLE);
			this.menuCurrentStateR = MenuStateR.CLOSING;
			this.menuAnimationScrollerR.startScroll(this.currentContentOffset,
					0, -this.currentContentOffset, 0, menuAnimationDuration);
			break;
		default:
			return;
		}

		this.menuAnimationHandler.postDelayed(this.menuAnimationRunnableR,
				menuAnimationPollingInterval);

		this.invalidate();
	}

	private int getMenuWidth() {
		return this.menu.getLayoutParams().width;
	}

	private int getMenuWidthR() {
		return this.menuRight.getLayoutParams().width;
	}
	private void calculateChildDimensions() {
		this.content.getLayoutParams().height = this.getHeight();
		this.content.getLayoutParams().width = this.getWidth();

		this.menu.getLayoutParams().width = this.getWidth() - menuMargin;
		this.menu.getLayoutParams().height = this.getHeight();
		this.menuRight.getLayoutParams().width = this.getWidth() - menuMargin;
		this.menuRight.getLayoutParams().height = this.getHeight();
	}
	

	private void adjustContentPosition(boolean isAnimationOngoing) {
		int scrollerOffset = this.menuAnimationScroller.getCurrX();

		this.content.offsetLeftAndRight(scrollerOffset
				- this.currentContentOffset);

		this.currentContentOffset = scrollerOffset;

		this.invalidate();
		
		if (isAnimationOngoing)
			this.menuAnimationHandler.postDelayed(this.menuAnimationRunnable,
					menuAnimationPollingInterval);
		else
			this.onMenuTransitionComplete();
	}
	private void adjustContentPositionR(boolean isAnimationOngoing) {
		int scrollerOffset = this.menuAnimationScrollerR.getCurrX();

		this.content.offsetLeftAndRight(-(scrollerOffset
				- this.currentContentOffset));

		this.currentContentOffset = scrollerOffset;

		this.invalidate();
		
		if (isAnimationOngoing)
			this.menuAnimationHandler.postDelayed(this.menuAnimationRunnableR,
					menuAnimationPollingInterval);
		else
			this.onMenuTransitionCompleteR();
	}

	private void onMenuTransitionComplete() {
		switch (this.menuCurrentState) {
		case OPENING:
			this.menuCurrentState = MenuState.OPEN;
			break;
		case CLOSING:
			this.menuCurrentState = MenuState.CLOSED;
			this.menu.setVisibility(View.GONE);
			break;
		default:
			return;
		}
	}

	private void onMenuTransitionCompleteR() {
		switch (this.menuCurrentStateR) {
		case OPENING:
			this.menuCurrentStateR = MenuStateR.OPEN;
			break;
		case CLOSING:
			this.menuCurrentStateR = MenuStateR.CLOSED;
			this.menuRight.setVisibility(View.GONE);
			break;
		default:
			return;
		}
	}
	protected class SmoothInterpolator implements Interpolator{

		@Override
		public float getInterpolation(float t) {
			return (float)Math.pow(t-1, 5) + 1;
		}
		
	}
	
	protected class AnimationRunnable implements Runnable {

		@Override
		public void run() {
			FlyOutContainer.this
					.adjustContentPosition(FlyOutContainer.this.menuAnimationScroller
							.computeScrollOffset());
			
		}

	}
	protected class AnimationRunnableR implements Runnable {

		@Override
		public void run() {
			FlyOutContainer.this
			.adjustContentPositionR(FlyOutContainer.this.menuAnimationScrollerR
					.computeScrollOffset());
		}

	}
}
