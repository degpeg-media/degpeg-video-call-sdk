<h1 align="center">Call Notification Integration</h1>

Call Notificaiton Integration document helps to implemenet the video/audio call notification and also it's guide to handle the actions for answer call or reject call. For more information please clone the example

# Preview
<p align="center">
<img src="https://github.com/degpeg-media/degpeg-video-call-sdk/blob/main/app/Screenshot_Call_1.png" alt="Screenshot_Call_1" width="200" height="400"> 
<img src="https://github.com/degpeg-media/degpeg-video-call-sdk/blob/main/app/Screenshot_Call_2.png" alt="Screenshot_Call_2" width="200" height="400">
<img src="https://github.com/degpeg-media/degpeg-video-call-sdk/blob/main/app/Screenshot_Call_3.png" alt="Screenshot_Call_3" width="200" height="400">
</p>

# Integration Steps

1. Add Notification permission in AndroidManifest.xml file

 ```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
```

2. Create the CallBroadcastReceiver and register in AndroidManifest.xml file

```xml
<receiver android:name=".notifier.CallBroadcastReceiver" />
```

```kotlin
class CallBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras == null) return
        val action = intent.action ?: return

        val notificationId = intent.getIntExtra("notification_id", 0)
        CallNotificationHelper.cancel(2, notificationId)

        when (action) {
            ACTION_MISSED -> {
                val title = "John Deo"
                CallNotificationHelper.showMissedCall("Missed call", title)
            }
            ACTION_DECLINE -> {
                //Do nothing system itself handle the notification.
            }
            ACTION_ANSWER -> {
                val callIntent = Intent(context, MainActivity::class.java)
                callIntent.putExtras(intent.extras!!)
                callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(callIntent)
            }
        }
    }
}
```

3. Create the ```IncomingCallActivity``` for manage the fullscreen calling view.

4. Add ```CallNotificationHelper``` class from sample code and modify the data as per your requirement, Like message, body, notification icons, etc.

5. Show the call notification using ```CallNotificationHelper.show()``` 
