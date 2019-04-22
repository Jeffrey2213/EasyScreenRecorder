package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference

class FavoriteAppsFragment: Fragment() {
    private lateinit var mInfoManager : AppInfoManager
    private var mAppInfoList : ArrayList<AppInfo> = ArrayList<AppInfo>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get the custom view for this fragment layout
        val view = inflater!!.inflate(R.layout.favoriteapps_fragment, container, false)
        var clickListener = AppInfoItemClickListener(mAppInfoList)

        var adapter = MyAdapter(mAppInfoList)
        var viewManager = GridLayoutManager(MainApplication.getMainApplicationContext(),3)
        var recyclerView = view.findViewById<RecyclerView>(R.id.favoriteapps_recyclerview)

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

        class AppInfoItemClickListener: ItemClick{
            private var mAppInfoList : WeakReference<ArrayList<AppInfo>>

            constructor(appInfoList : ArrayList<AppInfo>) {
                mAppInfoList = WeakReference(appInfoList)
            }
            override fun OnItemClick(v: View, position: Int) {
                if (mAppInfoList != null && mAppInfoList!!.get()!!.size > 0) {
                    Log.i("jeffrey-dbg","favorite, name = " + mAppInfoList.get()!!.get(position).getName())
                }
            }
            fun updateData(appInfoList: ArrayList<AppInfo>) {
                mAppInfoList = WeakReference(appInfoList)
            }
        }
    }


}