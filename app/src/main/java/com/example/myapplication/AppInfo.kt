package com.example.myapplication

import android.content.Intent
import android.graphics.drawable.Drawable

class AppInfo {
    private var mAppName : String ?= null
    private var mAppIcon : Drawable ?= null
    private var mPackageName : String ?= null
    private var mFavorite : Boolean = false

    constructor(appName : String, appIcon : Drawable, packageName : String) {
        mAppName = appName
        mAppIcon = appIcon
        mPackageName = packageName
    }
    fun getName() : String {
        return mAppName!!
    }
    fun getIcon() : Drawable {
        return mAppIcon!!
    }
    fun getPackageName() : String {
        return mPackageName!!
    }
    fun setFavorite(enable : Boolean) {
        mFavorite = enable
    }


}