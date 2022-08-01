//
//  CuvoAudioRecorderPlayer.h
//  RNCuvoPackage
//
//  Created by siva chandran on 01/08/22.
//  Copyright © 2022 cuvo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>


@interface CuvoAudioRecorderPlayer : RCTEventEmitter <RCTBridgeModule, AVAudioPlayerDelegate>
- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player
        successfully:(BOOL)flag;
- (void)updateRecorderProgress:(NSTimer*) timer;
- (void)updateProgress:(NSTimer*) timer;
- (void)startRecorderTimer;
- (void)startPlayerTimer;
@end
