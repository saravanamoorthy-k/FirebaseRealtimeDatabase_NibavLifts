package com.nibavlifts.firebaserealtimedatabase.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nibavlifts.firebaserealtimedatabase.model.Note
import com.nibavlifts.firebaserealtimedatabase.view.adapter.NoteList
import com.nibavlifts.firebaserealtimedatabase.R

class EmployeeActivity : AppCompatActivity() {

    private var buttonAddNote: Button? = null
    private var editTextNote: EditText? = null
    private var textViewEmployee: TextView? = null
    var listViewNotes: ListView? = null
    private var databaseNotes: DatabaseReference? = null
    var notes: MutableList<Note>? = null
    var employeeId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        val intent: Intent = intent
        employeeId = intent.getStringExtra(MainActivity.EMPLOYEE_ID)!!
        databaseNotes = FirebaseDatabase.getInstance().getReference("notes").child(employeeId)
        buttonAddNote = findViewById(R.id.buttonAddNote)
        editTextNote = findViewById(R.id.editTextName)
        textViewEmployee = findViewById(R.id.textViewEmployee)
        listViewNotes = findViewById(R.id.listViewNotes)

        notes = ArrayList()

        textViewEmployee!!.text = intent.getStringExtra(MainActivity.EMPLOYEE_NAME)

        buttonAddNote!!.setOnClickListener { saveNote() }

        listViewNotes!!.onItemLongClickListener =
            OnItemLongClickListener { adapterView, view, i, l ->
                val note = (notes as ArrayList<Note>)[i]
                showUpdateDeleteDialog(note.getId(), note.getNoteName())
                true
            }
    }

    override fun onStart() {
        super.onStart()
        databaseNotes!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                notes!!.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val note: Note = postSnapshot.getValue(Note::class.java)!!
                    notes!!.add(note)
                }
                val noteListAdapter = NoteList(this@EmployeeActivity, notes!!)
                listViewNotes!!.adapter = noteListAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun saveNote() {
        val noteTemp = editTextNote!!.text.toString().trim { it <= ' ' }

        if (!TextUtils.isEmpty(noteTemp)) {
            val id: String = databaseNotes!!.push().key!!
            val note = Note(id, noteTemp)
            databaseNotes!!.child(id).setValue(note)
            Toast.makeText(this, "Note saved", Toast.LENGTH_LONG).show()
            editTextNote!!.setText("")
        } else {
            Toast.makeText(this, "Please enter Note", Toast.LENGTH_LONG).show()
        }
    }

    private fun showUpdateDeleteDialog(noteId: String?, noteName: String?) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)
        val editTextName = dialogView.findViewById(R.id.editTextName) as EditText
        val buttonUpdate = dialogView.findViewById(R.id.buttonUpdateEmployee) as Button
        val buttonDelete = dialogView.findViewById(R.id.buttonDeleteEmployee) as Button
        dialogBuilder.setTitle(noteName)
        val b: AlertDialog = dialogBuilder.create()
        b.show()

        editTextName.setText(noteName)

        buttonUpdate.setOnClickListener {
            val name = editTextName.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(name)) {
                updateNote(noteId, name)
                b.dismiss()
            }
        }
        buttonDelete.setOnClickListener {
            deleteNote(noteId)
            b.dismiss()
        }
    }

    private fun updateNote(id: String?, name: String): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("notes").child(employeeId).child(id!!)

        val note = Note(id, name)
        dR.setValue(note)
        Toast.makeText(applicationContext, "Notes Updated", Toast.LENGTH_LONG).show()
        return true
    }

    private fun deleteNote(id: String?): Boolean {
        val drNotes =
            FirebaseDatabase.getInstance().getReference("notes").child(employeeId).child(id!!)

        drNotes.removeValue()
        Toast.makeText(applicationContext, "Notes Deleted", Toast.LENGTH_LONG).show()
        return true
    }
}