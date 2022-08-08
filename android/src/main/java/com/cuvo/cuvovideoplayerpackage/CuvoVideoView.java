package com.cuvo.cuvovideoplayerpackage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.TimedMetaData;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.MediaController;

import com.cuvo.cuvovideoplayerpackage.APKExpansionSupport;
import com.cuvo.cuvovideoplayerpackage.ZipResourceFile;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScalableVideoView;
import com.yqritc.scalablevideoview.ScaleManager;
import com.yqritc.scalablevideoview.Size;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import java.math.BigDecimal;

import javax.annotation.Nullable;

@SuppressLint("ViewConstructor")
public class CuvoVideoView  extends ScalableVideoView {

    public enum Events {
        EVENT_LOAD_START("onVideoLoadStart"),
        EVENT_LOAD("onVideoLoad"),
        EVENT_ERROR("onVideoError"),
        EVENT_PROGRESS("onVideoProgress"),
        EVENT_TIMED_METADATA("onTimedMetadata"),
        EVENT_SEEK("onVideoSeek"),
        EVENT_END("onVideoEnd"),
        EVENT_STALLED("onPlaybackStalled"),
        EVENT_RESUME("onPlaybackResume"),
        EVENT_READY_FOR_DISPLAY("onReadyForDisplay"),
        EVENT_FULLSCREEN_WILL_PRESENT("onVideoFullscreenPlayerWillPresent"),
        EVENT_FULLSCREEN_DID_PRESENT("onVideoFullscreenPlayerDidPresent"),
        EVENT_FULLSCREEN_WILL_DISMISS("onVideoFullscreenPlayerWillDismiss"),
        EVENT_FULLSCREEN_DID_DISMISS("onVideoFullscreenPlayerDidDismiss");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public CuvoVideoView(ThemedReactContext themedReactContext) {
        super(themedReactContext);

//        mThemedReactContext = themedReactContext;
//        mEventEmitter = themedReactContext.getJSModule(RCTEventEmitter.class);
//        themedReactContext.addLifecycleEventListener(this);
//
//        initializeMediaPlayerIfNeeded();
        setSurfaceTextureListener(this);

        // mProgressUpdateRunnable = new Runnable() {
        //     @Override
        //     public void run() {

        //         if (mMediaPlayerValid && !isCompleted && !mPaused && !mBackgroundPaused) {
        //             WritableMap event = Arguments.createMap();
        //             event.putDouble(EVENT_PROP_CURRENT_TIME, mMediaPlayer.getCurrentPosition() / 1000.0);
        //             event.putDouble(EVENT_PROP_PLAYABLE_DURATION, mVideoBufferedDuration / 1000.0); //TODO:mBufferUpdateRunnable
        //             event.putDouble(EVENT_PROP_SEEKABLE_DURATION, mVideoDuration / 1000.0);
        //             mEventEmitter.receiveEvent(getId(), Events.EVENT_PROGRESS.toString(), event);

        //             // Check for update after an interval
        //             mProgressUpdateHandler.postDelayed(mProgressUpdateRunnable, Math.round(mProgressUpdateInterval));
        //         }
        //     }
        // };
    }

}
