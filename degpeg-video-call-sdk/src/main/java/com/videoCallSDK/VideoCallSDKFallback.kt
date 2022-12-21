package com.videoCallSDK

import android.content.Context
import android.net.Uri

/**
 * To be used as a fallback to open the Uri when Custom Tabs is not available.
 */
internal interface VideoCallSDKFallback {
    fun openUri(context: Context?, uri: Uri?)
}