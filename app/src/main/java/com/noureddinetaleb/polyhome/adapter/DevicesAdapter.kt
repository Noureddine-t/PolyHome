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
 * Adapter for displaying the devices list with the possibility to send commands to the them.
 *
 * @param context The context of the activity.
 * @param dataSource The list of devices to display.
 * @return The view of the devices list with the possibility to send commands to the them.
 */
class DevicesAdapter(private val context: Context, private val dataSource: ArrayList<DevicesData>, private val onCommand: (deviceId: String, command: String) -> Unit) : BaseAdapter() {
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

        updateButtonsState(devices, btnOn, btnOff, btnOpen, btnClose, btnStop)

        btnOn.setOnClickListener { onCommand(devices.id, "TURN ON") }
        btnOff.setOnClickListener { onCommand(devices.id, "TURN OFF") }
        btnOpen.setOnClickListener { onCommand(devices.id, "OPEN") }
        btnClose.setOnClickListener { onCommand(devices.id, "CLOSE") }
        btnStop.setOnClickListener { onCommand(devices.id, "STOP") }
        return rowView
    }

    /**
     * Update the buttons state.
     *
     * Show the buttons according to the device type (on/off for light or open/close/stop for shutter and garage door).
     *
     * Enable or disable the buttons according to the device state.
     *
     * @param devices List of the devices at the house.
     * @param btnOn The button to turn on the light.
     * @param btnOff The button to turn off the light.
     * @param btnOpen The button to open the shutter or the garage door.
     * @param btnClose The button to close the shutter or the garage door.
     * @param btnStop The button to stop the shutter or the garage door.
     */
    private fun updateButtonsState(devices: DevicesData, btnOn: Button, btnOff: Button, btnOpen: Button, btnClose: Button, btnStop: Button) {
        when (devices.type) {
            "light" -> {
                btnOn.text = "On"
                btnOff.text = "Off"
                btnOpen.visibility = View.GONE
                btnClose.visibility = View.GONE
                btnStop.visibility = View.GONE

//                if (devices.power == 0) {
//                    btnOff.isEnabled = false
//                    btnOff.setBackgroundColor(Color.WHITE)
//                    btnOn.isEnabled = true
//
//                } else {
//                    btnOn.isEnabled = false
//                    btnOn.setBackgroundColor(Color.WHITE)
//                    btnOff.isEnabled = true
//                }
            }

            "sliding shutter", "garage door", "rolling shutter" -> {
                btnOpen.text = "Open"
                btnClose.text = "Close"
                btnStop.text = "Stop"
                btnOn.visibility = View.GONE
                btnOff.visibility = View.GONE

//                when (devices.opening) {
//                    0 -> {
//                        btnClose.isEnabled = false
//                        btnClose.setBackgroundColor(Color.WHITE)
//                    }
//                    1 -> {
//                        btnOpen.isEnabled = false
//                        btnOpen.setBackgroundColor(Color.WHITE)
//                    }
//                    in 1..99 -> {
//                        btnStop.isEnabled = false
//                        btnStop.setBackgroundColor(Color.WHITE)
//                    }
//                }
//
//                when (devices.openingMode) {
//                    0 -> {
//                        btnOpen.isEnabled = false
//                        btnOpen.setBackgroundColor(Color.WHITE)
//                    }
//                    1 -> {
//                        btnClose.isEnabled = false
//                        btnClose.setBackgroundColor(Color.WHITE)
//                    }
//                    2 -> {
//                        btnStop.isEnabled = false
//                        btnStop.setBackgroundColor(Color.WHITE)
//                    }
//                }
            }
        }
    }

}