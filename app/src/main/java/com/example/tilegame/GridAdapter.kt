package com.example.tilegame


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import com.example.tilegame.R

class GridAdapter(private val context: Context, private val dataList: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item_layout, parent, false)
            holder = ViewHolder()
            holder.button = view.findViewById(R.id.grid_item_button)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        holder.button.text = dataList[position]

        // Set click listener for the button
        holder.button.setOnClickListener {
            (context as GameActivity2).onTileClicked(it)
        }

        return view
    }

    private class ViewHolder {
        lateinit var button: Button
    }
}
