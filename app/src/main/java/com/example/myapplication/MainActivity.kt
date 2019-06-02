package com.example.myapplication

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log

import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.android.synthetic.main.fav_app_layout.view.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var mSearch : EditText ?= null

    private lateinit var mInfoManager: AppInfoManager
    private var mAppInfoList: ArrayList<AppInfo> = ArrayList<AppInfo>()
    private lateinit var mEditText : EditText
    private lateinit var mNaviHandler : NavUIHandler
    private var mFragment : Fragment ?= null

    override fun onResume() {
        super.onResume()
        mInfoManager.queryAllApps()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewpager)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        var drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        val mainUIHandler = MainUIHandler()

        setupViewPager(viewPager!!, mainUIHandler)

        // navigation view init
        var navMenu = navigationView.menu.addSubMenu("All apps")
        var naviAdapter = NaviAdapter()
        mNaviHandler = NavUIHandler(naviAdapter, mainUIHandler)
        //var listener = NaviSelectorListener(nvUIHandler, drawerLayout)
        val navlist : ListView = findViewById(R.id.nav_list)
        val navListListener = NaviListItemClickListener(mNaviHandler, naviAdapter,drawerLayout)
        navlist.adapter = naviAdapter
        navlist.onItemClickListener = navListListener
        navlist.onItemLongClickListener = navListListener
        mInfoManager = AppInfoManager(mNaviHandler)
        mEditText = initalEditText(mInfoManager, naviAdapter)
        navigationView.addHeaderView(mEditText)
        //navigationView.setNavigationItemSelectedListener (listener)
        //navigationView.itemIconTintList = null
    }
    private fun initalEditText(infoManager : AppInfoManager, adapter: BaseAdapter) : EditText {
        val editText = EditText(MainApplication.getMainApplicationContext())
        var textWatcher = InputTextWatcher(adapter, infoManager)
        editText.hint="Search"
        editText.setTextColor(Color.WHITE)
        editText.setHintTextColor(Color.WHITE)
        editText.setBackgroundColor(Color.parseColor("#99424242"))
        editText.setTextSize(18F)
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search, 0);
        var params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(0,120,0,0)
        editText.layoutParams = params
        editText.addTextChangedListener(textWatcher)
        textWatcher.setEditText(editText)
        return editText
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    private fun setupViewPager(viewPager: ViewPager, handler : Handler) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        mFragment = FavoriteAppsFragment(handler)
        adapter.addFragment(mFragment!! , "Favorite")
        viewPager.adapter = adapter
    }

    companion object {
        class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
            private val mFragmentList = ArrayList<Fragment>()
            private val mFragmentTitleList = ArrayList<String>()

            override fun getItem(position: Int): Fragment {
                return mFragmentList[position]
            }

            override fun getCount(): Int {
                return mFragmentList.size
            }

            fun addFragment(fragment: Fragment, title: String) {
                mFragmentList.add(fragment)
                mFragmentTitleList.add(title)
            }

            override fun getPageTitle(position: Int): CharSequence {
                return mFragmentTitleList[position]
            }
        }
        fun startActivity(context: Context, intent : Intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        class MainUIHandler : Handler() {
            companion object {
                public var MSG_LAUNCH_APP = 0
            }
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what) {
                    MSG_LAUNCH_APP -> {
                        var packageName = msg!!.obj as String
                        var intent = MainApplication.getMainPackageManager().getLaunchIntentForPackage(packageName)
                        startActivity(MainApplication.getMainApplicationContext(), intent!!)
                    }
                }
            }
        }

        class NavUIHandler : Handler {
            private var mAdapter : NaviAdapter
            private var mAppInfoList : ArrayList<AppInfo> ?= null
            private var mMainHandler : WeakReference<Handler>
            companion object {
                 var MSG_UPDATE_APPINFO = 0
                 var MSG_LAUNCH_APP_BY_APPNAME = 1
            }

            constructor(adapter : NaviAdapter, handler : Handler) {
                mAdapter = adapter
                mMainHandler = WeakReference(handler)
            }
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what) {
                    MSG_UPDATE_APPINFO -> {
                        var appList: ArrayList<AppInfo> = msg!!.obj as ArrayList<AppInfo>
                        appList.sortBy { it.getName() }
                        mAppInfoList = appList
                        mAdapter.updateData(appList)
                        mAdapter.notifyDataSetChanged()
                    }

                    MSG_LAUNCH_APP_BY_APPNAME -> {
                        var appName = msg.obj as String
                        for (info : AppInfo in mAppInfoList!!) {
                             if (info.getName().equals(appName)) {
                                 sendMessage(info.getPackageName())
                             }
                        }
                    }
                }
            }
            fun sendMessage(title : String) {
                var msg = Message()
                msg.obj = title
                msg.what = MainUIHandler.MSG_LAUNCH_APP
                mMainHandler.get()!!.sendMessage(msg)
            }

        }

        class NaviSelectorListener : NavigationView.OnNavigationItemSelectedListener {
            private var mDrawerLayout: WeakReference<DrawerLayout>
            private var mNaviHandler: WeakReference<NavUIHandler>
            constructor(naviHandler : NavUIHandler , drawer : DrawerLayout) {
                 mDrawerLayout = WeakReference(drawer)
                 mNaviHandler = WeakReference(naviHandler)
            }
            override fun onNavigationItemSelected(p0: MenuItem): Boolean {
                sendMessage(p0.title as String)
                mDrawerLayout.get()!!.closeDrawer(GravityCompat.START)
                return true
            }
            fun sendMessage(title : String) {
                var msg = Message()
                msg.obj = title
                msg.what = NavUIHandler.MSG_LAUNCH_APP_BY_APPNAME
                mNaviHandler.get()!!.sendMessage(msg)
            }
        }

        class InputTextWatcher : TextWatcher {
            private var mEditText: EditText ?= null
            private var mAdapter : WeakReference<BaseAdapter>
            private var mAppInfoManager : WeakReference<AppInfoManager>
            constructor(adapter : BaseAdapter, appInfoManager : AppInfoManager) {
               // mHandler = WeakReference(handler)
                mAdapter = WeakReference(adapter)
                mAppInfoManager = WeakReference(appInfoManager)
            }

            public fun setEditText(editText : EditText) {
                mEditText = editText
            }
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    mAppInfoManager.get()!!.queryAllApps()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.toString().isEmpty()) {
                    var adapter = mAdapter.get()!! as NaviAdapter
                    adapter.filter(s.toString())
                }
            }
        }

        class NaviListItemClickListener : AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
            PopupMenu.OnMenuItemClickListener{
            private var mHandler : WeakReference<Handler>
            private var mAdapter : WeakReference<BaseAdapter>
            private var mDrawerLayout : WeakReference<DrawerLayout>
            private var mLongPressPosition = 0
            constructor(handler : Handler, adapter : BaseAdapter, drawer: DrawerLayout) {
                mHandler = WeakReference(handler)
                mAdapter = WeakReference(adapter)
                mDrawerLayout = WeakReference(drawer)
            }
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mDrawerLayout.get()!!.closeDrawer(Gravity.START)
                var item = mAdapter.get()!!.getItem(position) as AppInfo
                sendMessage(item.getName())
            }

            fun sendMessage(title : String) {
                var msg = Message()
                msg.obj = title
                msg.what = NavUIHandler.MSG_LAUNCH_APP_BY_APPNAME
                mHandler.get()!!.sendMessage(msg)
            }

            override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
                mLongPressPosition = position
                var popMenu = PopupMenu(MainApplication.getMainApplicationContext(), view)
                popMenu.setOnMenuItemClickListener(this)
                popMenu.inflate(R.menu.menu_main)
                popMenu.show()
                return true
            }

            override fun onMenuItemClick(item: MenuItem?): Boolean {
                Log.i("jeffrey-dbg","menuitemclick = " +mLongPressPosition)
                var appInfo  = mAdapter.get()!!.getItem(mLongPressPosition) as AppInfo
                var intent = Intent(FavoriteAppsFragment.ADD_TO_FAVORITE)
                intent.putExtra("favorite", appInfo.getPackageName())
                LocalBroadcastManager.getInstance(MainApplication.getMainApplicationContext()).sendBroadcast(intent)
                return true
            }
        }


    }


}
