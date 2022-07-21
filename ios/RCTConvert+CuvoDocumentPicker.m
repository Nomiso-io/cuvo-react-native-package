#import "RCTConvert+CuvoDocumentPicker.h"

@implementation RCTConvert (CuvoDocumentPicker)
RCT_ENUM_CONVERTER(
    UIModalPresentationStyle,
    (@{
      @"fullScreen" : @(UIModalPresentationFullScreen),
      @"pageSheet" : @(UIModalPresentationPageSheet),
      @"formSheet" : @(UIModalPresentationFormSheet),
      @"overFullScreen" : @(UIModalPresentationOverFullScreen),
    }),
    UIModalPresentationFullScreen,
    integerValue)


RCT_ENUM_CONVERTER(
    UIModalTransitionStyle,
    (@{
      @"coverVertical" : @(UIModalTransitionStyleCoverVertical),
      @"flipHorizontal" : @(UIModalTransitionStyleFlipHorizontal),
      @"crossDissolve" : @(UIModalTransitionStyleCrossDissolve),
      @"partialCurl" : @(UIModalTransitionStylePartialCurl),
    }),
    UIModalTransitionStyleCoverVertical,
    integerValue)

@end
