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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pm : PackageManager = MainApplication.getMainPackageManager()
        val appContext : Context = MainApplication.getMainApplicationContext()
        val list :List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        var result = ArrayList<String>()
        var icons = ArrayList<Drawable>()

        for (packageInfo in list) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                val currAppName = pm.getApplicationLabel(packageInfo).toString()
                val currAppIcon = pm.getApplicationIcon(packageInfo.packageName)
                result.add(currAppName)
                icons.add(currAppIcon)
            } else {
                //System App
            }
        }

        viewManager = GridLayoutManager(this,2)
        viewAdapter = MyAdapter(result, icons)
        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
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
}
