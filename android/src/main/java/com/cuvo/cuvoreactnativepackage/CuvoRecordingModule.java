package com.cuvo.cuvoreactnativepackage;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.BaseJavaModule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CuvoRecordingModule extends ReactContextBaseJavaModule implements HBRecorderListener {

    public static final String NAME = "CuvoRecordingPackage";
    private static final int SCREEN_RECORD_REQUEST_CODE = 100;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 101;
    private static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = 102;
    HBRecorder hbRecorder;
    Button btnStart,btnStop;
    boolean hasPermissions;
    ContentValues contentValues;
    ContentResolver resolver;
    Uri mUri;
    private Activity activity;
    Callback callback;
    private ReactContext mReactContext;
    private ReactRootView mReactRootView;

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    public CuvoRecordingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mReactRootView = new ReactRootView(reactContext);

    }
    @ReactMethod
    public void startRecording(String input) {
        try {
            // image naming and path  to include sd card  appending name yoreactContext.addActivityEventListeneru choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/"  + ".jpg";

            activity = mReactContext.getCurrentActivity();
            //activity.setContentView(mReactRootView);

            // // System.out.print("Taking photo ------------------");

            hbRecorder = new HBRecorder(activity, this);
            hbRecorder.setVideoEncoder("H264");

            startRecordingScreen();

//            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)) {
//                hasPermissions = true;
//            }
//            if (hasPermissions) {
//                startRecordingScreen();
//            }

            // create bitmap screen capture
            View v1 = activity.getWindow().getDecorView().getRootView();


        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            // System.out.print("Take photo failed");
            e.printStackTrace();
        }
    }

    @Override
    public void HBRecorderOnStart() {
        Toast.makeText(activity, "Started", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void HBRecorderOnError(int errorCode, String reason) {
        Toast.makeText(activity, errorCode+": "+reason, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void HBRecorderOnComplete() {
        Toast.makeText(activity, "Completed", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Update gallery depending on SDK Level
            if (hbRecorder.wasUriSet()) {
                updateGalleryUri();
            }
        }
    }
    private void updateGalleryUri(){
        contentValues.clear();
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
        activity.getContentResolver().update(mUri, contentValues, null, null);
    }
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startRecordingScreen() {
        View v1 = activity.getWindow().getDecorView().getRootView();
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) v1.getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
        activity.startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
    }

    @ReactMethod
    public  void stopRecording(final Callback callback) {
        hbRecorder.stopScreenRecording();
        //final String uri = Uri.fromFile(tempVideoFile).toString();
        Log.w("TAG", "Testingnggngn --------------: ");
        callback.invoke("Testingnggngn");
    }
    private void startScreenRecord() {
        //hbRecorder.startScreenRecording(data, resultCode, this);
    }
    private void setOutputPath() {
        String filename = generateFileName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver = activity.getContentResolver();
            contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "SpeedTest/" + "SpeedTest");
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

    //Generate a timestamp to be used as a file name
    private String generateFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate).replace(" ", "");
    }
    //drawable to byte[]
    private byte[] drawable2ByteArray(@DrawableRes int drawableId) {
        Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
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
}
