package com.customTabSDK.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.customTabSDK.databinding.ActivityMainBinding
import com.videoCallSDK.VideoCallSDKHelper
import com.customTabSDK.notifier.CallNotificationHelper

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtEditText.setText("google.com")
        binding.btnCustomTab.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnIncomingCall.setOnClickListener(this)

        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val isCall = intent?.getBooleanExtra("isCall", false) ?: false
        if (isCall){
            VideoCallSDKHelper.with(this)
                .setToolbarColor(getColor("#000000"))
                .setSecondaryToolbarColor(getColor("#FFFFFF"))
                .setNavigationBarColor(Color.BLACK)
                .setNavigationBarDividerColor(Color.GREEN)
                .launchUrl("https://developer.android.com/")
        }
    }


    override fun onClick(v: View?) {
        when (v) {
            binding.btnClose -> {
                finish()
            }
            binding.btnIncomingCall -> {
                Thread {
                    CallNotificationHelper.show()
                }.start()
                finish()
            }
            binding.btnCustomTab -> {
                val url = binding.edtEditText.text.toString().trim()
                if (url.isEmpty()) return
                VideoCallSDKHelper.with(this)
                    .setToolbarColor(getColor("#000000"))
                    .setSecondaryToolbarColor(getColor("#FFFFFF"))
                    .setNavigationBarColor(Color.BLACK)
                    .setNavigationBarDividerColor(Color.GREEN)
                    .launchUrl(url)
            }
        }
    }

    private fun getColor(colorString: String): Int {
        return try {
            Color.parseColor(colorString)
        } catch (ex: NumberFormatException) {
            Log.i("getColor", "Unable to parse Color: $colorString")
            Color.LTGRAY
        }
    }
}