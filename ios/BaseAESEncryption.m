//
//  BaseAESEncryption.m
//  blunt
//
//  Created by Siva on 25/10/21.
//

#import "BaseAESEncryption.h"
#import "React/RCTBridgeModule.h"

@interface BaseAESEncryption ()

@property (nonatomic, strong) RCTResponseSenderBlock callback;
@property (nonatomic, copy) NSDictionary *options;

@end



@implementation BaseAESEncryption
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(takePhoto:(NSString *)option callback:(RCTResponseSenderBlock)callback)  {
    dispatch_async(dispatch_get_main_queue(), ^{
         [self takeScreenshot:option callback:callback];

    });
}
-(void) takeScreenshot:(NSString *)options callback:(RCTResponseSenderBlock)callback {

self.callback = callback;

  NSError *error = nil;
  
  UIWindow *keyWindow = [[UIApplication sharedApplication] keyWindow];
  CGRect rect = [keyWindow bounds];
  UIGraphicsBeginImageContextWithOptions(rect.size,YES,0.0f);
  CGContextRef context = UIGraphicsGetCurrentContext();
  [keyWindow.layer renderInContext:context];
  UIImage *capturedScreen = UIGraphicsGetImageFromCurrentImageContext();
  UIGraphicsEndImageContext();
  
  NSString *screenshotUrl = [NSHomeDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"Documents/capturedImage.jpg"]];
  [UIImageJPEGRepresentation(capturedScreen, 1.0) writeToFile:screenshotUrl atomically:YES];
    NSMutableDictionary *response = [[NSMutableDictionary alloc] init];
    response[@"screenshotUrl"] = screenshotUrl;
    self.callback(@[screenshotUrl]);
}

@end
