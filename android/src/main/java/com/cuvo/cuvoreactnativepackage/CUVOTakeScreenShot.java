package com.cuvo.cuvoreactnativepackage;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.view.View;
import android.app.Activity;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class CUVOTakeScreenShot extends ReactContextBaseJavaModule  {

    Callback callback;

    private static final String TEMP_FILE_PREFIX = "ReactNative-snapshot-image";

    public CUVOTakeScreenShot(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void takePhoto(String input,  final Callback callback) {
        try {
        // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/"  + ".jpg";

            final Activity activity = getCurrentActivity();
            // // System.out.print("Taking photo ------------------");

            // create bitmap screen capture
            View v1 = activity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File outputFile = null;
            outputFile = createTempFile(getReactApplicationContext(), "jpg", "scrnshot");
            FileOutputStream fos;
            final String uri = Uri.fromFile(outputFile).toString();
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                callback.invoke(uri);
            } catch (FileNotFoundException e) {
                //Log.e("GREC", e.getMessage(), e);
                callback.invoke(e.getMessage());
            } catch (IOException e) {
                //Log.e("GREC", e.getMessage(), e);
                callback.invoke("IOException");
            }
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            // System.out.print("Take photo failed");
            e.printStackTrace();
        }
    }

    @NonNull
    private File createTempFile(@NonNull final Context context, @NonNull final String ext, String fileName) throws IOException {
        final File externalCacheDir = context.getExternalCacheDir();
        final File internalCacheDir = context.getCacheDir();
        final File cacheDir;

        if (externalCacheDir == null && internalCacheDir == null) {
            throw new IOException("No cache directory available");
        }

        if (externalCacheDir == null) {
            cacheDir = internalCacheDir;
        } else if (internalCacheDir == null) {
            cacheDir = externalCacheDir;
        } else {
            cacheDir = externalCacheDir.getFreeSpace() > internalCacheDir.getFreeSpace() ?
                    externalCacheDir : internalCacheDir;
        }

        final String suffix = "." + ext;
        if (fileName != null) {
            return File.createTempFile(fileName, suffix, cacheDir);
        }
        return File.createTempFile(TEMP_FILE_PREFIX, suffix, cacheDir);
    }
    @Override
    public String getName() {
        return "CUVOTakeScreenShot";
    }
}
