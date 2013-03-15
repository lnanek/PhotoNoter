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

package com.htc.sample.pen.phonegap;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.htc.pen.PenEvent;

/**
 * Captures and displays some pen data.
 * 
 */
public class PenCaptureActivity extends Activity {

	private ImageView penTipGlow;
    
    private ImageView penButton1Glow;
    
    private ImageView penButton2Glow;
    
    private ImageView penBackGlow;
    
    private TextView readout;
    
    private CrosshairView crosshairs;
    
    private View content;
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readout);

        penTipGlow = (ImageView) findViewById(R.id.pen_tip_glow);
        penButton1Glow = (ImageView) findViewById(R.id.pen_button_1_glow);
        penButton2Glow = (ImageView) findViewById(R.id.pen_button_2_glow);
        penBackGlow = (ImageView) findViewById(R.id.pen_back_glow);
        crosshairs = (CrosshairView) findViewById(R.id.crosshairs);
        content = findViewById(R.id.content);
        readout = (TextView) findViewById(R.id.readout);
    }
    
    protected void setGlowVisible(final boolean tip, final boolean button1, final boolean button2, final boolean end) {
    	penTipGlow.setVisibility(tip ? View.VISIBLE : View.INVISIBLE);
    	penButton1Glow.setVisibility(button1 ? View.VISIBLE : View.INVISIBLE);
    	penButton2Glow.setVisibility(button2 ? View.VISIBLE : View.INVISIBLE);
    	penBackGlow.setVisibility(end ? View.VISIBLE : View.INVISIBLE);
    }
    
    protected String getPenData(final MotionEvent ev) {
    	return "\npressure : " + ev.getPressure() + getData(ev);
    }
    
    protected String getData(final MotionEvent ev) {
    	return "\nx : " + ev.getX() + "\ny : " + ev.getY();
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent touchEvent) {
    	
    	crosshairs.setLocation(touchEvent.getX(), touchEvent.getY());
       	content.postInvalidate();
    	
        final boolean isPenEvent = PenEvent.isPenEvent(touchEvent);
        if ( !isPenEvent ) {
        	readout.setText("last event: finger or pen back" + getPenData(touchEvent));
        	setGlowVisible(false, false, false, true);
        	return super.dispatchTouchEvent(touchEvent);
        }
        
        penTipGlow.setAlpha(touchEvent.getPressure() / 2 + 0.5f);
        
       	final int button = PenEvent.PenButton(touchEvent);
        if ( button == PenEvent.PEN_BUTTON1 ) {
        	readout.setText("last event: pen with button 1 held" + getPenData(touchEvent));
        	setGlowVisible(true, true, false, false);    
        	return super.dispatchTouchEvent(touchEvent);        	
        }
        
        if ( button == PenEvent.PEN_BUTTON2 ) {
        	readout.setText("last event: pen with button 2 held" + getPenData(touchEvent));
        	setGlowVisible(true, false, true, false);    
        	return super.dispatchTouchEvent(touchEvent);        	
        }

    	readout.setText("last event: pen" + getPenData(touchEvent));
       	setGlowVisible(true, false, false, false);    
       	
        return super.dispatchTouchEvent(touchEvent);
    }
    
}
