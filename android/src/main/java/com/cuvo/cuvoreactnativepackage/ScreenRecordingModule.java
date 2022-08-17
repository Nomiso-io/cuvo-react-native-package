package com.cuvo.cuvoreactnativepackage;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjectionManager;
import android.content.Context;
import android.content.ContentResolver;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.Callback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class ScreenRecordingModule extends ReactContextBaseJavaModule implements HBRecorderListener {

    public static final String NAME = "ScreenRecordingPackage";
    private static final int SCREEN_RECORD_REQUEST_CODE = 100;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 101;
    private static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = 102;
    HBRecorder hbRecorder;
    boolean hasPermissions;
    private Activity activity;
    private ReactContext mReactContext;
    private ReactRootView mReactRootView;
    private static final int CAST_PERMISSION_CODE = 22;
    ContentValues contentValues;
    ContentResolver resolver;

    Uri mUri;

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
           if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
               if (resultCode == activity.RESULT_OK) {
                   //Start screen recording
                    setOutputPath();
                    hbRecorder.startScreenRecording(data, resultCode);
               }
           }
        }
    };


    public ScreenRecordingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mReactRootView = new ReactRootView(reactContext);
        reactContext.addActivityEventListener(activityEventListener);

    }
    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        getReactApplicationContext().removeActivityEventListener(activityEventListener);
    }
    @ReactMethod
    public void startRecording(String input) {
        activity = mReactContext.getCurrentActivity();

        hbRecorder = new HBRecorder(activity, this);
        hbRecorder.setVideoEncoder("H264");
        startScreenRecording("");
    }

    private void startScreenRecording(String input) {
        View v1 = activity.getWindow().getDecorView().getRootView();
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)  v1.getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
       activity.startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
    }

    @ReactMethod
    public void stopRecording(final Callback callback) {
        hbRecorder.stopScreenRecording();

        callback.invoke("file://"+hbRecorder.getFilePath());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setOutputPath() {
        String filename = generateFileName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver = activity.getContentResolver();
            contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies");
            contentValues.put(MediaStore.Video.Media.TITLE, filename);
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            mUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            //FILE NAME SHOULD BE THE SAME
            hbRecorder.setFileName(filename);
            hbRecorder.setOutputUri(mUri);
        }else{
            createFolder();
            hbRecorder.setOutputPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) +"/HBRecorder");
        }
    }
    //Check if permissions was granted
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }
    private void updateGalleryUri(){
        contentValues.clear();
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
        //getContentResolver().update(mUri, contentValues, null, null);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void refreshGalleryFile() {
        MediaScannerConnection.scanFile(activity,
                new String[]{hbRecorder.getFilePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
    //Generate a timestamp to be used as a file name
    private String generateFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate).replace(" ", "");
    }
    //drawable to byte[]
//    private byte[] drawable2ByteArray(@DrawableRes int drawableId) {
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), drawableId);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        return stream.toByteArray();
//    }
    //Create Folder
    //Only call this on Android 9 and lower (getExternalStoragePublicDirectory is deprecated)
    //This can still be used on Android 10> but you will have to add android:requestLegacyExternalStorage="true" in your Manifest
    private void createFolder() {
        File f1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "SpeedTest");
        if (!f1.exists()) {
            if (f1.mkdirs()) {
                Log.i("Folder ", "created");
            }
        }
    }

    @Override
    public void HBRecorderOnStart() {
        //Toast.makeText(activity, "Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void HBRecorderOnComplete() {
        //Toast.makeText(activity, "Completed", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Update gallery depending on SDK Level
//            if (hbRecorder.wasUriSet()) {
//                updateGalleryUri();
//            }else{
//                refreshGalleryFile();
//            }
        }
    }

    @Override
    public void HBRecorderOnError(int errorCode, String reason) {
        //Toast.makeText(activity, errorCode+": "+reason, Toast.LENGTH_SHORT).show();
    }
}