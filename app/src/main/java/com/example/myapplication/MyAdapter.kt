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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.my_text_view.view.*
import java.util.*
import android.graphics.drawable.GradientDrawable



class MyAdapter  :
    RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    public class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var appIcon: ImageView = view.findViewById(R.id.image_button)
        var appName: TextView = view.findViewById(R.id.item_title)
    }


    private var mAppInfoList : ArrayList<AppInfo> = ArrayList<AppInfo>()
    private var item : ItemClick ?= null;

    constructor(appInfoList : ArrayList<AppInfo>) {
        mAppInfoList = appInfoList
    }
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {

        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_text_view, parent, false)
        // set the view's size, margins, paddings and layout parameters
        view.setOnClickListener(onClick())
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //val color = Color.argb(255,  Random().nextInt(256),  Random().nextInt(256),  Random().nextInt(256))
        holder.appName.text = mAppInfoList.get(position).getName()
        holder.appIcon.setImageDrawable(mAppInfoList.get(position).getIcon())
        holder.view.tag = position
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = mAppInfoList.size

    fun updateData(list : ArrayList<AppInfo>) {
        mAppInfoList = list
    }

    inner class onClick : View.OnClickListener{
        override fun onClick(v: View?) {
            var positon:Int= v!!.getTag() as Int
            item!!.OnItemClick(v,positon)
        }

    }
    fun setClickListener(item : ItemClick) {
        this.item = item
    }
}