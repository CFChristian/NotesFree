package com.project.notesfree

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        //Autentikasi user
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.createSaveNote.setOnClickListener {
                saveNote()
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun saveNote() {
        val title = binding.edtTitleCreateNote.text.toString()
        val description = binding.edtDescriptionCreateNote.text.toString()

        if (title.isEmpty() && description.isEmpty()) {
            binding.edtTitleCreateNote.error = "Title and desciption is required"
            binding.edtTitleCreateNote.requestFocus()
            return
        }

        if (title.length > 20) {
            binding.edtTitleCreateNote.error = "Title is too long"
            binding.edtTitleCreateNote.requestFocus()
            return
        }

        val note = NoteData().apply {
            this.title = title
            this.content = description
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy")
            val timeFormat = java.text.SimpleDateFormat("HH:mm")
            this.time = timeFormat.format(Date())
            this.date = dateFormat.format(Date())
        }

        val currentUser = auth.currentUser!!

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("notes")
            .add(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}