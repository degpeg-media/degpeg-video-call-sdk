package com.videoCallSDK

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.videoCallSDK.VideoCallSDKExtension.getUpdatedUrl
import com.videoCallSDK.VideoCallSDKUtils.CHROME_PACKAGE

class VideoCallSDKHelper(private val context: Context) {

    @ColorInt
    private var mToolbarColor: Int? = null

    @ColorInt
    private var mSecondaryToolbarColor: Int? = null

    @ColorInt
    private var mNavigationBarColor: Int? = null

    @ColorInt
    private var mNavigationBarDividerColor: Int? = null

    private var resultLauncher: ActivityResultLauncher<Intent>? = null

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

    fun startForResult(resultLauncher: ActivityResultLauncher<Intent>): VideoCallSDKHelper {
        this.resultLauncher = resultLauncher
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

        openCustomTab(
            builder.build(),
            Uri.parse(url.getUpdatedUrl()),
            object : VideoCallSDKFallback {
                override fun openUri(context: Context?, uri: Uri?) {
                    startIntentForChrome()
//                    Toast.makeText(context, "Unable to launch customTab", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun openCustomTab(
        customTabsIntent: CustomTabsIntent, uri: Uri, fallback: VideoCallSDKFallback?
    ) {
        val packageName = VideoCallSDKUtils.getCustomTabsPackages(context)
        //If we cant find a package name, it means theres no browser that supports
        //Chrome Custom Tabs installed. So, we fallback to the webview
        if (packageName == null) {
            fallback?.openUri(context, uri)
        } else {
            customTabsIntent.intent.setPackage(packageName)
            if (resultLauncher != null) {
                customTabsIntent.intent.data = uri
                resultLauncher?.launch(customTabsIntent.intent)
            } else {
                customTabsIntent.launchUrl(context, uri)
            }
        }
    }

    private fun startIntentForChrome() {
        MaterialAlertDialogBuilder(context).setMessage("CustomTab service is unsupported, Try installing chrome browser and make it as default app from setting.")
            .setPositiveButton("Install Chrome") { _, _ ->
                try {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$CHROME_PACKAGE"))
                    )
                } catch (a: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$CHROME_PACKAGE")
                        )
                    )
                }
            }.setNegativeButton("Cancel") { _, _ -> }.show()
    }

    /**
     * CustomTab Color scheme
     * */
    private fun getColorScheme(): CustomTabColorSchemeParams {
        val themeBuilder = CustomTabColorSchemeParams.Builder()
        if (mToolbarColor != null) themeBuilder.setToolbarColor(mToolbarColor!!)
        if (mSecondaryToolbarColor != null) themeBuilder.setSecondaryToolbarColor(
            mSecondaryToolbarColor!!
        )

        if (mNavigationBarColor != null) themeBuilder.setNavigationBarColor(mNavigationBarColor!!)
        if (mNavigationBarDividerColor != null) themeBuilder.setNavigationBarDividerColor(
            mNavigationBarDividerColor!!
        )

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
