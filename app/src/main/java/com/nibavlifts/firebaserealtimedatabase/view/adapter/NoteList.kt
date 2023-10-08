package com.nibavlifts.firebaserealtimedatabase.view.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.nibavlifts.firebaserealtimedatabase.model.Note
import com.nibavlifts.firebaserealtimedatabase.R

class NoteList(private val context: Activity, var notes: List<Note>) :
    ArrayAdapter<Note?>(context, R.layout.layout_employee_list, notes) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_employee_list, null, true)
        val textViewName = listViewItem.findViewById<View>(R.id.textViewName) as TextView
        val track = notes[position]
        textViewName.text = track.getNoteName()
        return listViewItem
    }
}