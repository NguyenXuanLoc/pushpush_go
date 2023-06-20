//
//  PushPushGoPlugin.swift
//  pushpush_go
//
//  Created by Tà on 19/06/2023.
//

import Foundation
import Flutter
import UserNotifications

public class PushPushGoPlugin: NSObject {
    
    @objc public static let shared = PushPushGoPlugin()
    
    private let appID: String = ""
    private let apiToken: String = ""
    
    private var application: UIApplication?
        
    private var registerResult: FlutterResult?
    private var receiveHandler: PushPushGoStreamHandler?
    private var acceptHandler: PushPushGoStreamHandler?
    private var openHandler: DeepLinkStreamHandler?
    private var cachedDeepLink: String?
    
    private override init() {}

    public func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        self.application = application
        
        // Initialize PPG framework
        PPG.initializeNotifications(projectId: appID, apiToken: apiToken)
        
        return true
    }
    
    public func applicationDidBecomeActive(_ application: UIApplication) {
      // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.

      PPG.sendEventsDataToApi()
    }
    
    public func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        PPG.sendDeviceToken(deviceToken) { _ in }
    }
    
    public func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any]) -> Bool {
        let urlString = url.absoluteString
        if let openHandler = openHandler {
            openHandler.sendDeepLink(deepLink: urlString)
        } else {
            cachedDeepLink = urlString
        }
        return true
    }
}

extension PushPushGoPlugin: FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        var channel: FlutterMethodChannel = FlutterMethodChannel(name: "pushwoosh", binaryMessenger: registrar.messenger())
        registrar.addMethodCallDelegate(shared, channel: channel)
        
        var receiveEventChannel: FlutterEventChannel = FlutterEventChannel(name: "pushwoosh/receive", binaryMessenger: registrar.messenger())
        shared.receiveHandler = PushPushGoStreamHandler()
        receiveEventChannel.setStreamHandler(shared.receiveHandler)

        var acceptEventChannel: FlutterEventChannel = FlutterEventChannel(name: "pushwoosh/accept", binaryMessenger: registrar.messenger())
        shared.acceptHandler = PushPushGoStreamHandler()
        acceptEventChannel.setStreamHandler(shared.acceptHandler)
        
        var openEventChannel: FlutterEventChannel = FlutterEventChannel(name: "pushwoosh/deeplink", binaryMessenger: registrar.messenger())
        shared.openHandler = DeepLinkStreamHandler()
        openEventChannel.setStreamHandler(shared.openHandler)
        
        if let cachedDeepLink = shared.cachedDeepLink {
            shared.openHandler?.sendDeepLink(deepLink: cachedDeepLink)
            shared.cachedDeepLink = nil
        }
        
        registrar.addApplicationDelegate(shared)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "initialize":
            break
        case "registerForPushNotifications":
            registerResult = result
            guard let application = application else {
                result(nil)
                return
            }
            // Register for push notifications if you do not already
            PPG.registerForNotifications(application: application, handler: { result in
                switch result {
                case .error(let error):
                    // handle error
                    print(error)
                    return
                case .success:
                    return
                }
            })
            UNUserNotificationCenter.current().delegate = self
        case "unregisterForPushNotifications":
            PPG.unsubscribeUser { res in
                
            }
        case "showForegroundAlert":
            if let value = call.arguments as? NSNumber {
                
            } else {
                
            }
        case "getHWID":
            break
        case "getPushToken":
            break
        case "setUserId":
            break
        case "setLanguage":
            break
        case "setTags":
            break
        case "getTags":
            break
        case "postEvent":
            break
        case "addToApplicationIconBadgeNumber":
            if let arguments = call.arguments as? [String: Any] {
                var badge = arguments["badges"] as? Int ?? 0
                UIApplication.shared.applicationIconBadgeNumber += badge
            }
        case "setApplicationIconBadgeNumber":
            if let arguments = call.arguments as? [String: Any] {
                var badge = arguments["badges"] as? Int ?? 0
                UIApplication.shared.applicationIconBadgeNumber = badge
            }
        case "getApplicationIconBadgeNumber":
            let badge = UIApplication.shared.applicationIconBadgeNumber
            result(NSNumber(integerLiteral: badge))
        default:
            result(FlutterMethodNotImplemented)
        }
    }
}

extension PushPushGoPlugin: UNUserNotificationCenterDelegate {
    public func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification,
              withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        
        // Display notification when app is in foreground, optional
        completionHandler([.alert, .badge, .sound])
    }
    
    public func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {

        // Send information about clicked notification to framework
        PPG.notificationClicked(response: response)

        // Open external link from push notification
        // Remove this section if this behavior is not expected
        guard let url = PPG.getUrlFromNotificationResponse(response: response)
            else {
                completionHandler()
                return
            }
        UIApplication.shared.open(url)
        //
        completionHandler()
    }
    
    /*
    #pragma mark - PushNotificationDelegate
    - (void)onPushReceived:(PushNotificationManager *)pushManager withNotification:(NSDictionary *)pushNotification onStart:(BOOL)onStart {
        [_receiveHandler sendPushNotification:pushNotification onStart:onStart];
    }

    - (void)onPushAccepted:(PushNotificationManager *)pushManager withNotification:(NSDictionary *)pushNotification onStart:(BOOL)onStart {
        [_acceptHandler sendPushNotification:pushNotification onStart:onStart];
    }
    */
}

class PushPushGoStreamHandler: NSObject, FlutterStreamHandler {
    public var eventSink: FlutterEventSink?
    public var startPushNotification: [String: Any]?
    
    public func sendPushNotification(pushNotification: [String: Any]?, onStart: Bool) {
        guard let eventSink = eventSink, let pushNotification = pushNotification else {
            //flutter app is not initialized yet, so save push notification, we send it to listener later
            startPushNotification = pushNotification;
            return
        }
        let pushDict = pushNotification["aps"] as? [String: Any]
        var title: String?
        var message: String?
        var alertMsg = pushDict?["alert"]
        
        if let alertMsg = alertMsg as? [String: Any] {
            title = alertMsg["title"] as? String
            message = alertMsg["body"] as? String
        } else if let alertMsg = alertMsg as? String {
            message = alertMsg
        }
        
        var customData: [String: Any]? = pushNotification // Need check this in pushwoosh
        
        var messageDict: [String: Any] = [:]
        messageDict["title"] = title
        messageDict["message"] = message
        messageDict["customData"] = customData

        messageDict["fromBackground"] = onStart ? 1 : 0

        messageDict["payload"] = pushNotification

        eventSink(messageDict)
    }
    
    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        self.eventSink = events
        if let startPushNotification = startPushNotification {
            self.sendPushNotification(pushNotification: startPushNotification, onStart: true)
            self.startPushNotification = nil
        }
        return nil
    }
    
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        self.eventSink = nil
        return nil
    }
}

class DeepLinkStreamHandler: NSObject, FlutterStreamHandler {
    public var eventSink: FlutterEventSink?
    public var cachedDeepLink: String?
    
    public func sendDeepLink(deepLink: String?) {
        if let eventSink = eventSink {
            eventSink(deepLink)
        } else {
            //flutter app is not initialized yet, caching deep link to send it later
            cachedDeepLink = deepLink
        }
    }
    
    func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        self.eventSink = events
        if let cachedDeepLink = cachedDeepLink {
            self.sendDeepLink(deepLink: cachedDeepLink)
            self.cachedDeepLink = nil
        }
        return nil
    }
    
    func onCancel(withArguments arguments: Any?) -> FlutterError? {
        eventSink = nil
        return nil
    }
}

extension NSError {
    var flutterError: FlutterError {
        let code = "Error \(self.code)"
        return FlutterError(code: code, message: domain, details: localizedDescription)
    }
}
