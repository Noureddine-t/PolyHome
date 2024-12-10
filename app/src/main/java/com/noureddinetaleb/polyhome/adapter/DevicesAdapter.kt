package com.noureddinetaleb.polyhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.data.DevicesData

/**
 * Adapter for the devices list
 * @param context: the context of the activity
 * @param dataSource: the list of devices
 * @return the view of the devices list
 */
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

        val btnOpen = rowView.findViewById<Button>(R.id.openButton)
        val btnClose = rowView.findViewById<Button>(R.id.closeButton)
        val btnStop = rowView.findViewById<Button>(R.id.stopButton)
        val btnOn = rowView.findViewById<Button>(R.id.onButton)
        val btnOff = rowView.findViewById<Button>(R.id.offButton)


        /**
         * Set the buttons text and visibility according to the device type
         */
        when (devices.type.lowercase()) {
            "light" -> {
                btnOn.text = "On"
                btnOff.text = "Off"
                btnOpen.visibility = View.GONE
                btnClose.visibility = View.GONE
                btnStop.visibility = View.GONE
            }
            "sliding shutter", "garage door", "rolling shutter" -> {
                btnOpen.text = "Open"
                btnClose.text = "Close"
                btnStop.text = "Stop"
                btnOn.visibility = View.GONE
                btnOff.visibility = View.GONE
            }
        }


        return rowView
    }
    private fun manageLight(deviceId: String, action: String) {

    }

    private fun manageShutterAndGarage(deviceId: String, action: String) {

    }

}