package com.cuvo.cuvovideoplayerpackage;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.yqritc.scalablevideoview.ScalableType;

import javax.annotation.Nullable;
import java.util.Map;

public class CuvoVideoViewManager extends SimpleViewManager<CuvoVideoView> {

    public static final String REACT_CLASS = "CuvoVideo";

@Override
    public String getName() {
        return REACT_CLASS;
    }

     @Override
     protected CuvoVideoView createViewInstance(ThemedReactContext themedReactContext) {
         return new CuvoVideoView(themedReactContext);
     }
}
