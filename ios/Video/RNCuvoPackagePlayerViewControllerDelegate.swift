import Foundation
import AVKit

protocol RNCuvoPackagePlayerViewControllerDelegate : NSObject {
    func videoPlayerViewControllerWillDismiss(playerViewController:AVPlayerViewController)
    func videoPlayerViewControllerDidDismiss(playerViewController:AVPlayerViewController)
}
