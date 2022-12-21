package com.customTabSDK.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.customTabSDK.databinding.ActivityIncomingCallBinding
import com.customTabSDK.notifier.CallNotificationHelper

class IncomingCallActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityIncomingCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAccept.setOnClickListener(this)
        binding.btnReject.setOnClickListener(this)
    }

    override fun onPause() {
        super.onPause()
        finishAndRemoveTask()
    }

    override fun onClick(p0: View?) {
        val notificationId = intent.getIntExtra("notification_id", 0)

        when(p0){
            binding.btnAccept->{
                CallNotificationHelper.cancel(2, notificationId)
                val callIntent = Intent(this, MainActivity::class.java)
                callIntent.putExtras(intent.extras!!)
                callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(callIntent)
                finish()
            }
            binding.btnReject->{
                CallNotificationHelper.cancel(2, notificationId)
                finishAndRemoveTask()
            }
        }
    }
}