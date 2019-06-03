package com.example.myapplication

import android.os.Handler

class AppInfoManager (UIHandler : Handler) {
    private var mAppInfoModel: AppInfoModel

    init {
        mAppInfoModel = AppInfoModel(UIHandler, MainApplication.getMainPackageManager())
        mAppInfoModel.start()
    }

    fun queryAllApps() {
        mAppInfoModel.queryAllApps()
    }
}