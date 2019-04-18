package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.content.pm.ApplicationInfo



class WorkHandlerThread(var UIHandler : Handler, var pm : PackageManager): HandlerThread("WorkThread") {

    private var mWorkHandler : Handler? = null

    companion object {
        const val MSG_QUERY_ALL_APPS = 1
        const val MSG_QUERY_A_APP = 2
    }
    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mWorkHandler = getHandler(looper)
    }
    public fun queryAllApps() {
        var msg = Message()
        mWorkHandler?.sendMessage(msg)
    }

    private fun getHandler(looper: Looper): Handler {
        return object : Handler(looper) {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                var results  = "helloworls"
               // when (msg!!.what) {
                    //MSG_QUERY_ALL_APPS -> results = QueryAllApps()
                var list : List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                var appInfoList = ArrayList<AppInfo>()
                for (packageInfo in list) {
                    if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                        val appName = pm.getApplicationLabel(packageInfo).toString()
                        val appIcon = pm.getApplicationIcon(packageInfo.packageName)
                        var appInfo = AppInfo(appName, appIcon)
                        appInfoList.add(appInfo)
                    } else {
                        //System App
                    }
                }
                var uimsg = Message()
                uimsg.obj = appInfoList
                UIHandler.sendMessage(uimsg)
            }

        }
    }
    private fun QueryAllApps() : String {
        return "hello all apps"
    }
}