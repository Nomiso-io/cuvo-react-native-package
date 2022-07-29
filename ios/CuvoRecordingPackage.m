//
//  CuvoRecordingPackage.m
//  RNCuvoPackage
//
//  Created by siva chandran on 29/07/22.
//  Copyright © 2022 cuvo. All rights reserved.
//

#import "CuvoRecordingPackage.h"
#import "React/RCTBridgeModule.h"
#import "CuvoScreenRecorder.h"

@interface CuvoRecordingPackage ()

@property (nonatomic, strong) RCTResponseSenderBlock callback;
@property (nonatomic, copy) NSDictionary *options;

@end


@implementation CuvoRecordingPackage {
    CuvoScreenRecorder *screenRecorder;
}
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(startRecording:(NSString *)option)  {
    dispatch_async(dispatch_get_main_queue(), ^{
         [self screenRecordingStart:option];

    });
}
-(void) screenRecordingStart:(NSString *)options  {
    
    UIWindow *keyWindow = [[UIApplication sharedApplication] keyWindow];
    screenRecorder = [[CuvoScreenRecorder alloc] initWithWindow:keyWindow];
    screenRecorder.frameInterval = 1; // 60 FPS
    screenRecorder.autosaveDuration = 1800; // 30 minutes
    screenRecorder.showsTouchPointer = NO; // hidden touch pointer
    screenRecorder.filenameBlock = ^(void) {
            return @"screencast.mov";
        }; // change filename
    [screenRecorder startRecording];
  
}
RCT_EXPORT_METHOD(stopScreenRecording:(NSString *)option callback:(RCTResponseSenderBlock)callback)  {
    dispatch_async(dispatch_get_main_queue(), ^{
         [self stopRecording:option];
    });
}

-(void) stopRecording:(NSString *)options callback:(RCTResponseSenderBlock)callback {

    self.callback = callback;
    [screenRecorder stopRecording:^(NSString *output) {
        self.callback(@[output]);
    }];
}
@end
