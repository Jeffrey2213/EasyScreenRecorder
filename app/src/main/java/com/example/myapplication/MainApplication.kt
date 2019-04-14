package com.example.myapplication

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager

class MainApplication : Application() {

    init {
        instance = this
    }
    companion object {
        private var instance : MainApplication ?= null;
        fun getMainPackageManager() : PackageManager {
            return instance!!.packageManager;
        }
        fun getMainApplicationContext() : Context {
            return instance!!.applicationContext;
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun getMyString() : String{
        return "hellloApp";
    }


}