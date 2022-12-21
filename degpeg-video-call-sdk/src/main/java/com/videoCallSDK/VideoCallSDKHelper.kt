package com.videoCallSDK

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.videoCallSDK.VideoCallSDKExtension.getUpdatedUrl

class VideoCallSDKHelper(private val context: Context) {

    @ColorInt
    private var mToolbarColor: Int? = null

    @ColorInt
    private var mSecondaryToolbarColor: Int? = null

    @ColorInt
    private var mNavigationBarColor: Int? = null

    @ColorInt
    private var mNavigationBarDividerColor: Int? = null

    /**
     * Theme customization
     */
    fun setToolbarColor(@ColorInt color: Int): VideoCallSDKHelper {
        mToolbarColor = color or -0x1000000
        return this
    }

    fun setSecondaryToolbarColor(@ColorInt color: Int): VideoCallSDKHelper {
        mSecondaryToolbarColor = color
        return this
    }

    fun setNavigationBarColor(@ColorInt color: Int): VideoCallSDKHelper {
        mNavigationBarColor = color or -0x1000000
        return this
    }

    fun setNavigationBarDividerColor(@ColorInt color: Int): VideoCallSDKHelper {
        mNavigationBarDividerColor = color
        return this
    }

    /**
     * Launch url with customTab
     * */
    fun launchUrl(url: String) {
        if (url.isEmpty()) return
        val builder = CustomTabsIntent.Builder()
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .setDefaultColorSchemeParams(getColorScheme())
            .setShowTitle(true)

        openCustomTab(builder.build(), Uri.parse(url.getUpdatedUrl()), object : VideoCallSDKFallback {
            override fun openUri(context: Context?, uri: Uri?) {
                Toast.makeText(context, "Unable to launch customTab", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openCustomTab(
        customTabsIntent: CustomTabsIntent,
        uri: Uri,
        fallback: VideoCallSDKFallback?
    ) {
        val packageName = VideoCallSDKUtils.getPackageNameToUse(context)
        //If we cant find a package name, it means theres no browser that supports
        //Chrome Custom Tabs installed. So, we fallback to the webview
        if (packageName == null) {
            fallback?.openUri(context, uri)
        } else {
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(context, uri)
        }
    }

    /**
     * CustomTab Color scheme
     * */
    private fun getColorScheme(): CustomTabColorSchemeParams {
        val themeBuilder = CustomTabColorSchemeParams.Builder()
        if (mToolbarColor != null)
            themeBuilder.setToolbarColor(mToolbarColor!!)
        if (mSecondaryToolbarColor != null)
            themeBuilder.setSecondaryToolbarColor(mSecondaryToolbarColor!!)

        if (mNavigationBarColor != null)
            themeBuilder.setNavigationBarColor(mNavigationBarColor!!)
        if (mNavigationBarDividerColor != null)
            themeBuilder.setNavigationBarDividerColor(mNavigationBarDividerColor!!)

        return themeBuilder.build()
    }

    /**
     * Returns the CustomTabHelper instance
     * */
    companion object {
        fun with(context: Context): VideoCallSDKHelper {
            return VideoCallSDKHelper(context)
        }
    }
}
