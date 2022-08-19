package com.cuvo.cuvoreactnativepackage;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import android.os.ResultReceiver;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.cuvo.cuvoreactnativepackage.Constants.ERROR_KEY;
import static com.cuvo.cuvoreactnativepackage.Constants.ERROR_REASON_KEY;
import static com.cuvo.cuvoreactnativepackage.Constants.MAX_FILE_SIZE_REACHED_ERROR;
import static com.cuvo.cuvoreactnativepackage.Constants.MAX_FILE_SIZE_KEY;
import static com.cuvo.cuvoreactnativepackage.Constants.NO_SPECIFIED_MAX_SIZE;
import static com.cuvo.cuvoreactnativepackage.Constants.ON_COMPLETE;
import static com.cuvo.cuvoreactnativepackage.Constants.ON_COMPLETE_KEY;
import static com.cuvo.cuvoreactnativepackage.Constants.ON_START;
import static com.cuvo.cuvoreactnativepackage.Constants.ON_START_KEY;
import static com.cuvo.cuvoreactnativepackage.Constants.SETTINGS_ERROR;

/**
 * Created by HBiSoft on 13 Aug 2019
 * Copyright (c) 2019 . All rights reserved.
 */

public class ScreenRecordService extends Service {

    private static final String TAG = "ScreenRecordService";
    private long maxFileSize = NO_SPECIFIED_MAX_SIZE;
    private boolean hasMaxFileBeenReached = false;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;
    private boolean isVideoHD;
    private boolean isAudioEnabled;
    private String path;

    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;
    private String name;
    private int audioBitrate;
    private int audioSamplingRate;
    private static String filePath;
    private static String fileName;
    private int audioSourceAsInt;
    private int videoEncoderAsInt;
    private boolean isCustomSettingsEnabled;
    private int videoFrameRate;
    private int videoBitrate;
    private int outputFormatAsInt;
    private int orientationHint;

    public final static String BUNDLED_LISTENER = "listener";
    private Uri returnedUri = null;
    private Intent mIntent;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        boolean isAction = false;

        //Check if there was an action called
        if (intent != null && intent.getAction() != null){
            isAction = true;
        }
        //If there was an action, check what action it was
        //Called when recording should be paused or resumed
        if (isAction){
            //Pause Recording
            if (intent.getAction().equals("pause")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pauseRecording();
                }
            }
            //Resume Recording
            else if (intent.getAction().equals("resume")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resumeRecording();
                }
            }
        }
        //Start Recording
        else {
            //Get intent extras
            hasMaxFileBeenReached = false;
            mIntent = intent;
            maxFileSize = intent.getLongExtra(MAX_FILE_SIZE_KEY, NO_SPECIFIED_MAX_SIZE);
            byte[] notificationSmallIcon = intent.getByteArrayExtra("notificationSmallBitmap");
            int notificationSmallVector = intent.getIntExtra("notificationSmallVector", 0);
            String notificationTitle = intent.getStringExtra("notificationTitle");
            String notificationDescription = intent.getStringExtra("notificationDescription");
            String notificationButtonText = intent.getStringExtra("notificationButtonText");
            orientationHint = intent.getIntExtra("orientation", 400);
            mResultCode = intent.getIntExtra("code", -1);
            mResultData = intent.getParcelableExtra("data");
            mScreenWidth = intent.getIntExtra("width", 0);
            mScreenHeight = intent.getIntExtra("height", 0);

            if (intent.getStringExtra("mUri") != null) {
                returnedUri = Uri.parse(intent.getStringExtra("mUri"));
            }

            if (mScreenHeight == 0 || mScreenWidth == 0) {
                HBRecorderCodecInfo hbRecorderCodecInfo = new HBRecorderCodecInfo();
                hbRecorderCodecInfo.setContext(this);
                mScreenHeight = hbRecorderCodecInfo.getMaxSupportedHeight();
                mScreenWidth = hbRecorderCodecInfo.getMaxSupportedWidth();
            }

            mScreenDensity = intent.getIntExtra("density", 1);
            isVideoHD = intent.getBooleanExtra("quality", true);
            isAudioEnabled = intent.getBooleanExtra("audio", true);
            path = intent.getStringExtra("path");
            name = intent.getStringExtra("fileName");
            String audioSource = intent.getStringExtra("audioSource");
            String videoEncoder = intent.getStringExtra("videoEncoder");
            videoFrameRate = intent.getIntExtra("videoFrameRate", 30);
            videoBitrate = intent.getIntExtra("videoBitrate", 40000000);

            if (audioSource != null) {
                setAudioSourceAsInt(audioSource);
            }
            if (videoEncoder != null) {
                setvideoEncoderAsInt(videoEncoder);
            }

            filePath = name;
            audioBitrate = intent.getIntExtra("audioBitrate", 128000);
            audioSamplingRate = intent.getIntExtra("audioSamplingRate", 44100);
            String outputFormat = intent.getStringExtra("outputFormat");
            if (outputFormat != null) {
                setOutputFormatAsInt(outputFormat);
            }

            isCustomSettingsEnabled = intent.getBooleanExtra("enableCustomSettings", false);

            //Set notification notification button text if developer did not
            if (notificationButtonText == null) {
                notificationButtonText = "STOP RECORDING";
            }
            //Set notification bitrate if developer did not
            if (audioBitrate == 0) {
                audioBitrate = 128000;
            }
            //Set notification sampling rate if developer did not
            if (audioSamplingRate == 0) {
                audioSamplingRate = 44100;
            }
            //Set notification title if developer did not
            if (notificationTitle == null || notificationTitle.equals("")) {
                notificationTitle = "Drag down to stop the recording";
            }
            //Set notification description if developer did not
            if (notificationDescription == null || notificationDescription.equals("")) {
                notificationDescription = "Recording your screen";
            }

            //Notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "001";
                String channelName = "RecordChannel";
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
                channel.setLightColor(Color.BLUE);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                    Notification notification;

                    Intent myIntent = new Intent(this, NotificationReceiver.class);
                    PendingIntent pendingIntent;

                    if (Build.VERSION.SDK_INT >= 31){
                        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_IMMUTABLE);
                    }else{
                        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

                    }

                    Notification.Action action = new Notification.Action.Builder(
                            Icon.createWithResource(this, android.R.drawable.presence_video_online),
                            notificationButtonText,
                            pendingIntent).build();
                    if (notificationSmallIcon != null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(notificationSmallIcon, 0, notificationSmallIcon.length);
                        //Modify notification badge
                        notification = new Notification.Builder(getApplicationContext(), channelId).setOngoing(true).setSmallIcon(Icon.createWithBitmap(bmp)).setContentTitle(notificationTitle).setContentText(notificationDescription).addAction(action).build();
                    } else if (notificationSmallVector != 0){
                        notification = new Notification.Builder(getApplicationContext(), channelId).setOngoing(true).setSmallIcon(notificationSmallVector).setContentTitle(notificationTitle).setContentText(notificationDescription).addAction(action).build();
                    }
                    else {
                        //Modify notification badge
                        notification = new Notification.Builder(getApplicationContext(), channelId).setOngoing(true).setSmallIcon(notificationSmallVector).setContentTitle(notificationTitle).setContentText(notificationDescription).addAction(action).build();
//                        notification = new Notification.Builder(getApplicationContext(), channelId).setOngoing(true).setSmallIcon(R.drawable.icon).setContentTitle(notificationTitle).setContentText(notificationDescription).addAction(action).build();
                    }
                    startForeground(101, notification);
                }
            } else {
                startForeground(101, new Notification());
            }
            if (returnedUri == null) {
                if (path == null) {
                    path = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
                }
            }
            //Init MediaRecorder
            try {
                initRecorder();
            } catch (Exception e) {
                ResultReceiver receiver = intent.getParcelableExtra(ScreenRecordService.BUNDLED_LISTENER);
                Bundle bundle = new Bundle();
                bundle.putString(ERROR_REASON_KEY, Log.getStackTraceString(e));
                if (receiver != null) {
                    receiver.send(Activity.RESULT_OK, bundle);
                }
            }
            //Init MediaProjection
            try {
                initMediaProjection();
            } catch (Exception e) {
                ResultReceiver receiver = intent.getParcelableExtra(ScreenRecordService.BUNDLED_LISTENER);
                Bundle bundle = new Bundle();
                bundle.putString(ERROR_REASON_KEY, Log.getStackTraceString(e));
                if (receiver != null) {
                    receiver.send(Activity.RESULT_OK, bundle);
                }
            }
            //Init VirtualDisplay
            try {
                initVirtualDisplay();
            } catch (Exception e) {
                ResultReceiver receiver = intent.getParcelableExtra(ScreenRecordService.BUNDLED_LISTENER);
                Bundle bundle = new Bundle();
                bundle.putString(ERROR_REASON_KEY, Log.getStackTraceString(e));
                if (receiver != null) {
                    receiver.send(Activity.RESULT_OK, bundle);
                }
            }

            mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int what, int extra) {
                    if ( what == 268435556 && hasMaxFileBeenReached) {
                        // Benign error b/c recording is too short and has no frames. See SO: https://stackoverflow.com/questions/40616466/mediarecorder-stop-failed-1007
                        return;
                    }
                    ResultReceiver receiver = intent.getParcelableExtra(ScreenRecordService.BUNDLED_LISTENER);
                    Bundle bundle = new Bundle();
                    bundle.putInt(ERROR_KEY, SETTINGS_ERROR);
                    bundle.putString(ERROR_REASON_KEY, String.valueOf(what));
                    if (receiver != null) {
                        receiver.send(Activity.RESULT_OK, bundle);
                    }
                }
            });

            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                        hasMaxFileBeenReached = true;
                        Log.i(TAG,String.format(Locale.US,"onInfoListen what : %d | extra %d", what, extra));
                        ResultReceiver receiver = intent.getParcelableExtra(ScreenRecordService.BUNDLED_LISTENER);
                        Bundle bundle = new Bundle();
                        bundle.putInt(ERROR_KEY, MAX_FILE_SIZE_REACHED_ERROR);
                        bundle.putString(ERROR_REASON_KEY, "File size max has been reached.");
                        if (receiver != null) {
                            receiver.send(Activity.RESULT_OK, bundle);
                        }
                    }
                }
            });
            //Start Recording
            try {
                mMediaRecorder.start();
                ResultReceiver receiver = intent.getParcelableExtra(ScreenRecordService.BUNDLED_LISTENER);
                Bundle bundle = new Bundle();
                bundle.putInt(ON_START_KEY, ON_START);
                if (receiver != null) {
                    receiver.send(Activity.RESULT_OK, bundle);
                }
            } catch (Exception e) {
                // From the tests I've done, this can happen if another application is using the mic or if an unsupported video encoder was selected
                ResultReceiver receiver = intent.getParcelableExtra(ScreenRecordService.BUNDLED_LISTENER);
                Bundle bundle = new Bundle();
                bundle.putInt(ERROR_KEY, SETTINGS_ERROR);
                bundle.putString(ERROR_REASON_KEY, Log.getStackTraceString(e));
                if (receiver != null) {
                    receiver.send(Activity.RESULT_OK, bundle);
                }
            }
        }
        return Service.START_STICKY;
    }

    //Pause Recording
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pauseRecording(){
        mMediaRecorder.pause();
    }

    //Resume Recording
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resumeRecording(){
        mMediaRecorder.resume();
    }

    //Set output format as int based on what developer has provided
    //It is important to provide one of the following and nothing else.
    private void setOutputFormatAsInt(String outputFormat) {
        switch (outputFormat) {
            case "DEFAULT":
                outputFormatAsInt = 0;
                break;
            case "THREE_GPP":
                outputFormatAsInt = 1;
                break;
            case "AMR_NB":
                outputFormatAsInt = 3;
                break;
            case "AMR_WB":
                outputFormatAsInt = 4;
                break;
            case "AAC_ADTS":
                outputFormatAsInt = 6;
                break;
            case "MPEG_2_TS":
                outputFormatAsInt = 8;
                break;
            case "WEBM":
                outputFormatAsInt = 9;
                break;
            case "OGG":
                outputFormatAsInt = 11;
                break;
            case "MPEG_4":
            default:
                outputFormatAsInt = 2;
        }
    }

    //Set video encoder as int based on what developer has provided
    //It is important to provide one of the following and nothing else.
    private void setvideoEncoderAsInt(String encoder) {
        switch (encoder) {
            case "DEFAULT":
                videoEncoderAsInt = 0;
                break;
            case "H263":
                videoEncoderAsInt = 1;
                break;
            case "H264":
                videoEncoderAsInt = 2;
                break;
            case "MPEG_4_SP":
                videoEncoderAsInt = 3;
                break;
            case "VP8":
                videoEncoderAsInt = 4;
                break;
            case "HEVC":
                videoEncoderAsInt = 5;
                break;
        }
    }

    //Set audio source as int based on what developer has provided
    //It is important to provide one of the following and nothing else.
    private void setAudioSourceAsInt(String audioSource) {
        switch (audioSource) {
            case "DEFAULT":
                audioSourceAsInt = 0;
                break;
            case "MIC":
                audioSourceAsInt = 1;
                break;
            case "VOICE_UPLINK":
                audioSourceAsInt = 2;
                break;
            case "VOICE_DOWNLINK":
                audioSourceAsInt = 3;
                break;
            case "VOICE_CALL":
                audioSourceAsInt = 4;
                break;
            case "CAMCODER":
                audioSourceAsInt = 5;
                break;
            case "VOICE_RECOGNITION":
                audioSourceAsInt = 6;
                break;
            case "VOICE_COMMUNICATION":
                audioSourceAsInt = 7;
                break;
            case "REMOTE_SUBMIX":
                audioSourceAsInt = 8;
                break;
            case "UNPROCESSED":
                audioSourceAsInt = 9;
                break;
            case "VOICE_PERFORMANCE":
                audioSourceAsInt = 10;
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initMediaProjection() {
        mMediaProjection = ((MediaProjectionManager) Objects.requireNonNull(getSystemService(Context.MEDIA_PROJECTION_SERVICE))).getMediaProjection(mResultCode, mResultData);
    }

    //Return the output file path as string
    public static String getFilePath() {
        return filePath;
    }

    //Return the name of the output file
    public static String getFileName() {
        return fileName;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initRecorder() throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        String curTime = formatter.format(curDate).replace(" ", "");
        String videoQuality = "HD";
        if (!isVideoHD) {
            videoQuality = "SD";
        }
        if (name == null) {
            name = videoQuality + curTime;
        }
        path = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
        filePath = path + "/" + name + ".mp4";
        fileName = name + ".mp4";

        mMediaRecorder = new MediaRecorder();

        // state = mounted or unmounted
        String state = Environment.getExternalStorageState();
        Log.d(TAG, state);

        Context ctx = this.getApplicationContext();
        File audioDir = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "AudioMemos");
        audioDir.mkdirs();
        String audioDirPath = audioDir.getAbsolutePath();

        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        mediaRecorder.setVideoEncodingBitRate(1024 * 1000);
        mMediaRecorder.setVideoEncodingBitRate(camcorderProfile.videoBitRate);
//        mediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoFrameRate(camcorderProfile.videoFrameRate);

//        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
//        mediaRecorder.setVideoSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
        mMediaRecorder.setVideoSize(1080, 1920);
//        mediaRecorder.setVideoSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
        mMediaRecorder.setOutputFile(filePath);

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG, mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();
        resetAll();
        callOnComplete();

    }

    private void callOnComplete() {
        if ( mIntent != null ) {
            ResultReceiver receiver = mIntent.getParcelableExtra(ScreenRecordService.BUNDLED_LISTENER);
            Bundle bundle = new Bundle();
            bundle.putString(ON_COMPLETE_KEY, ON_COMPLETE);
            if (receiver != null) {
                receiver.send(Activity.RESULT_OK, bundle);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void resetAll() {
        stopForeground(true);
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.reset();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
