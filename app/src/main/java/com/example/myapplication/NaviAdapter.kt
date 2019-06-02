package com.example.myapplication

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

class NaviAdapter : BaseAdapter {

    var mAppInfoList : ArrayList<AppInfo> = ArrayList<AppInfo>()

    constructor() {

    }

    private class ViewHolder(view: View) {
        var mAppName : TextView = view.findViewById(R.id.appName)
        var mAppIcon : ImageView = view.findViewById(R.id.appicon)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater =
                MainApplication.getMainApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.navilist_layout, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.mAppName.text = mAppInfoList[position].getName()
        viewHolder.mAppIcon.setImageDrawable(mAppInfoList[position].getIcon())
        viewHolder.mAppName.setTextColor(Color.WHITE)
        return view as View
    }

    override fun getItem(i: Int): AppInfo {
        return mAppInfoList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return mAppInfoList.size
    }

    public fun updateData(list : ArrayList<AppInfo>) {
        mAppInfoList = list
    }

    fun filter(keyword: String) {
        var tempList = ArrayList(mAppInfoList)
        mAppInfoList.clear()
        if (keyword.length > 0) {
            for (i in 0..tempList.size - 1) {
                if (tempList.get(i).getName().toLowerCase().contains(keyword.toLowerCase())) {
                    mAppInfoList.add(tempList.get(i))
                }
            }
        }
        notifyDataSetChanged()
    }

}