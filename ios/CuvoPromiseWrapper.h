//
//  CuvoPromiseWrapper.h
//  RNCuvoPackage
//
//  Created by siva chandran on 21/07/22.
//  Copyright © 2022 cuvo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

static NSString * _Nonnull const ASYNC_OP_IN_PROGRESS = @"ASYNC_OP_IN_PROGRESS";


NS_ASSUME_NONNULL_BEGIN

@interface CuvoPromiseWrapper : NSObject

-(void)setPromiseWithInProgressCheck:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject fromCallSite: (NSString*) callsite;
-(void)resolve: (id) result;
-(void)reject:(NSString *)message withError:(NSError *)error;
-(void)reject:(NSString *)message withCode:(NSString*) code withError:(NSError *)error;

@property (readonly, assign) NSString *nameOfCallInProgress;

@end

NS_ASSUME_NONNULL_END
