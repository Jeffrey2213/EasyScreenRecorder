package com.example.myapplication

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log

class AppInfoManager (UIHandler : Handler) {
    private var mWorkHandlerThread: WorkHandlerThread

    init {
        mWorkHandlerThread = WorkHandlerThread(UIHandler,
            MainApplication.getMainPackageManager())

        mWorkHandlerThread.start()
    }
    public fun queryAllApps() {
        mWorkHandlerThread.queryAllApps()
    }


}