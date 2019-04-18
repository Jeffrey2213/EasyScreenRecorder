package com.example.myapplication

import android.graphics.drawable.Drawable

class AppInfo {
    private var mAppName : String ?= null
    private var mAppIcon : Drawable ?= null
    constructor(appName : String, appIcon : Drawable) {
        mAppName = appName
        mAppIcon = appIcon
    }
    public fun getName() : String {
        return mAppName!!
    }
    public fun getIcon() : Drawable {
        return mAppIcon!!
    }

}