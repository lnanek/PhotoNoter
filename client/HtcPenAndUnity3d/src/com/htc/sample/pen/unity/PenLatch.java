/*
 * Copyright (C) 2011 HTC Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htc.sample.pen.unity;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;

import com.htc.pen.PenEvent;

/**
 * Retains information about recent pen events.
 * This allows checking input once per frame,
 * a popular method used to check input in games
 * that helps prevent excess CPU use and garbage
 * collection and so helps maintain high frame
 * rate.
 */
public class PenLatch {
	
	/**
	 * The one instance of this class permitted.
	 */
	public static final PenLatch INSTANCE = new PenLatch();
	
	/**
	 * The tag to be output for logging messages.
	 */
	private static final String LOG_TAG = PenLatch.class.getSimpleName();
	
	/**
	 * The maximum number of pen touches to record per frame.
	 * Touches beyond this are discarded.
	 */
	private static final int MAX_TOUCHES_RECORDED_PER_FRAME = 100;
	
	private boolean enabled;
	
	private boolean anyPen;
	
	private float penPositionX;
		
	private float penPositionY;
	
	private boolean anyPenDown;
	
	private float pressure;

	private boolean button1;
	
	private boolean button1Down;
	
	private boolean button1Up;
	
	private boolean button2;
	
	private boolean button2Down;
	
	private boolean button2Up;
	
	private int touchesCount;
	
	private float[] touchesX = new float[MAX_TOUCHES_RECORDED_PER_FRAME];

	private float[] touchesY = new float[MAX_TOUCHES_RECORDED_PER_FRAME];

	private float[] touchesPressure = new float[MAX_TOUCHES_RECORDED_PER_FRAME];
	
	private int[] touchesPhase = new int[MAX_TOUCHES_RECORDED_PER_FRAME];
	
	private PenLatch() {}

	/**
	 * Reset the recorded values to none;
	 */
	public void reset() {
		anyPen = false;
		anyPenDown = false;
		penPositionX = Float.NaN;
		penPositionY = Float.NaN;
		penPositionY = Float.NaN;
		anyPenDown = false;
		pressure = Float.NaN;
		button1 = false;
		button1Down = false;
		button1Up = false;
		button2 = false;
		button2Down = false;
		button2Up = false;
		touchesCount = 0;
	}
	
	public boolean dispatchTouchEvent(final Activity activity, final MotionEvent ev) {
		if (!enabled) {
	        if ( Flags.LOG ) {
	        	Log.d(LOG_TAG, "dispatchTouchEvent translating pen events");
	        }
			PenTranslator.translateTouchEvent(activity, ev);
			return false;
		}

        if ( Flags.LOG ) {
        	Log.d(LOG_TAG, "dispatchTouchEvent recording pen events");
        }
		return recordPenEvent(ev);
	}
	
	/**
	 * Records any pen event data that occurred in this motion event.
	 * @param touchEvent MotionEvent that occurred
	 */
    private boolean recordPenEvent(final MotionEvent touchEvent) {
        if ( !PenEvent.isPenEvent(touchEvent) ) {
            if ( Flags.LOG ) {
            	Log.d(LOG_TAG, "recordPenEvent passing non-pen event on");
            }
        	return false;
        }

    	anyPen = true;
    	if ( touchEvent.getAction() == MotionEvent.ACTION_DOWN ) {
    		anyPenDown = true;
    	}
    	penPositionX = touchEvent.getX();
    	penPositionY = touchEvent.getY();
    	pressure = touchEvent.getPressure();

    	touchesCount++;
    	if (touchesCount >= MAX_TOUCHES_RECORDED_PER_FRAME) {
    		if (Flags.LOG) {
    			Log.w(LOG_TAG, "Warning Will Robinson! Max touches recorded per frame reached!");
    		}
    	} else {
    		final int touchesIndex = touchesCount - 1;
    		touchesX[touchesIndex] = penPositionX;
    		touchesY[touchesIndex] = penPositionY;
    		touchesPressure[touchesIndex] = pressure;
    		touchesPhase[touchesIndex] = touchEvent.getAction();
    	}
    	        	
        final int penButton = PenEvent.PenButton(touchEvent);
        if (PenEvent.PEN_BUTTON1 == penButton && !button1) {
       		button1Down = true;
        	button1 = true;
        } else if ( PenEvent.PEN_BUTTON1 != penButton && button1 ) {
       		button1Up = true;
        }
        
        if (PenEvent.PEN_BUTTON2 == penButton && !button2) {
       		button2Down = true;
        	button2 = true;
        } else if ( PenEvent.PEN_BUTTON2 != penButton && button2 ) {
       		button2Up = true;
        }

        if ( Flags.LOG ) {
        	Log.d(LOG_TAG, "recordPenEvent recording and consuming pen event");
        }
        return true;
    }
    
    /**
     * Returns the average of an array of data.
     * @param data float[] data
     * @param count int indicating the amount of data elements to average
     * @return average value of the requested elements or NaN if none
     */
    private float average(final float[] data, final int count) {
    	if ( 0 == count ) {
    		return Float.NaN;
    	}

    	float average = 0;
    	for(int i = 0; i < count; i++) {
    		average += data[i];
    	}
    	average /= count;
    	return average;
    }

	/**
	 * If any pen event has occurred since reset was called.
	 */
	public boolean isAnyPen() {
		return anyPen;
	}

	/**
	 * The latest x position of the pen or NaN if no pen events since reset was called.
	 */
	public float getPenPositionX() {
		return penPositionX;
	}

	/**
	 * The average x position of the pen or NaN if no pen events since reset was called.
	 */
	public float getPenPositionXAverage() {
		return average(touchesX, touchesCount);
	}
	
	/**
	 * The latest y position of the pen or NaN if no pen events since reset was called.
	 */
	public float getPenPositionY() {
		return penPositionY;
	}
	
	/**
	 * The average y position of the pen or NaN if no pen events since reset was called.
	 */
	public float getPenPositionYAverage() {
		return average(touchesY, touchesCount);
	}

	/**
	 * Returns true the first time after reset that a pen was touched down on to the screen.
	 */
	public boolean isAnyPenDown() {
		return anyPenDown;
	}

	/** 
	 * Returns the latest pen pressure of the last event since reset.
	 */
	public float getPressure() {
		return pressure;
	}

	/**
	 * Returns the average pen pressure of the last event since reset.
	 */
	public float getPressureAverage() {
		return average(touchesPressure, touchesCount);
	}

	/**
	 * Returns true if the first button on the pen has been used since reset.
	 */
	public boolean isButton1() {
		return button1;
	}
	
	/**
	 * Returns true if the first button on the pen has been pressed down since reset.
	 */
	public boolean isButton1Down() {
		return button1Down;
	}

	/**
	 * Returns true if the first button on the pen has been released since reset.
	 */
	public boolean isButton1Up() {
		return button1Up;
	}
	
	/**
	 * Returns true if the second button on the pen has been used since reset.
	 */
	public boolean isButton2() {
		return button2;
	}

	/**
	 * Returns true if the second button on the pen has been pressed since reset.
	 */
	public boolean isButton2Down() {
		return button2Down;
	}

	/**
	 * Returns true if the second button on the pen has been released since reset.
	 */
	public boolean isButton2Up() {
		return button2Up;
	}

	/**
	 * Returns the number of touches recorded.
	 */
	public int getTouchesCount() {
		return touchesCount;
	}

	/**
	 * Returns the x positions of the recorded touches.
	 */
	public float[] getTouchesX() {
		return touchesX;
	}

	/**
	 * Returns the y positions of the recorded touches.
	 */
	public float[] getTouchesY() {
		return touchesY;
	}

	/**
	 * Returns the pressure of the recorded touches.
	 */
	public float[] getTouchesPressure() {
		return touchesPressure;
	}
	
	/**
	 * Returns the action of the recorded touches.
	 * These match MotionEvent.ACTION_DOWN, 
	 * MotionEvent.ACTION_MOVE, and MotionEvent.ACTION_UP.
	 */
	public int[] getTouchesPhase() {
		return touchesPhase;
	}
	
	/**
	 * Enables recording of pen events for handling by Unity scripts.
	 */
	public void enable() {
		enabled = true;		
		reset();
	}
	
	/**
	 * Disables recording of pen events.
	 * Enables translating them into more standard actions like back and menu.
	 */
	public void disable() {
		reset();
		enabled = false;
	}

}
