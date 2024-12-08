package com.noureddinetaleb.polyhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.data.UsersWithAccessData

class UsersAdapter (private val context: Context, private val dataSource: ArrayList<UsersWithAccessData>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): Any = dataSource[position]
    override fun getCount(): Int = dataSource.size

    private var userActionListener: OnUserActionListener? = null
    interface OnUserActionListener {
        fun onRemoveUser(userLogin: String)
    }
    fun setUserActionListener(listener: OnUserActionListener) {
        this.userActionListener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.users_list_item, parent, false)
        val users = dataSource[position]

        val btnRemove = rowView.findViewById<Button>(R.id.delButton)
        val userView = rowView.findViewById<TextView>(R.id.lblUser)

        if (users.owner == 1) {
            userView.text = users.userLogin + " (Propriétaire)"
            btnRemove.isEnabled = false
        } else {
            userView.text = users.userLogin
            btnRemove.isEnabled = true
        }

        btnRemove.setOnClickListener {
                userActionListener?.onRemoveUser(users.userLogin)
        }
        return rowView
    }
}