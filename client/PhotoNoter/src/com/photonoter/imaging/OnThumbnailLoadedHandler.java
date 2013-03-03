
package com.photonoter.imaging;

import android.os.Handler;
import android.os.Message;
import android.view.View;


public class OnThumbnailLoadedHandler extends Handler {

    // Receive thumbnails generated in the background and set image views to
    // them.
    @Override
    public void handleMessage(final Message aMsg) {

        final LoadedBitmap loaded = (LoadedBitmap)aMsg.obj;
        if (null == loaded || null == loaded.view) {
            return;
        }

        final Integer tag = (Integer)loaded.view.getTag();
        if (null == tag) {
            return;
        }

        // If the view is still tagged to still have this image's thumbnail.
        if (tag.equals(loaded.id)) {
            if (null != loaded.thumbnail) {
                loaded.view.setImageBitmap(loaded.thumbnail);
            } else {
                loaded.view.setVisibility(View.GONE);
            }
            // mImageGrid.requestLayout();
            // mImageGrid.invalidate();
        }
    }
}
