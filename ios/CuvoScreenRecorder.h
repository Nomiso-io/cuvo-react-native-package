//
//  CuvoScreenRecorder.h
//  RNCuvoPackage
//
//  Created by siva chandran on 28/07/22.
//  Copyright © 2022 cuvo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>
#import <TargetConditionals.h>

typedef NSString *(^SRScreenRecorderOutputFilenameBlock)(void);

@interface CuvoScreenRecorder : NSObject

@property (retain, nonatomic, readonly) UIWindow *window; // A window to be recorded.
@property (assign, nonatomic) NSInteger frameInterval;
@property (assign, nonatomic) NSUInteger autosaveDuration; // in second, default value is 600 (10 minutes).
@property (assign, nonatomic) BOOL showsTouchPointer;
@property (copy, nonatomic) SRScreenRecorderOutputFilenameBlock filenameBlock;

- (instancetype)initWithWindow:(UIWindow *)window;

- (void)startRecording;
- (void)stopRecording :(void (^)(NSString *output))completionHandler;
@end

