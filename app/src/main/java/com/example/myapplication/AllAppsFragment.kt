package com.example.myapplication

import android.app.PendingIntent.getActivity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.lang.Exception
import java.lang.ref.WeakReference

class AllAppsFragment : Fragment {

    private lateinit var mInfoManager : AppInfoManager
    private var mAppInfoList : ArrayList<AppInfo> = ArrayList<AppInfo>()
    private var mMainUIHandler : Handler ?= null
    constructor(handler : Handler?) {
        mMainUIHandler = handler
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get the custom view for this fragment layout
        val view = inflater!!.inflate(R.layout.allapps_fragment, container, false)
        var clickListener = AppInfoItemClickListener(mAppInfoList, mMainUIHandler!!)
        var adapter = MyAdapter(mAppInfoList)
        var viewManager = LinearLayoutManager(MainApplication.getMainApplicationContext())
        var recyclerView = view.findViewById<RecyclerView>(R.id.allapps_recyclerview)
        adapter.setClickListener(clickListener)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = adapter

        var uiHandler = UIHandler(recyclerView, adapter, clickListener)
        mInfoManager = AppInfoManager(uiHandler)

        // Return the fragment view/layout

        return view
    }

    override fun onResume() {
        super.onResume()
        mInfoManager.queryAllApps()
    }

    companion object {
        class UIHandler : Handler {
            private  var mRecyclerView : WeakReference<RecyclerView>
            private  var mViewAdapter : WeakReference<MyAdapter> ?= null
            private  var mItemClick : WeakReference<AppInfoItemClickListener> ?= null
            constructor(recyclerView : RecyclerView, adapter : MyAdapter, listener : AppInfoItemClickListener) {
                mRecyclerView = WeakReference(recyclerView)
                mViewAdapter = WeakReference(adapter)
                mItemClick = WeakReference(listener)
            }

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                var appList : ArrayList<AppInfo> = msg!!.obj as ArrayList<AppInfo>
                mViewAdapter!!.get()!!.updateData(appList)
                mItemClick!!.get()!!.updateData(appList)
                mRecyclerView!!.get()!!.adapter!!.notifyItemChanged(appList.size)
            }
        }

        class AppInfoItemClickListener: ItemClick {
            private var mAppInfoList : WeakReference<ArrayList<AppInfo>>
            private var mMainUIHandler : Handler ?= null
            constructor(appInfoList : ArrayList<AppInfo>, handler : Handler) {
                mAppInfoList = WeakReference(appInfoList)
                mMainUIHandler = handler
            }
            override fun OnItemClick(v: View, position: Int) {
                if (mAppInfoList != null && mAppInfoList!!.get()!!.size > 0) {
                    Log.i("jeffrey-dbg", "name = " + mAppInfoList.get()!!.get(position).getName())
                    val packageName = mAppInfoList.get()!!.get(position).getPackageName()
                    var message = Message()
                    message.obj = packageName
                    mMainUIHandler!!.sendMessage(message)
                }
            }
            fun updateData(appInfoList: ArrayList<AppInfo>) {
                mAppInfoList = WeakReference(appInfoList)
            }
        }
    }


}