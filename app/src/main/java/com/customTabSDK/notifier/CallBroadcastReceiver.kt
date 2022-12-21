package com.customTabSDK.notifier

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.customTabSDK.activity.MainActivity
import com.customTabSDK.notifier.CallNotificationHelper.ACTION_ANSWER
import com.customTabSDK.notifier.CallNotificationHelper.ACTION_DECLINE
import com.customTabSDK.notifier.CallNotificationHelper.ACTION_MISSED

class CallBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras == null) return
        val action = intent.action ?: return

        val notificationId = intent.getIntExtra("notification_id", 0)
        CallNotificationHelper.cancel(2, notificationId)

        when (action) {
            ACTION_MISSED -> {
                Log.e("CallBroadcastReceiver", "ACTION_MISSED")
                val title = "John Deo"
                CallNotificationHelper.showMissedCall("Missed call", title)
            }
            ACTION_DECLINE -> {
                Log.e("CallBroadcastReceiver", "ACTION_DECLINE")
                //Do nothing system itself handle the notification.
            }
            ACTION_ANSWER -> {
                Log.e("CallBroadcastReceiver", "ACTION_ANSWER")
                val callIntent = Intent(context, MainActivity::class.java)
                callIntent.putExtras(intent.extras!!)
                callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(callIntent)
            }
        }
    }
}