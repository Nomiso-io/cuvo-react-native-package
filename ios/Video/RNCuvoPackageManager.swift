import AVFoundation
import React

@objc(RNCuvoPackageManager)
class RNCuvoPackageManager: RCTViewManager {
    
    override func view() -> UIView {
        return RNCuvoPackage(eventDispatcher: bridge.eventDispatcher() as! RCTEventDispatcher)
    }
    
    func methodQueue() -> DispatchQueue {
        return bridge.uiManager.methodQueue
    }
    
    @objc(save:reactTag:resolver:rejecter:)
    func save(options: NSDictionary, reactTag: NSNumber, resolve: @escaping RCTPromiseResolveBlock,reject: @escaping RCTPromiseRejectBlock) -> Void {
        bridge.uiManager.prependUIBlock({_ , viewRegistry in
            let view = viewRegistry?[reactTag]
            if !(view is RNCuvoPackage) {
                RCTLogError("Invalid view returned from registry, expecting RNCuvoPackage, got: %@", String(describing: view))
            } else if let view = view as? RNCuvoPackage {
                view.save(options: options, resolve: resolve, reject: reject)
            }
        })
    }
    
    @objc(setLicenseResult:reactTag:)
    func setLicenseResult(license: NSString, reactTag: NSNumber) -> Void {
        bridge.uiManager.prependUIBlock({_ , viewRegistry in
            let view = viewRegistry?[reactTag]
            if !(view is RNCuvoPackage) {
                RCTLogError("Invalid view returned from registry, expecting RNCuvoPackage, got: %@", String(describing: view))
            } else if let view = view as? RNCuvoPackage {
                view.setLicenseResult(license as String)
            }
        })
    }
    
    @objc(setLicenseResultError:reactTag:)
    func setLicenseResultError(error: NSString, reactTag: NSNumber) -> Void {
        bridge.uiManager.prependUIBlock({_ , viewRegistry in
            let view = viewRegistry?[reactTag]
            if !(view is RNCuvoPackage) {
                RCTLogError("Invalid view returned from registry, expecting RNCuvoPackage, got: %@", String(describing: view))
            } else if let view = view as? RNCuvoPackage {
                view.setLicenseResultError(error as String)
            }
        })
    }
    
    override func constantsToExport() -> [AnyHashable : Any]? {
        return [
            "ScaleNone": AVLayerVideoGravity.resizeAspect,
            "ScaleToFill": AVLayerVideoGravity.resize,
            "ScaleAspectFit": AVLayerVideoGravity.resizeAspect,
            "ScaleAspectFill": AVLayerVideoGravity.resizeAspectFill
        ]
    }

    override class func requiresMainQueueSetup() -> Bool {
        return true
    }
}
