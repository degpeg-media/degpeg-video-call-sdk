package com.videoCallSDK

internal object VideoCallSDKExtension {

    internal fun String.getUpdatedUrl(): String {
        var url: String = this
        if (url.startsWith("http")) return this
        if (!url.startsWith("www.")
            && !url.startsWith("http://")
        ) url = "www.$url"

        if (!url.startsWith("http://")) url = "http://$url"
        return url
    }
}