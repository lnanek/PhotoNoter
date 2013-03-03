
package com.photonoter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;

public class BackgroundThumbnailLoader extends Handler {

    private final Context mContext;

    private final OnThumbnailLoadedHandler mUiHandler;

    public BackgroundThumbnailLoader(final Context aContext, final OnThumbnailLoadedHandler aHandler) {
        super(getThread().getLooper());
        mContext = aContext;
        mUiHandler = aHandler;
    }

    private static HandlerThread getThread() {
        final HandlerThread thread = new HandlerThread("BackgroundThumbnailLoader");
        thread.start();
        return thread;
    }

    @Override
    public void handleMessage(final Message aMsg) {
        // That creates thumbnails and sends them back to the UI thread.
        final int imageId = aMsg.arg1;
        if (-1 == imageId) {
            return;
        }

        final LoadedBitmap loaded = new LoadedBitmap();
        loaded.view = (ImageView)aMsg.obj;
        loaded.id = imageId;
        loaded.thumbnail = MediaStoreUtil.waitForImageThumbnail(mContext, imageId);
        mUiHandler.sendMessage(mUiHandler.obtainMessage(0, 0, 0, loaded));
    }

}
