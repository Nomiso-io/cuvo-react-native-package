package com.cuvo.cuvoreactnativepackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.projection.MediaProjectionManager;
import androidx.annotation.NonNull;

import android.net.Uri;
import android.os.Environment;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class ScreenRecordingModule extends ReactContextBaseJavaModule {

    public static final String NAME = "ScreenRecordingPackage";
    private static final int CAST_PERMISSION_CODE = 22;
    private DisplayMetrics mDisplayMetrics;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaRecorder mMediaRecorder;
    private MediaProjectionManager mProjectionManager;
    private Activity activity;
    private  String filePath;
    private int mScreenDensity;
    private Surface mSurface;
    private SurfaceView mSurfaceView;
    private static final String TEMP_FILE_PREFIX = "ReactNative-ScreenRecord-video";
    public static final String videoFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/MediaProjection.mp4";
    private ReactContext mReactContext;
    private ReactRootView mReactRootView;
    
    Callback callback;

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
            if (requestCode != CAST_PERMISSION_CODE) {
                // Where did we get this request from ? -_-
                Log.w("TAG", "Unknown request code: " + requestCode);
                return;
            }
            if (resultCode != activity.RESULT_OK) {
                //Toast.makeText(activity, "Screen Cast Permission Denied :(", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.w("TAG", "resultCode --------------: " + resultCode);
            Log.w("TAG", "requestCode --------------: " + requestCode);
            Log.w("TAG", "data --------------: " + data);

            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            // TODO Register a callback that will listen onStop and release & prepare the recorder for next recording
            // mMediaProjection.registerCallback(callback, null);

//        Toast.makeText(activity, "getVirtualDisplay :(", Toast.LENGTH_SHORT).show();

            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
//            mMediaProjection.createVirtualDisplay(
//                    "sample",
//                    1080, 1920, displayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                    mMediaRecorder.getSurface(), null, null);

            mVirtualDisplay = getVirtualDisplay();
            mMediaRecorder.start();
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

        mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mScreenDensity = mDisplayMetrics.densityDpi;

        mMediaRecorder = createRecorder();

        mProjectionManager = (MediaProjectionManager) activity.getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);


//        prepareRecording();
        startScreenRecording("");
    }
    private MediaRecorder createRecorder() {

        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        MediaRecorder mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        mediaRecorder.setVideoEncodingBitRate(1024 * 1000);
        mediaRecorder.setVideoEncodingBitRate(camcorderProfile.videoBitRate);
//        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoFrameRate(camcorderProfile.videoFrameRate);

//        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

//        mediaRecorder.setVideoSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
        mediaRecorder.setVideoSize(1080, 1920);
//        mediaRecorder.setVideoSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
        mediaRecorder.setOutputFile(videoFile);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("TAG", "ExceptionExceptionException --------------: " + e);
        }
        return mediaRecorder;
    }
    private void startScreenRecording(String input) {
        // If mMediaProjection is null that means we didn't get a context, lets ask the user
        if (mMediaProjection == null) {
            // This asks for user permissions to capture the screen
            View v1 = activity.getWindow().getDecorView().getRootView();
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)  v1.getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            activity.startActivityForResult(mProjectionManager.createScreenCaptureIntent(), CAST_PERMISSION_CODE);
            return;
        }
        mVirtualDisplay = getVirtualDisplay();
        mMediaRecorder.start();
    }

    @ReactMethod
    public void stopRecording(final Callback callback) {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        prepareRecording();
        callback.invoke(filePath);
    }

    public String getCurSysDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate).replace(" ", "");
    }

    private void prepareRecording() {
        try {
            final String directory = Environment.getExternalStorageDirectory() + File.separator + "Recordings";
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return;
            }
            String videoName = ("capture_" + getCurSysDate());
            File outputFile = null;
            outputFile = createTempFile(getReactApplicationContext(), "mp4", videoName);
            FileOutputStream fos;
            filePath = outputFile.toString();

            try {
                fos = new FileOutputStream(outputFile);
                int width = mDisplayMetrics.widthPixels;
                int height = mDisplayMetrics.heightPixels;

//                 mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                 mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//                 mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                 mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//                 mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                 mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
//                 mMediaRecorder.setVideoSize(width, height);
//                 mMediaRecorder.setVideoFrameRate(30);
//                 mMediaRecorder.setOutputFile(filePath);

                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mMediaRecorder.setVideoFrameRate(30);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mMediaRecorder.setVideoSize(width, height);
                mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
                mMediaRecorder.setOutputFile(filePath);

                try {
                    mMediaRecorder.prepare();
                    Log.w("TAG", "prepare --------------: " + filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w("TAG", "ExceptionExceptionException --------------: " + e);
                    return;
                }
                mSurface = mMediaRecorder.getSurface();

                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                callback.invoke(e.getMessage());
            } catch (IOException e) {
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
    private VirtualDisplay getVirtualDisplay() {
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;
        Log.w("TAG", "width --------------: " + width);
        if (mMediaRecorder.getSurface().isValid()){  // get surface again
            Log.w("Notice","Surface holder is valid");
        }
        else {
            Log.w("Notice","Surface holder ISNOT valid");  //Always receive this
        }
        return mMediaProjection.createVirtualDisplay(NAME, width, height, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }
}