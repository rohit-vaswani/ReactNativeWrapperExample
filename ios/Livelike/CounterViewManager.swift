//
//  CounterViewManager.swift
//  ReactLivelike
//
//  Created by Changdeo Jadhav on 16/01/22.
//

import Foundation
@objc(LiveLikeiOSViewManager)
class LiveLikeiOSViewManager: RCTViewManager {
  override func view() -> UIView! {
    return LiveLikeView()
  }
  override static func requiresMainQueueSetup() -> Bool {
     return true
   }
 
 
}
