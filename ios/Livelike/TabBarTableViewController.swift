//
//  TabBarTableViewController.swift
//  SDKDemo
//
//  Created by Changdeo Jadhav on 05/08/21.
//

import UIKit
import EngagementSDK

class TabBarTableViewController: UITabBarController {
  
  private let contentSession: ContentSession
  
  private lazy var chatViewControllerContainer: ChatViewControllerContainer = {
    let vc = ChatViewControllerContainer(contentSession: self.contentSession)
    return vc
  }()
  
  private lazy var timelineViewController: TimelineViewController = {
    let vc = TimelineViewController(contentSession: self.contentSession)
    return vc
  }()
  
  init(contentSession: ContentSession) {
    self.contentSession = contentSession
    super.init(nibName: nil, bundle: nil)
  }
  
  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    // Uncomment the following line to preserve selection between presentations
    // self.clearsSelectionOnViewWillAppear = false
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem
    self.navigationController?.isNavigationBarHidden = true
    
    // UITabBar.appearance().barTintColor = .systemBackground
    if #available(iOS 13.0, *) {
      tabBar.tintColor = .label
      view.backgroundColor = .systemBackground
    } else {
      // Fallback on earlier versions
    }
    setupVCs()
  }
  
  
  
  fileprivate func createNavController(for rootViewController: UIViewController,
                                       title: String) -> UIViewController {
    let navController = UINavigationController(rootViewController: rootViewController)
    navController.tabBarItem.title = title
    navController.isNavigationBarHidden = true
    return navController
  }
  
  
  
  func setupVCs() {
    viewControllers = [
      createNavController(for: chatViewControllerContainer, title: NSLocalizedString("Chat", comment: "") ),
      createNavController(for: timelineViewController, title: NSLocalizedString("Timeline", comment: "")),
      
    ]
  }
}
