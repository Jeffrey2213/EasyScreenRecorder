package com.example.myapplication

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference

class FavoriteAppsFragment: Fragment {
    private lateinit var mInfoManager : AppInfoManager
    private var mAppInfoList : ArrayList<AppInfo> = ArrayList<AppInfo>()
    private var mMainUIHandler :Handler
    private lateinit var mFavoriteUIHandler : Handler
    constructor(handler : Handler) {
        mMainUIHandler = handler
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get the custom view for this fragment layout
        val view = inflater!!.inflate(R.layout.favoriteapps_fragment, container, false)
        var clickListener = AppInfoItemClickListener(mAppInfoList, mMainUIHandler!!)

        var adapter = MyAdapter(mAppInfoList)
        var viewManager = GridLayoutManager(MainApplication.getMainApplicationContext(),4)
        var recyclerView = view.findViewById<RecyclerView>(R.id.favoriteapps_recyclerview)

        adapter.setClickListener(clickListener)

        recyclerView.layoutManager = viewManager
        recyclerView.adapter = adapter

        mFavoriteUIHandler = UIHandler(recyclerView, adapter, clickListener)
        mInfoManager = AppInfoManager(mFavoriteUIHandler)

        // Return the fragment view/layout

        return view
    }

    override fun onResume() {
        super.onResume()
        mInfoManager.queryAllApps()
        LocalBroadcastManager.getInstance(MainApplication.getMainApplicationContext())
            .registerReceiver(Receiver(mFavoriteUIHandler), IntentFilter(ADD_TO_FAVORITE))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(MainApplication.getMainApplicationContext())
            .unregisterReceiver(Receiver(mFavoriteUIHandler))
    }

    companion object {
        var ADD_TO_FAVORITE = "com.example.myapplication.addtofavorite"
        var MSG_UPDATE_APPINFO = 0
        var MSG_ADD_FAVORITE = 1
        class UIHandler : Handler {
            private  var mFavorite = ArrayList<String>()
            private  var mRecyclerView : WeakReference<RecyclerView>
            private  var mViewAdapter : WeakReference<MyAdapter> ?= null
            private  var mItemClick : WeakReference<AppInfoItemClickListener> ?= null
            private  var mAppList : ArrayList<AppInfo> ?= null
            constructor(recyclerView : RecyclerView, adapter : MyAdapter, listener : AppInfoItemClickListener) {
                mRecyclerView = WeakReference(recyclerView)
                mViewAdapter = WeakReference(adapter)
                mItemClick = WeakReference(listener)
                var default = MainApplication.getMainApplicationContext().getResources().getStringArray(R.array.default_list);
                for (app : String in default) {
                    mFavorite.add(app)
                }
            }

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_UPDATE_APPINFO -> {
                        var appList: ArrayList<AppInfo> = msg!!.obj as ArrayList<AppInfo>
                        var navList: ArrayList<AppInfo> = ArrayList<AppInfo>()
                        if (appList != null) {
                            for (appInfo: AppInfo in appList) {
                                for (favorite: String in mFavorite) {
                                    if (appInfo.getPackageName().toLowerCase().compareTo(favorite.toLowerCase()) == 0) {
                                        navList.add(appInfo)
                                    }
                                }
                            }
                            mViewAdapter!!.get()!!.updateData(navList)
                            mItemClick!!.get()!!.updateData(navList)
                            mRecyclerView!!.get()!!.adapter!!.notifyDataSetChanged()
                            mAppList = appList
                        }
                    }

                    MSG_ADD_FAVORITE -> {
                        var favName = msg.obj as String
                        var addFavorite = true
                        for (pkgName : String in mFavorite) {
                            if (pkgName.compareTo(favName) == 0) {
                                addFavorite = false
                                break
                            }
                        }
                        if (addFavorite) {
                            mFavorite.add(favName)
                            var navList: ArrayList<AppInfo> = ArrayList<AppInfo>()
                            if (mAppList!! != null && !mAppList!!.isEmpty()) {
                                for (appInfo: AppInfo in mAppList!!) {
                                    for (favorite: String in mFavorite) {
                                        if (appInfo.getPackageName().toLowerCase().compareTo(favorite.toLowerCase()) == 0) {
                                            navList.add(appInfo)
                                            Log.i("jeffrey-dbg","appInfo = " + appInfo.getName() +", pkg = " + appInfo.getName())
                                        }
                                    }
                                }
                                mViewAdapter!!.get()!!.updateData(navList)
                                mItemClick!!.get()!!.updateData(navList)
                                mRecyclerView!!.get()!!.adapter!!.notifyDataSetChanged()
                            }
                        }

                    }
                }
            }
        }

        class AppInfoItemClickListener: ItemClick{
            private var mAppInfoList : WeakReference<ArrayList<AppInfo>>
            private var mMainUIHandler : Handler ?= null
            constructor(appInfoList : ArrayList<AppInfo>, handler : Handler) {
                mAppInfoList = WeakReference(appInfoList)
                mMainUIHandler = handler
            }
            override fun OnItemClick(v: View, position: Int) {
                if (mAppInfoList != null && mAppInfoList!!.get()!!.size > 0) {
                    val packageName = mAppInfoList.get()!!.get(position).getPackageName()
                    var message = Message()
                    message.obj = packageName
                    message.what = MainActivity.Companion.MainUIHandler.MSG_LAUNCH_APP
                    mMainUIHandler!!.sendMessage(message)
                }
            }
            fun updateData(appInfoList: ArrayList<AppInfo>) {
                mAppInfoList = WeakReference(appInfoList)
            }
        }

        class Receiver : BroadcastReceiver {
            private var mHandler : WeakReference<Handler>
            constructor(handler : Handler) {
                mHandler = WeakReference(handler)
            }
            override fun onReceive(context: Context?, intent: Intent?) {
                var favorite = intent!!.getStringExtra("favorite") as String
                if (favorite != null) {
                    var msg = Message()
                    msg.what = MSG_ADD_FAVORITE
                    msg.obj = favorite
                    mHandler.get()!!.sendMessage(msg)
                }

            }
        }
    }

}