package com.example.myapplication

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import java.lang.ref.WeakReference
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private lateinit var mInfoManager : AppInfoManager
    override fun onResume() {
        super.onResume()

        mInfoManager.queryAllApps()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var adapter = MyAdapter()
        var viewManager = GridLayoutManager(MainApplication.getMainApplicationContext(),3)
        var recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = adapter

        var uiHandler = UIHandler(recyclerView, adapter)
        mInfoManager = AppInfoManager(uiHandler)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        class UIHandler : Handler {
            private  var mRecyclerView : WeakReference<RecyclerView>
            private  var mViewAdapter : WeakReference<MyAdapter> ?= null

            constructor(recyclerView : RecyclerView, adapter : MyAdapter) {
                mRecyclerView = WeakReference(recyclerView)
                mViewAdapter = WeakReference(adapter)
            }

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                var appList : ArrayList<AppInfo> = msg!!.obj as ArrayList<AppInfo>
                mViewAdapter!!.get()?.updateData(appList)
                mRecyclerView!!.get()!!.adapter!!.notifyItemChanged(appList.size)
            }

        }
    }
}
