package com.customTabSDK.notifier

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color.green
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.service.notification.StatusBarNotification
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.customTabSDK.Controller
import com.customTabSDK.R
import com.customTabSDK.activity.IncomingCallActivity
import com.customTabSDK.activity.MainActivity
import java.util.concurrent.TimeUnit


object CallNotificationHelper {
    const val ACTION_DECLINE = "action_decline"
    const val ACTION_ANSWER = "action_answer"
    const val ACTION_MISSED = "action_missed"

    private val CALL_VIBRATION_PATTERN = longArrayOf(
        500, 600, 700, 800, 900, 900, 900, 1000, 1000, 1000, 1000, 900, 900, 800, 800, 700, 600,
        700, 700, 800, 900, 900, 1000, 1000, 1000, 1000, 1000, 900, 900, 800, 800, 700, 600,
        700, 700, 800, 900, 900, 1000, 1000, 1000, 1000, 1000, 900, 900, 800, 800, 700, 600,
        700, 800, 900, 900, 900, 1000, 1000, 1000, 900, 900, 800, 700, 600, 500, 400, 300
    )

    // Id is reserved for call notification as it is used later for comparison of active call list.
    private const val CALL_NOTIFICATION_ID = 10122

    private val CALL_NOTIFICATION_TIME_OUT = TimeUnit.SECONDS.toMillis(20)

    private const val CHANNEL_ID = "voip_call_id"
    private const val CHANNEL_NAME = "VOIP Call"

    private const val MISSED_CALL_CHANNEL_ID = "voip_missed_call_id"
    private const val MISSED_CALL_CHANNEL_NAME = "Missed Call"

    private val unique_id = (System.currentTimeMillis() and 0xfffffff).toInt()

    private val notificationManager: NotificationManager = Controller.instance
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannel()
    }

    fun show() {
        val context = Controller.instance
        val message = "Incoming call"
        val title = "John Deo"

        val bundle = bundleOf(
            "" to 1,
            "notification_id" to CALL_NOTIFICATION_ID,
            "isCall" to true
        )

        val fullScreenIntent = Intent(context, IncomingCallActivity::class.java)
        fullScreenIntent.action = Intent.ACTION_MAIN
        fullScreenIntent.putExtras(bundle)

        val intentAnswer = Intent(context, CallBroadcastReceiver::class.java)
        intentAnswer.action = ACTION_ANSWER
        intentAnswer.putExtras(bundle)

        val intentDecline = Intent(context, CallBroadcastReceiver::class.java)
        intentDecline.action = ACTION_DECLINE
        intentDecline.putExtras(bundle)

        val intentMissed = Intent(context, CallBroadcastReceiver::class.java)
        intentMissed.action = ACTION_MISSED
        intentMissed.putExtras(bundle)

        val answer = getActionText(context, R.string.answer, R.color.green)
        val decline = getActionText(context, R.string.decline, R.color.red)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(getOpponentImage())
                .setFullScreenIntent(getFullScreenPendingIntent(fullScreenIntent), true)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setAutoCancel(false)
                .setTimeoutAfter(CALL_NOTIFICATION_TIME_OUT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDeleteIntent(getPendingIntentReceiver(intentMissed, 3))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .addAction(
                    R.drawable.ic_end_call,
                    decline,
                    getPendingIntentReceiver(intentDecline, 1)
                )
                .addAction(
                    R.drawable.ic_voice_call,
                    answer,
                    getPendingIntentReceiver(intentAnswer, 0)
                )

        createChannel()
        notificationManager.notify("2", CALL_NOTIFICATION_ID, builder.build())
    }


    private fun getOpponentImage(): Bitmap? {
        return Glide.with(Controller.instance).asBitmap().load(R.drawable.default_profile_image)
            .centerInside().circleCrop().submit().get()
    }

    private fun getPendingIntentReceiver(intent: Intent, request_code: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            Controller.instance,
            request_code,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getFullScreenPendingIntent(intent: Intent): PendingIntent {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            Controller.instance,
            unique_id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createChannel() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            channel.enableVibration(true)
            channel.enableLights(true)
//            channel.vibrationPattern = CALL_VIBRATION_PATTERN
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getActionText(
        context: Context,
        @StringRes stringRes: Int,
        @ColorRes colorRes: Int,
    ): Spannable {
        val spannable: Spannable = SpannableString(context.getText(stringRes))
        if (VERSION.SDK_INT >= VERSION_CODES.N_MR1) {
            spannable.setSpan(
                ForegroundColorSpan(context.getColor(colorRes)),
                0,
                spannable.length,
                0
            )
        }
        return spannable
    }


    private fun createMissedCallChannel() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(
                MISSED_CALL_CHANNEL_ID,
                MISSED_CALL_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMissedCall(
        title: String?,
        contentText: String?,
    ) {
        createMissedCallChannel()
        val context = Controller.instance
        val builder = NotificationCompat.Builder(Controller.instance, MISSED_CALL_CHANNEL_ID)
            .setColor(ContextCompat.getColor(Controller.instance, R.color.red))
            .setContentTitle(title)
            .setContentText(contentText)
            .setSubText(context.getString(R.string.call))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_call_missed)
            .setContentIntent(getPendingIntent(context, 1032))
            .setAutoCancel(true)
        NotificationManagerCompat.from(Controller.instance)
            .notify(("missed_" + 1).hashCode(), builder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    internal fun getPendingIntent(context: Context, uniqueId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context,
            uniqueId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun getActiveNotificationFromTag(tag: String): StatusBarNotification? {
        val notifications: Array<StatusBarNotification> = notificationManager.activeNotifications
        for (notification in notifications) {
            if (notification.tag != null && notification.tag == tag) return notification
        }
        return null
    }

    private fun isCallNotificationActive(): Boolean {
        val notifications: Array<StatusBarNotification> = notificationManager.activeNotifications
        for (notification in notifications) {
            if (notification.id == CALL_NOTIFICATION_ID) return true
        }
        return false
    }

    fun cancel(tag: Long?, notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(Controller.instance)
        notificationManager.cancel(tag.toString(), notificationId)
        cancel(notificationId)
    }

    @Synchronized
    fun cancel(id: Int) {
        val manager = getNotificationManager() ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val notifications = manager.activeNotifications ?: return

            var groupKey: String? = null
            for (notification in notifications) {
                if (id == notification.id) {
                    groupKey = notification.groupKey
                    break
                }
            }
            var counter = 0
            for (notification in notifications) {
                if (notification.groupKey == groupKey) {
                    counter++
                }
            }
        }
        manager.cancel(id)
    }

    private var manager: NotificationManager? = null
    private fun getNotificationManager(): NotificationManager? {
        if (manager != null) return manager
        manager = Controller.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager
    }
}