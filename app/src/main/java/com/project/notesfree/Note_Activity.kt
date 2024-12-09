package com.project.notesfree

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.notesfree.databinding.ActivityNoteBinding
import java.util.Date

class Note_Activity : AppCompatActivity() {

    //Deklarasi variabel
    private lateinit var binding: ActivityNoteBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private var docId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        //Autentikasi user
        docId = intent.getStringExtra("docId")
        if (docId != null) {
            binding.edtTitleCreateNote.setText(intent.getStringExtra("title"))
            binding.edtDescriptionCreateNote.setText(intent.getStringExtra("description"))
            binding.noteTitle.text = "Edit Your Note"
        }

        //Menyimpan data note
        binding.createSaveNote.setOnClickListener {
            saveNote()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    //Fungsi untuk menyimpan data note
    private fun saveNote() {
        val title = binding.edtTitleCreateNote.text.toString()
        val description = binding.edtDescriptionCreateNote.text.toString()

        if (title.isEmpty()) {
            binding.edtTitleCreateNote.error = "Title is required"
            binding.edtTitleCreateNote.requestFocus()
            return
        }

        if (description.isEmpty()) {
            binding.edtDescriptionCreateNote.error = "Description is required"
            binding.edtDescriptionCreateNote.requestFocus()
            return
        }

        if (title.length > 20) {
            binding.edtTitleCreateNote.error = "Title is too long"
            binding.edtTitleCreateNote.requestFocus()
            return
        }

        val note = mapOf(
            "title" to title,
            "content" to description,
            "date" to java.text.SimpleDateFormat("dd/MM/yyyy").format(Date()),
            "time" to java.text.SimpleDateFormat("HH:mm").format(Date()),
            "timestamp" to System.currentTimeMillis()
        )

        val currentUser = auth.currentUser!!

        if(docId == null) {
            firestore.collection("users")
                .document(currentUser.uid)
                .collection("notes")
                .add(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            firestore.collection("users")
                .document(currentUser.uid)
                .collection("notes")
                .document(docId!!)
                .update(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}