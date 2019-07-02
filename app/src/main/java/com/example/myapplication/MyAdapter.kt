package com.example.myapplication

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates
import kotlinx.android.synthetic.main.fav_app_layout.view.*
import java.util.*
import android.graphics.drawable.GradientDrawable
import android.widget.*
import java.lang.ref.WeakReference


class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    public class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var appIcon: ImageView = view.findViewById(R.id.image_button)
        var appName: TextView = view.findViewById(R.id.item_title)
        var outerLayout : LinearLayout = view.findViewById(R.id.outer_layout)
        var interLayout : LinearLayout = view.findViewById(R.id.linearlayout)
    }


    private var mAppInfoList : ArrayList<AppInfo> = ArrayList<AppInfo>()
    private lateinit var mItemClick : ItemClick

    constructor(appInfoList : ArrayList<AppInfo>) {
        mAppInfoList = appInfoList
    }
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {

        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fav_app_layout, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //val color = Color.argb(255,  Random().nextInt(256),  Random().nextInt(256),  Random().nextInt(256))
        holder.appIcon.setImageDrawable(mAppInfoList[position].getIcon())
        holder.appName.text = mAppInfoList[position].getName()
        holder.appIcon.tag = position
        holder.appName.tag = position
        holder.outerLayout.tag = position

        if (mItemClick != null) {
            var clickListener = onClick(mItemClick)
            if (clickListener != null) {
                holder.outerLayout.setOnClickListener(clickListener)
                holder.outerLayout.setOnLongClickListener(clickListener)
                holder.appIcon.setOnClickListener(clickListener)
                holder.appName.setOnClickListener(clickListener)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = mAppInfoList.size

    fun updateData(list : ArrayList<AppInfo>) {
        var appInfoList =  ArrayList<AppInfo>()
        appInfoList.addAll(list)
        mAppInfoList = appInfoList
    }

    fun setClickListener(item : ItemClick) {
        mItemClick = item
    }

    companion object {
        class onClick : View.OnClickListener, View.OnLongClickListener {
            private var mItem: WeakReference<ItemClick>
            constructor(item: ItemClick) {
                mItem = WeakReference(item)
            }
            override fun onClick(v: View) {
                var position = v.tag as Int
                mItem.get()!!.OnItemClick(v, position)
            }

            override fun onLongClick(v: View): Boolean {
                Log.i("jeffrey-dbg","longClick")
                var position = v.tag as Int
                mItem.get()!!.OnItemLongClick(v, position)
                return true
            }
        }
    }

}