package com.noureddinetaleb.polyhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.noureddinetaleb.polyhome.R
import com.noureddinetaleb.polyhome.data.UsersWithAccessData

/**
 * Adapter for the users list
 * @param context: the context of the activity
 * @param dataSource: the list of users with access
 * @return the view of the users list
 */
class UsersAdapter(private val context: Context, private val dataSource: ArrayList<UsersWithAccessData>,private val onRemoveUser:(userLogin:String)->Unit ) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItem(position: Int): Any = dataSource[position]
    override fun getCount(): Int = dataSource.size

    /**
     * Update the users list
     * @param position: the position of the user to remove
     * @param convertView: the view of the user to remove
     * @param parent: the parent view of the user to remove
     * @return the view of the users list
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.users_list_item, parent, false)
        val users = dataSource[position]

        val btnRemove = rowView.findViewById<Button>(R.id.delButton)
        val userView = rowView.findViewById<TextView>(R.id.lblUser)

        if (users.owner == 1) {
            userView.text = users.userLogin + " (Propriétaire)"
            btnRemove.visibility = View.GONE
        } else {
            userView.text = users.userLogin
        }

        btnRemove.setOnClickListener {
            onRemoveUser(users.userLogin)
        }
        return rowView
    }
}