package com.pushpushgo;

/*import com.pushwoosh.internal.utils.JsonUtils;
import com.pushwoosh.internal.utils.PWLog;
import com.pushwoosh.notification.NotificationServiceExtension;
import com.pushwoosh.notification.PushMessage;*/

public class PushPushGoNotificationServiceExtension {

}/* extends NotificationServiceExtension {
    private String TAG = "PushwooshNotificationServiceExtension";
    private boolean showForegroundPush = false;

    public PushwooshNotificationServiceExtension() {
        try {
            String packageName = getApplicationContext().getPackageName();
            ApplicationInfo ai =  getApplicationContext().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            if (ai != null && ai.metaData != null) {
                showForegroundPush = ai.metaData.getBoolean("PW_BROADCAST_PUSH", false) || ai.metaData.getBoolean("com.pushwoosh.foreground_push", false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to read AndroidManifest metaData", e);
        }

        Log.e(TAG, "showForegroundPush = " + showForegroundPush);
    }

    @Override
    protected boolean onMessageReceived(PushMessage pushMessage) {
        boolean appOnForeground = isAppOnForeground();
        try {
            PushwooshPlugin.onMessageReceived(JsonUtils.jsonToMap(pushMessage.toJson()), !appOnForeground);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse PushMessage from JSON", e);
        }
        return !PushwooshPlugin.showForegroundPush && appOnForeground || super.onMessageReceived(pushMessage);
    }

    @Override
    protected void onMessageOpened(PushMessage pushMessage) {
        boolean appOnForeground = isAppOnForeground();
        try {
            PushwooshPlugin.onMessageAccepted(JsonUtils.jsonToMap(pushMessage.toJson()), !appOnForeground);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse PushMessage from JSON", e);
        }
    }
}
*/