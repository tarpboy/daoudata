/*
 * Barebones implementation of displaying camera preview.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.devcrane.payfun.daou.camera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.devcrane.payfun.daou.utility.BHelper;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private PreviewCallback previewCallback;
	private AutoFocusCallback autoFocusCallback;
	private static  final int FOCUS_AREA_SIZE= 300;

	public CameraPreview(Context context, Camera camera, PreviewCallback previewCb, AutoFocusCallback autoFocusCb) {
		super(context);
		mCamera = camera;
		previewCallback = previewCb;
		autoFocusCallback = autoFocusCb;

		/*
		 * Set camera to continuous focus if supported, otherwise use software auto-focus. Only works for API level >=9.
		 */
		/*
		 * Camera.Parameters parameters = camera.getParameters(); for (String f : parameters.getSupportedFocusModes()) { if (f == Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) { mCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); autoFocusCallback = null; break; } }
		 */

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);

		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the preview.
		try {
			mCamera.cancelAutoFocus();
			setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                focusOnTouch(event);
		            }
					return true;
				}
			});
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
			BHelper.db("Error setting camera preview: " + e.getMessage());
		}
	}
	
	protected void focusOnTouch(MotionEvent event) {
		// TODO Auto-generated method stub
		if (mCamera != null ) {

	        Camera.Parameters parameters = mCamera.getParameters();
	        if (parameters.getMaxNumMeteringAreas() > 0){
	            Rect rect = calculateFocusArea(event.getX(), event.getY());

	            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
	            meteringAreas.add(new Camera.Area(rect, 800));
	            parameters.setFocusAreas(meteringAreas);

	            mCamera.setParameters(parameters);
	            mCamera.autoFocus(autoFocusCallback);
	        }else {
	            mCamera.autoFocus(autoFocusCallback);
	        }
	    }
	}
	private Rect calculateFocusArea(float x, float y) {
	    int left = clamp(Float.valueOf((x / getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
	    int top = clamp(Float.valueOf((y / getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

	    return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
	}

	private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
	    int result;
	    if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
	        if (touchCoordinateInCameraReper>0){
	            result = 1000 - focusAreaSize/2;
	        } else {
	            result = -1000 + focusAreaSize/2;
	        }
	    } else{
	         result = touchCoordinateInCameraReper - focusAreaSize/2;
	    }
	    return result;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		this.getHolder().removeCallback(this);
	    mCamera.release();
	    mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		/*
		 * If your preview can change or rotate, take care of those events here. Make sure to stop the preview before resizing or reformatting it.
		 */
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			e.printStackTrace();
			// ignore: tried to stop a non-existent preview
		}

		try {
			// Hard code camera surface rotation 90 degs to match Activity view in portrait
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(mHolder);
			mCamera.setPreviewCallback(previewCallback);
			mCamera.startPreview();
			mCamera.autoFocus(autoFocusCallback);
		} catch (Exception e) {
			e.printStackTrace();
			BHelper.db("Error starting camera preview: " + e.getMessage());
		}
	}
}
