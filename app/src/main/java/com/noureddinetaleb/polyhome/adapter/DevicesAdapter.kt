package com.noureddinetaleb.polyhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.data.DevicesData

class DevicesAdapter(private val context: Context, private val dataSource: ArrayList<DevicesData>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): Any = dataSource[position]
    override fun getCount(): Int = dataSource.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.devices_list_item, parent, false)
        val devices = dataSource[position]

        val deviceView = rowView.findViewById<TextView>(R.id.deviceName)

        deviceView.text = devices.id

        return rowView
    }
}