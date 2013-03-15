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
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.htc.painting.engine.HtcPaintingView;
import com.htc.painting.engine.ViewPort;
import com.htc.painting.penmenu.PenMenu;
import com.htc.pen.PenEvent;

/**
 * Allows drawing on top of some web page content.
 *
 */
public class DrawSignatureActivity extends Activity {
    /**
     * View for drawn strokes.
     */
    private HtcPaintingView paintingView;
    
    private PenMenu penMenu;
       
	/**
     * Performs initialization.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // For this demo, always start with a clean slate when the signing
        // screen comes back up.
        super.onCreate(null);
        FileSerializeDAO dao = new FileSerializeDAO(this);
        dao.deleteAll();

        setContentView(R.layout.draw_signature);

        paintingView = (HtcPaintingView) findViewById(R.id.paintingView);
        paintingView.setEnabled(true);
        paintingView.init(dao);

        int group[] = {
            0
        };
        DefaultPaintingViewPort viewPort = new DefaultPaintingViewPort();
        DefaultPaintingViewPort viewPorts[] = {
            viewPort
        };
        paintingView.requestStrokeGroups(group, viewPorts);

        penMenu = (PenMenu) findViewById(R.id.pen_menu);
        penMenu.setPaintingView(paintingView);

        PenEvent.enablePenEvent(this, true);
        //penMenu.changeState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        penMenu.onDestroy();
        penMenu = null;
        paintingView.destroy();
        paintingView = null;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        penMenu.restoreSetting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        penMenu.saveSetting();
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        penMenu.OnRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        penMenu.OnSaveInstanceState(outState);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (penMenu.handleKeyEvent(keyCode, event) == true) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //Log.i("Pen Event Debug", "Pen Event Log Application side, action"
        //        + event.getAction() + " meta state:" + event.getMetaState());

        boolean isPen = PenEvent.isPenEvent(event);
        if (!isPen)
            return super.dispatchTouchEvent(event);
        int action = PenEvent.PenAction(event);
        int buttonStates = PenEvent.PenButton(event);
        switch (buttonStates) {
            case PenEvent.PEN_BUTTON_NONE:
                penMenu.setEraserMode(false);
                penMenu.forceHideStroke(false);
                break;

            case PenEvent.PEN_BUTTON2:
                if (action == PenEvent.PEN_ACTION_DOWN) {
                    penMenu.setEraserMode(true);
                }
                else if (action == PenEvent.PEN_ACTION_UP) {
                    penMenu.setEraserMode(false);
                }
                return super.dispatchTouchEvent(event);

            case PenEvent.PEN_BUTTON1:
                if (action == PenEvent.PEN_ACTION_DOWN)
                    penMenu.forceHideStroke(true);
                else if (action == PenEvent.PEN_ACTION_UP)
                    penMenu.forceHideStroke(false);
                return true;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * A ViewPort with good defaults for a full view drawing surface with no 
     * special features like offset, rotation, or scrolling.
     */
    public class DefaultPaintingViewPort extends ViewPort {
        @Override
        public float getOffsetX() {
            return (float) 0;
        }

        @Override
        public float getOffsetY() {
            return (float) 0;
        }

        @Override
        public float getScalingPivotX() {
            return 50f;
        }

        @Override
        public float getScalingPivotY() {
            return 50f;
        }

        @Override
        public float getScaleX() {
            return 1f;
        }

        @Override
        public float getScaleY() {
            return 1f;
        }

        @Override
        public RectF getRectF() {
            Rect rect = new Rect();
            paintingView.getDrawingRect(rect);
            RectF rectF = new RectF(rect);
            return rectF;
        }

        @Override
        public float getRotateDeg() {
            return 0;
        }

        @Override
        public float getRotatePivotX() {
            return 0;
        }

        @Override
        public float getRotatePivotY() {
            return 0;
        }
    }
}
