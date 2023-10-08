package com.nibavlifts.firebaserealtimedatabase.view.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.nibavlifts.firebaserealtimedatabase.model.Employee
import com.nibavlifts.firebaserealtimedatabase.R

class EmployeeList(private val context: Activity, var employees: List<Employee>) :
    ArrayAdapter<Employee?>(context, R.layout.layout_employee_list, employees) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_employee_list, null, true)
        val textViewName = listViewItem.findViewById(R.id.textViewName) as TextView
        val artist = employees[position]
        textViewName.text = artist.getEmployeeName()
        return listViewItem
    }
}