package com.customTabSDK.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.customTabSDK.databinding.ActivityMainBinding
import com.customTabSDK.notifier.CallNotificationHelper
import com.videoCallSDK.VideoCallSDKHelper


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.edtEditText.setText("https://slf.degpeg.com/aditya/camera/")
        binding.edtEditText.setText("https://github.com/degpeg-media/degpeg-video-call-sdk")
        binding.btnCustomTab.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnIncomingCall.setOnClickListener(this)

        onNewIntent(intent)


//        VideoCallSDKHelper.with(this)
//            .setToolbarColor(getColor("#000000"))
//            .setSecondaryToolbarColor(getColor("#FFFFFF"))
//            .setNavigationBarColor(Color.BLACK)
//            .setNavigationBarDividerColor(Color.GREEN)
//            .launchUrl("https://slf.degpeg.com/aditya/camera/")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val isCall = intent?.getBooleanExtra("isCall", false) ?: false
        if (isCall) {
            VideoCallSDKHelper.with(this)
                .setToolbarColor(getColor("#000000"))
                .setSecondaryToolbarColor(getColor("#FFFFFF"))
                .setNavigationBarColor(Color.BLACK)
                .setNavigationBarDividerColor(Color.GREEN)
                .startForResult(startForResult)
                .launchUrl("https://developer.android.com/")
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Toast.makeText(this, "Finished with result", Toast.LENGTH_SHORT).show()
            finish()
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