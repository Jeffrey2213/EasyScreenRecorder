package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.content.pm.ApplicationInfo



class AppInfoModel(var UIHandler : Handler, var pm : PackageManager): HandlerThread("AppInfoWorkThread") {

    private var mWorkHandler : Handler? = null

    companion object {
        const val MSG_QUERY_ALL_APPS = 1
    }

    fun queryAllApps() {
        if (mWorkHandler == null) {
            mWorkHandler = getHandler(looper)
        }
        mWorkHandler?.removeMessages(MSG_QUERY_ALL_APPS)
        var msg = Message()
        msg.what = MSG_QUERY_ALL_APPS
        mWorkHandler?.sendMessage(msg)
    }

    private fun getHandler(looper: Looper): Handler {
        var mAppInfoList = ArrayList<AppInfo>()
        return object : Handler(looper) {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                var list : List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                mAppInfoList.clear()
                when (msg!!.what) {
                    MSG_QUERY_ALL_APPS -> {
                        for (packageInfo in list) {
                            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                                val appName = pm.getApplicationLabel(packageInfo).toString()
                                val appIcon = pm.getApplicationIcon(packageInfo.packageName)
                                val packageName = packageInfo.packageName
                                var appInfo = AppInfo(appName, appIcon, packageName)
                                mAppInfoList.add(appInfo)
                            }
                        }
                    }
                }
                sendMessage(UIHandler, mAppInfoList)

            }
            fun sendMessage(handler : Handler, appInfoList : ArrayList<AppInfo>) {
                if (UIHandler is MainActivity.Companion.NavUIHandler) {
                    var uimsg = Message()
                    uimsg.obj = appInfoList
                    uimsg.what = MainActivity.Companion.NavUIHandler.MSG_UPDATE_APPINFO
                    UIHandler.sendMessage(uimsg)
                }
                if (UIHandler is FavoriteAppsFragment.Companion.UIHandler) {
                    var uimsg = Message()
                    uimsg.obj = appInfoList
                    uimsg.what = FavoriteAppsFragment.MSG_UPDATE_APPINFO
                    UIHandler.sendMessage(uimsg)
                }
            }

        }
    }
}