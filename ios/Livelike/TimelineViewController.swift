//
//  TimelineViewController.swift
//  SDKDemo
//
//  Created by Changdeo Jadhav on 09/08/21.
//

import UIKit
import EngagementSDK

class TimelineViewController: UIViewController {
  
  private let contentSession: ContentSession
  init(contentSession: ContentSession) {
    self.contentSession = contentSession
    super.init(nibName: nil, bundle: nil)
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  private var sdk: EngagementSDK!
  override func viewDidLoad() {
    super.viewDidLoad()
    let timelineVC = InteractiveWidgetTimelineViewController(contentSession: contentSession)
    
    // Do any additional setup after loading the view.
    // Add timelineVC to layout
    addChild(timelineVC)
    timelineVC.didMove(toParent: self)
    timelineVC.view.translatesAutoresizingMaskIntoConstraints = false
    view.addSubview(timelineVC.view)
    
    // Apply layout constraints
    NSLayoutConstraint.activate([
      timelineVC.view.topAnchor.constraint(equalTo: view.topAnchor),
      timelineVC.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
      timelineVC.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
      timelineVC.view.bottomAnchor.constraint(equalTo: view.bottomAnchor)
    ])
  }
  
  private func setupEngagementSDK() {
    sdk = EngagementSDK.init(config: EngagementSDKConfig(clientID: "mOBYul18quffrBDuq2IACKtVuLbUzXIPye5S3bq5"))
    EngagementSDK.logLevel = .debug
    
  }
}



/*
 // MARK: - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
 // Get the new view controller using segue.destination.
 // Pass the selected object to the new view controller.
 }
 */


