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

import com.htc.painting.engine.AbsSerializeDAO;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
  * Persists stroke data to files.
  *
  */
public class FileSerializeDAO extends AbsSerializeDAO {

    private static String TAG = "FileSerializeDAO";

    private static boolean sEnableDebugLog = true;
    
    private static boolean sEnableErrorLog = true;
    
    private static final String FILE_PREFIX = "serialize";

    private Context mContext = null;

    public FileSerializeDAO(Context ctx) {
        mContext = ctx.getApplicationContext();
    }

    @Override
    public byte[] load(int drawerGroupId) {
        if (sEnableDebugLog) {
            Log.i(TAG, "load");
        }

        FileInputStream fis = null;
        byte[] returnByteArray = null;

        try {
            fis = mContext.openFileInput(FILE_PREFIX + drawerGroupId);
            if (fis != null) {
                returnByteArray = new byte[fis.available()];
                fis.read(returnByteArray);
                return returnByteArray;
            }
        } catch (IOException e) {
            if (sEnableErrorLog) {
                Log.e(TAG, "load", e);
            }
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    if (sEnableErrorLog) {
                        Log.e(TAG, "load finally", e);
                    }
                }
        }

        return null;
    }

    @Override
    public void save(int drawerGroupId, byte[] byteArray) {
        if (sEnableDebugLog) {
            Log.i(TAG, "save");
        }

        if (byteArray == null) {
            delete(drawerGroupId);
            return;
        }

        FileOutputStream fos = null;

        try {
            fos = mContext
                    .openFileOutput(FILE_PREFIX + drawerGroupId, Context.MODE_WORLD_WRITEABLE);
            if (fos != null) {
                fos.write(byteArray);
            }

        } catch (IOException e) {
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }

    }

    @Override
    public void delete(int drawerGroupId) {
        File folder = mContext.getFilesDir();
        File file = new File(folder.getPath() + File.separator + FILE_PREFIX + drawerGroupId);
        file.delete();
    }

    @Override
    public void deleteAll() {
        File folder = mContext.getFilesDir();
        for (String fileName : folder.list() ) {
			if ( fileName.startsWith(FILE_PREFIX) ) {
	        	File file = new File(folder.getPath() + File.separator + FILE_PREFIX + 0);
	        	file.delete();
			}
		}
    }

}
