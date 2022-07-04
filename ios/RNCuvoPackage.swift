//
//  RNCuvoPackage.swift
//  RNCuvoPackage
//
//  Copyright © 2020 cuvo. All rights reserved.
//

import Foundation

@objc(RNCuvoPackage)
class RNCuvoPackage: NSObject {
  @objc
  func constantsToExport() -> [AnyHashable : Any]! {
    return ["count": 1]
  }

  @objc
  static func requiresMainQueueSetup() -> Bool {
    return true
  }
}
