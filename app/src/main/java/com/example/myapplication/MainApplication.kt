package com.example.myapplication

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler

class MainApplication : Application() {

    init {
        instance = this
    }
    companion object {
        private var instance : MainApplication ?= null
        private var mHandler : Handler ?= null
        fun getMainPackageManager() : PackageManager {
            return instance!!.packageManager
        }
        fun getMainApplicationContext() : Context {
            return instance!!.applicationContext
        }
        fun getMainActivityHandler() : Handler {
            return mHandler!!
        }
        fun setMainActivityHandler(handler : Handler) {
            mHandler = handler
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}