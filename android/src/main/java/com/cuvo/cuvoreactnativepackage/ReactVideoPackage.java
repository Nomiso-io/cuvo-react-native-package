package com.cuvo.cuvoreactnativepackage;

import androidx.annotation.NonNull;

import com.cuvo.cuvovideoplayerpackage.DefaultReactExoplayerConfig;
import com.cuvo.cuvovideoplayerpackage.ReactExoplayerConfig;
import com.cuvo.cuvovideoplayerpackage.ReactExoplayerViewManager;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReactVideoPackage implements ReactPackage {

    private ReactExoplayerConfig config;

    public ReactVideoPackage() {
    }

    public ReactVideoPackage(ReactExoplayerConfig config) {
        this.config = config;
    }

//     @Override
//     public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
//         return Collections.emptyList();
//     }

 @Override
  public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();
    modules.add(new ScreenRecordingModule(reactContext));
    modules.add(new CuvoAudioRecorderPlayerModule(reactContext));
    modules.add(new CUVOTakeScreenShot(reactContext));
    // modules.add(new ReactExoplayerViewManager(config));
    return modules;
  }

  // Deprecated RN 0.47
  public List<Class<? extends JavaScriptModule>> createJSModules() {
     return Collections.emptyList();
  }


    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
       if (config == null) {
           config = new DefaultReactExoplayerConfig(reactContext);
       }
       return Collections.singletonList(new ReactExoplayerViewManager(config));
    }
}
