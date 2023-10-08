package com.nibavlifts.firebaserealtimedatabase.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nibavlifts.firebaserealtimedatabase.model.Employee
import com.nibavlifts.firebaserealtimedatabase.view.adapter.EmployeeList
import com.nibavlifts.firebaserealtimedatabase.R

class MainActivity : AppCompatActivity() {
    private var editTextName: EditText? = null
    private var buttonAddEmployee: Button? = null
    var listViewEmployee: ListView? = null

    var employees: MutableList<Employee>? = null

    private var databaseEmployees: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseEmployees = FirebaseDatabase.getInstance().getReference("employees")

        editTextName = findViewById(R.id.editTextName)
        listViewEmployee = findViewById(R.id.listViewEmployee)
        buttonAddEmployee = findViewById(R.id.buttonAddEmployee)

        employees = ArrayList()

        buttonAddEmployee!!.setOnClickListener {
            addEmployee()
        }
        listViewEmployee!!.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val employee = (employees as ArrayList<Employee>)[i]

            val intent = Intent(applicationContext, EmployeeActivity::class.java)

            intent.putExtra(EMPLOYEE_ID, employee.getEmployeeId())
            intent.putExtra(EMPLOYEE_NAME, employee.getEmployeeName())

            startActivity(intent)
        }
        listViewEmployee!!.onItemLongClickListener =
            OnItemLongClickListener { adapterView, view, i, l ->
                val employee = (employees as ArrayList<Employee>)[i]
                showUpdateDeleteDialog(employee.getEmployeeId(), employee.getEmployeeName())
                true
            }
    }

    private fun addEmployee() {
        val name = editTextName!!.text.toString().trim { it <= ' ' }

        if (!TextUtils.isEmpty(name)) {
            val id = databaseEmployees!!.push().key
            val employee = Employee(id, name)
            databaseEmployees!!.child(id!!).setValue(employee)
            editTextName!!.setText("")
            Toast.makeText(this, "Employee added", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        databaseEmployees!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                employees!!.clear()

                for (postSnapshot in dataSnapshot.children) {
                    val employee = postSnapshot.getValue(Employee::class.java)
                    employees!!.add(employee!!)
                }

                val employeeAdapter = EmployeeList(this@MainActivity, employees!!)
                listViewEmployee!!.adapter = employeeAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun showUpdateDeleteDialog(employeeId: String?, employeeName: String?) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)
        val editTextName = dialogView.findViewById(R.id.editTextName) as EditText
        val buttonUpdate = dialogView.findViewById(R.id.buttonUpdateEmployee) as Button
        val buttonDelete = dialogView.findViewById(R.id.buttonDeleteEmployee) as Button
        dialogBuilder.setTitle(employeeName)
        val b: AlertDialog = dialogBuilder.create()
        b.show()

        editTextName.setText(employeeName)

        buttonUpdate.setOnClickListener {
            val name = editTextName.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(name)) {
                updateEmployee(employeeId, name)
                b.dismiss()
            }
        }
        buttonDelete.setOnClickListener {
            deleteEmployee(employeeId)
            b.dismiss()
        }
    }

    private fun updateEmployee(id: String?, name: String): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("employees").child(id!!)

        val employee = Employee(id, name)
        dR.setValue(employee)
        Toast.makeText(applicationContext, "Employee Updated", Toast.LENGTH_LONG).show()
        return true
    }

    private fun deleteEmployee(id: String?): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("employees").child(id!!)

        dR.removeValue()

        val drNotes = FirebaseDatabase.getInstance().getReference("notes").child(
            id
        )

        drNotes.removeValue()
        Toast.makeText(applicationContext, "Employee Deleted", Toast.LENGTH_LONG).show()
        return true
    }

    companion object {
        const val EMPLOYEE_NAME = "employeename"
        const val EMPLOYEE_ID = "employeeid"
    }
}