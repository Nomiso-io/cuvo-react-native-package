package com.cuvo.cuvovideoplayerpackage;

import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;

public interface ReactExoplayerConfig {
    LoadErrorHandlingPolicy buildLoadErrorHandlingPolicy(int minLoadRetryCount);

    void setDisableDisconnectError(boolean disableDisconnectError);
    boolean getDisableDisconnectError();

    DefaultBandwidthMeter getBandwidthMeter();
}

