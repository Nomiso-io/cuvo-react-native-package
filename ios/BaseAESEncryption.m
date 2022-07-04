//
//  BaseAESEncryption.m
//  blunt
//
//  Created by Siva on 25/10/21.
//

#import "BaseAESEncryption.h"
#import "React/RCTBridgeModule.h"

@implementation BaseAESEncryption
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(getEncryptionString:(NSString *) input
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)  {
    NSLog(@"screenshotUrl ------------------");
//    NSError *error = nil;
  
//   UIWindow *keyWindow = [[UIApplication sharedApplication] keyWindow];
//   CGRect rect = [keyWindow bounds];
//   UIGraphicsBeginImageContextWithOptions(rect.size,YES,0.0f);
//   CGContextRef context = UIGraphicsGetCurrentContext();
//   [keyWindow.layer renderInContext:context];
//   UIImage *capturedScreen = UIGraphicsGetImageFromCurrentImageContext();
//   UIGraphicsEndImageContext();
  
//   NSString *screenshotUrl = [NSHomeDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"Documents/capturedImage.jpg"]];
//   [UIImageJPEGRepresentation(capturedScreen, 1.0) writeToFile:screenshotUrl atomically:YES];
//   NSLog(@"screenshotUrl ------------------ %@",screenshotUrl);
  NSError *error = nil;
  NSString *screenshotUrl = @"WQQWQWQWQWQWQWQEQEQEQEQEQE";
//   if (screenshotUrl == nil) {
//       reject(@"encrypt_fail", @"Encrypt error", error);
//   } else {
      resolve(screenshotUrl);
//   }
}
  RCT_EXPORT_METHOD(getDecryptString:(NSString *) input
                    resolver:(RCTPromiseResolveBlock)resolve
                    rejecter:(RCTPromiseRejectBlock)reject)  {

    NSError *error = nil;
//    NSLog(@"--- %@",dictionaryResponseDecrypted);
    NSString *sDecode = @"getEncryptionString";

    if (sDecode == nil) {
        reject(@"encrypt_fail", @"Encrypt error", error);
    } else {
        resolve(sDecode);
    }
    
}
@end
