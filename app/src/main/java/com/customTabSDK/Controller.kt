package com.customTabSDK

import android.app.Application

class Controller : Application() {

    companion object {
        private val TAG = Controller::class.java.name
        lateinit var instance: Controller
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

