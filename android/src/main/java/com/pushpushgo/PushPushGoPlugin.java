package com.pushpushgo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pushpushgo.sdk.PushPushGo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

public class PushPushGoPlugin implements MethodCallHandler, PluginRegistry.NewIntentListener, FlutterPlugin, ActivityAware {
    public static boolean listen;
    public static boolean showForegroundPush = true;
    public static MethodChannel channel;
    public static EventChannel receiveChannel;
    public static EventChannel acceptChannel;
    public static EventChannel openChannel;
    public static StreamHandler receiveHandler = new StreamHandler();
    public static StreamHandler acceptHandler = new StreamHandler();
    public static DeepLinkStreamHandler openHandler = new DeepLinkStreamHandler();
    public static BinaryMessenger messenger;
    public static ActivityPluginBinding activityPluginBinding;
    public static PushPushGoPlugin pluginInstance;

    public static String cachedDeepLink;
    private PushPushGo ppg = PushPushGo.getInstance();

    private void onAttachedToEngine(BinaryMessenger messenger) {

        PushPushGoPlugin.messenger = messenger;
        PushPushGoPlugin.channel = new MethodChannel(messenger, "pushwoosh");
        PushPushGoPlugin.receiveChannel = new EventChannel(messenger, "pushwoosh/receive");
        PushPushGoPlugin.acceptChannel = new EventChannel(messenger, "pushwoosh/accept");
        PushPushGoPlugin.openChannel = new EventChannel(messenger, "pushwoosh/deeplink");
        PushPushGoPlugin.channel.setMethodCallHandler(new PushPushGoPlugin());

        PushPushGoPlugin.receiveChannel.setStreamHandler(receiveHandler);
        PushPushGoPlugin.acceptChannel.setStreamHandler(acceptHandler);
        PushPushGoPlugin.openChannel.setStreamHandler(openHandler);
    }

    private static void handleCachedLinkIntent(PushPushGoPlugin instance, Activity activity) {
        if (activity != null && activity.getIntent() != null) {
            instance.handleIntent(activity.getIntent());
        }
        if (cachedDeepLink != null) {
            forwardDeepLinkToFlutter(cachedDeepLink);
            cachedDeepLink = null;
        }
    }


    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        pluginInstance = new PushPushGoPlugin();
        pluginInstance.onAttachedToEngine(binding.getBinaryMessenger());
        if (activityPluginBinding != null) {
            PushPushGoPlugin.handleCachedLinkIntent(pluginInstance, activityPluginBinding.getActivity());
        }
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        PushPushGoPlugin.channel.setMethodCallHandler(null);
        PushPushGoPlugin.receiveChannel.setStreamHandler(null);
        PushPushGoPlugin.openChannel.setStreamHandler(null);
        PushPushGoPlugin.acceptChannel.setStreamHandler(null);

        PushPushGoPlugin.receiveHandler.onCancel(null);
        PushPushGoPlugin.acceptHandler.onCancel(null);
        PushPushGoPlugin.openHandler.onCancel(null);
        PushPushGoPlugin.channel = null;
        PushPushGoPlugin.receiveChannel = null;
        PushPushGoPlugin.acceptChannel = null;
        PushPushGoPlugin.openChannel = null;
        PushPushGoPlugin.pluginInstance = null;
        PushPushGoPlugin.messenger = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activityPluginBinding = binding;
        activityPluginBinding.addOnNewIntentListener(pluginInstance);
        handleIntent(binding.getActivity().getIntent());
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        activityPluginBinding.removeOnNewIntentListener(pluginInstance);
        activityPluginBinding=null;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
       /* Pushwoosh pushwooshInstance = Pushwoosh.getInstance();*/
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case "initialize":
                initialize(call/*, pushwooshInstance*/);
                break;
            case "enableHuaweiNotifications":
                enableHuaweiNotifications();
                break;
            case "getInstance":
                break;
            case "registerForPushNotifications":
                registerForPushNotifications(call, result);
                break;
            case "unregisterForPushNotifications":
                unregisterForPushNotifications(call, result);
                break;
            case "getPushToken":
                getPushToken(result);
                break;
            case "getHWID":
                break;
            case "setTags":
                setTags(call, result);
                break;
            case "getTags":
                getTags(call, result);
                break;
            case "startListening":
                PushPushGoPlugin.listen = true;
                break;
            case "setShowForegroundPush":
                setShowForegroundPush(call);
                break;
            case "showForegroundAlert":
                showForegroundAlert(call, result);
                break;
            case "postEvent":
                postEvent(call, result);
                break;
            case "setMultiNotificationMode":
                setMultiNotificationMode(call);
                break;
            case "setUserId":
                setUserId(call, result);
                break;
            case "setLanguage":
                setLanguage(call, result);
                break;
            case "addToApplicationIconBadgeNumber":
                addToApplicationIconBadgeNumber(call, result);
                break;
            case "setApplicationIconBadgeNumber":
                setApplicationIconBadgeNumber(call, result);
                break;
            case "getApplicationIconBadgeNumber":
             /*   result.success(PushwooshBadge.getBadgeNumber());*/
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public boolean onNewIntent(Intent intent) {
        handleIntent(intent);
        return false;
    }
void getPushToken(Result result){
/*    Futures.addCallback(ppg.getPushToken(), new FutureCallback<String>() {
        @Override
        public void onSuccess(String value) {
            result.success(value);
        }

        @Override
        public void onFailure(Throwable t) {

        }
    }, ContextCompat.getMainExecutor(context));*/
}
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String url = intent.getDataString();

        if (Intent.ACTION_VIEW.equals(action)) {
            forwardDeepLinkToFlutter(url);
        }
    }

    private static void forwardDeepLinkToFlutter(final String deepLink) {
        if (TextUtils.isEmpty(deepLink)) {
            return;
        }
        final DeepLinkStreamHandler openHandler = PushPushGoPlugin.openHandler;
        if (openHandler != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    openHandler.sendDeepLink(deepLink);
                }
            });
        } else {
            cachedDeepLink = deepLink;
        }
    }

    public static void onMessageReceived(final Map<String, Object> map, final boolean fromBackground) {
        final StreamHandler receiveHandler = PushPushGoPlugin.receiveHandler;
        if (receiveHandler != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    receiveHandler.sendEvent(map, fromBackground);
                }
            });
        }
    }

    public static void onMessageAccepted(final Map<String, Object> map, final boolean fromBackground) {
        final StreamHandler acceptHandler = PushPushGoPlugin.acceptHandler;
        if (acceptHandler != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    acceptHandler.sendEvent(map, fromBackground);
                }
            });
        }
    }

    public static void callToFlutter(String methode, Map<String, Object> arg) {
        MethodChannel channel = PushPushGoPlugin.channel;
        boolean listen = PushPushGoPlugin.listen;
        if (channel != null && listen) {
            channel.invokeMethod(methode, arg);
        }
    }

    private void registerForPushNotifications(MethodCall call, final Result result) {
        ppg.registerSubscriber();
    }

    private void sendResultException(Result result, Exception e) {
        if (e == null) {
            return;
        }
        String message = e.getMessage();
        result.error(e.getClass().getSimpleName(), message, Log.getStackTraceString(e));
    }

    private void unregisterForPushNotifications(MethodCall call, final Result result) {
        ppg.unregisterSubscriber();
    }

    private void getTags(MethodCall call, final Result result) {
/*        Pushwoosh.getInstance().getTags(new Callback<TagsBundle, GetTagsException>() {
            @Override
            public void process(com.pushwoosh.function.Result<TagsBundle, GetTagsException> resultRequest) {
                if (resultRequest.isSuccess()) {
                    TagsBundle data = resultRequest.getData();
                    Map<String, Object> map = data != null ? data.getMap() : null;
                    Map<String, Object> mapParsed = new HashMap<>();
                    if (map != null) {
                        Iterator it = map.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            if (pair.getValue() instanceof JSONArray) {
                                Object value = data.getList((String) pair.getKey());
                                if (value != null) {
                                    mapParsed.put((String) pair.getKey(), value);
                                }
                            } else {
                                mapParsed.put((String) pair.getKey(), pair.getValue());
                            }
                        }
                        result.success(mapParsed);
                    }
                } else {
                    sendResultException(result, resultRequest.getException());
                }
            }
        });*/
    }

    private void setTags(MethodCall call, final Result result) {
        Map<String, String> map = call.argument("tags");
        if (map == null) {
            return;
        }
        JSONObject json = new JSONObject(map);
/*        Pushwoosh.getInstance().setTags(Tags.fromJson(json), new Callback<Void, PushwooshException>() {
            @Override
            public void process(com.pushwoosh.function.Result<Void, PushwooshException> resultRequest) {
                if (resultRequest.isSuccess()) {
                    result.success(null);
                } else {
                    sendResultException(result, resultRequest.getException());
                }
            }
        });*/
    }

    private void showForegroundAlert(MethodCall call, Result result) {
        if (call.arguments == null) {
            result.success(showForegroundPush);
        } else {
            showForegroundPush = (Boolean) call.arguments;
        }
    }

    @SuppressWarnings("unchecked")
    private void postEvent(MethodCall call, Result result) {
        List<Object> args = call.arguments();
        String method = (String) args.get(0);
        Map<String, Object> map = (Map<String, Object>) args.get(1);
        JSONObject jsonObject = new JSONObject(map);
       /* InAppManager.getInstance().postEvent(method, Tags.fromJson(jsonObject));*/
        result.success(null);
    }

    private void setMultiNotificationMode(MethodCall call) {
        Object on = call.argument("on");
        if (on instanceof Boolean) {
            /*PushwooshNotificationSettings.setMultiNotificationMode((Boolean) on);*/
        }
    }

    private void setUserId(MethodCall call, Result result) {
        try {
            /*Pushwoosh.getInstance().setUserId((String) call.argument("userId"));*/
            result.success(null);
        } catch (Exception e) {
            sendResultException(result, e);
        }
    }

    private void setLanguage(MethodCall call, Result result) {
        try {
            /*Pushwoosh.getInstance().setLanguage((String) call.argument("language"));*/
            result.success(null);
        } catch (Exception e) {
            sendResultException(result, e);
        }
    }

    private void setApplicationIconBadgeNumber(MethodCall call, Result result) {
        try {
            /*PushwooshBadge.setBadgeNumber((int) call.argument("badges"));*/
            result.success(null);
        } catch (Exception e) {
            sendResultException(result, e);
        }
    }

    private void addToApplicationIconBadgeNumber(MethodCall call, Result result) {
        try {
            /*PushwooshBadge.setBadgeNumber((int) call.argument("badges"));*/
            result.success(null);
        } catch (Exception e) {
            sendResultException(result, e);
        }
    }

    private void initialize(MethodCall call/*, Pushwoosh pushwooshInstance*/) {
/*        String appId = call.argument("app_id");
        if (appId != null) {
            pushwooshInstance.setAppId(appId);
        }
        String sendId = call.argument("sender_id");
        if (sendId != null) {
            pushwooshInstance.setSenderId(sendId);
        }*/
    }

    private void enableHuaweiNotifications() {//ToDo
        /*Pushwoosh.getInstance().enableHuaweiPushNotifications();*/
    }

    private void setShowForegroundPush(MethodCall call) {
        if (call.argument("show_foreground_push") instanceof Boolean) {
            PushPushGoPlugin.showForegroundPush =
                    call.argument("show_foreground_push");
        } else {
            PushPushGoPlugin.showForegroundPush = false;
        }
    }

    private static class StreamHandler implements EventChannel.StreamHandler {
        private EventChannel.EventSink events;
        private Map<String, Object> startPushNotification;

        private void sendEvent(Map<String, Object> map, boolean fromBackground) {
            if (events != null) {
                events.success(convertMap(map, fromBackground));
            } else {
                //flutter app is not initialized yet, so save push notification, we send it to listener later
                startPushNotification = map;
            }
        }

        private Map<String, Object> convertMap(Map<String, Object> map, boolean fromBackground) {
            HashMap<String, Object> mapForFlutter = new HashMap<>();
            Object title = map.get("title");
            mapForFlutter.put("title", title == null ? "" : title);
            Object body = map.get("body");
            mapForFlutter.put("message", body == null ? "" : body);
            Object customData = map.get("userdata");
            mapForFlutter.put("customData", customData == null ? new HashMap<String, Object>() : customData);
            mapForFlutter.put("fromBackground", fromBackground);
            mapForFlutter.put("payload", map);
            return mapForFlutter;
        }

        @Override
        public void onListen(Object arguments, EventChannel.EventSink events) {
            this.events = events;

            if (startPushNotification != null) {
                sendEvent(startPushNotification, true);
                startPushNotification = null;
            }
        }

        @Override
        public void onCancel(Object arguments) {
            this.events = null;
        }
    }

    private static class DeepLinkStreamHandler implements EventChannel.StreamHandler {
        private EventChannel.EventSink events;
        private String cachedDeepLink;

        @Override
        public void onListen(Object arguments, EventChannel.EventSink events) {
            this.events = events;
            if (cachedDeepLink != null) {
                events.success(cachedDeepLink);
                cachedDeepLink = null;
            }
        }

        @Override
        public void onCancel(Object arguments) {
            this.events = null;
        }

        private void sendDeepLink(String deepLink) {
            if (events != null) {
                events.success(deepLink);
            } else {
                //flutter app is not initialized yet, caching deep link to send it later
                cachedDeepLink = deepLink;
            }
        }
    }
}
