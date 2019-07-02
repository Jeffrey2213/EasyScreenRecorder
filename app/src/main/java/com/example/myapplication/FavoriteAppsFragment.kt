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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import java.lang.ref.WeakReference

class FavoriteAppsFragment() : Fragment() {
    private lateinit var mInfoManager : AppInfoManager
    private var mAppInfoList : ArrayList<AppInfo> = ArrayList<AppInfo>()
    private lateinit var mFavoriteUIHandler : UIHandler
    private lateinit var mAdapter: MyAdapter
    private lateinit var mClickListener : AppInfoItemClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get the custom view for this fragment layout
        val view = inflater!!.inflate(R.layout.favoriteapps_fragment, container, false)
        mClickListener = AppInfoItemClickListener(mAppInfoList,
            MainApplication.getMainActivityHandler())

        mAdapter = MyAdapter(mAppInfoList)
        var viewManager = GridLayoutManager(MainApplication.getMainApplicationContext(),4)
        var recyclerView = view.findViewById<RecyclerView>(R.id.favoriteapps_recyclerview)

        mAdapter.setClickListener(mClickListener)

        recyclerView.layoutManager = viewManager
        recyclerView.adapter = mAdapter

        mFavoriteUIHandler = UIHandler(recyclerView, mClickListener)
        mFavoriteUIHandler.setAdapter(mAdapter)
        mInfoManager = AppInfoManager(mFavoriteUIHandler)

        mClickListener.setFavoriteHandler(mFavoriteUIHandler)
        // Return the fragment view/layout

        return view
    }

    override fun onResume() {
        super.onResume()
        mFavoriteUIHandler.setAdapter(mAdapter)
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
        var MSG_REMOVE_FAVORITE = 2

        class UIHandler : Handler {
            private  var mFavorite = ArrayList<String>()
            private  var mRecyclerView : WeakReference<RecyclerView>
            private  lateinit var mViewAdapter : MyAdapter
            private  var mItemClick : AppInfoItemClickListener
            private  var mAppList : ArrayList<AppInfo> ?= null
            constructor(recyclerView : RecyclerView, listener : AppInfoItemClickListener) {
                mRecyclerView = WeakReference(recyclerView)
                mItemClick = listener
                var default = MainApplication.getMainApplicationContext().getResources().getStringArray(R.array.default_list);
                for (app : String in default) {
                    mFavorite.add(app)
                }
            }
            fun setAdapter (adapter : MyAdapter) {
                mViewAdapter = adapter
            }

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_UPDATE_APPINFO -> {
                        var appList: ArrayList<AppInfo> = msg!!.obj as ArrayList<AppInfo>
                        var navList: ArrayList<AppInfo> = ArrayList<AppInfo>()
                        var templist = ArrayList<AppInfo>()
                        if (appList != null) {
                            for (appInfo: AppInfo in appList) {
                                for (favorite: String in mFavorite) {
                                    if (appInfo.getPackageName().toLowerCase().compareTo(favorite.toLowerCase()) == 0) {
                                        navList.add(appInfo)
                                    }
                                }
                            }

                            templist.addAll(appList)
                            mAppList = templist

                            if (mViewAdapter != null && mRecyclerView != null && mItemClick != null) {
                                mViewAdapter.updateData(navList)
                                mItemClick.updateData(navList)
                                mRecyclerView.get()!!.adapter!!.notifyDataSetChanged()
                            }

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
                                        }
                                    }
                                }
                                if (mViewAdapter != null && mRecyclerView != null && mItemClick != null) {
                                    mViewAdapter.updateData(navList)
                                    mItemClick.updateData(navList)
                                    mRecyclerView!!.get()!!.adapter!!.notifyDataSetChanged()
                                }
                            }
                        }

                    }

                    MSG_REMOVE_FAVORITE -> {
                        var removePosition = msg.arg1
                        mFavorite.removeAt(removePosition)

                        var navList: ArrayList<AppInfo> = ArrayList<AppInfo>()
                        if (mAppList!! != null && !mAppList!!.isEmpty()) {
                            for (appInfo: AppInfo in mAppList!!) {
                                for (favorite: String in mFavorite) {
                                    if (appInfo.getPackageName().toLowerCase().compareTo(favorite.toLowerCase()) == 0) {
                                        navList.add(appInfo)
                                    }
                                }
                            }
                            if (mViewAdapter != null && mRecyclerView != null && mItemClick != null) {
                                mViewAdapter.updateData(navList)
                                mItemClick.updateData(navList)
                                mRecyclerView!!.get()!!.adapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }

        class AppInfoItemClickListener: ItemClick, PopupMenu.OnMenuItemClickListener {
            private lateinit var mAppInfoList : ArrayList<AppInfo>
            private var mMainUIHandler : WeakReference<Handler>
            private lateinit var mFavoriteHandler : WeakReference<UIHandler>
            private var mLongPressPosition = 0
            constructor(appInfoList : ArrayList<AppInfo>, handler : Handler) {
                copyList(appInfoList)
                mMainUIHandler = WeakReference(handler)
            }
            override fun OnItemClick(v: View, position: Int) {
                if (mAppInfoList != null) {
                    val packageName = mAppInfoList.get(position).getPackageName()
                    var message = Message()
                    message.obj = packageName
                    message.what = MainActivity.Companion.MainUIHandler.MSG_LAUNCH_APP
                    mMainUIHandler.get()!!.sendMessage(message)
                }
            }
            override fun OnItemLongClick(v: View, position: Int) {
                mLongPressPosition = position
                var popMenu = PopupMenu(MainApplication.getMainApplicationContext(), v)
                popMenu.setOnMenuItemClickListener(this)
                popMenu.inflate(R.menu.menu_remove_uninstall)
                popMenu.show()
            }
            fun updateData(appInfoList: ArrayList<AppInfo>) {
                copyList(appInfoList)
            }
            private fun copyList(list : ArrayList<AppInfo>) {
                var tmp = ArrayList<AppInfo>()
                tmp.addAll(list)
                mAppInfoList = tmp
            }
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                Log.i("jeffrey-dbg","fragment longpress = " + mLongPressPosition)
                var message = mFavoriteHandler.get()!!.obtainMessage(MSG_REMOVE_FAVORITE)
                message.arg1 = mLongPressPosition
                mFavoriteHandler.get()!!.sendMessage(message)
                return true
            }
            fun setFavoriteHandler(favHandler : UIHandler) {
                mFavoriteHandler = WeakReference(favHandler)
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