package com.project.notesfree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.notesfree.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var firestore: FirebaseFirestore
    private val noteList = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        binding.txtGreet.text = "Welcome, ${currentUser?.displayName}"
        setupRecyclerView()
        fetchNotes(currentUser?.uid ?: "")

        binding.addNoteBtn.setOnClickListener {
            val intent = Intent(this, Note_Activity::class.java)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchNotes(currentUser?.uid ?: "")
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Close the app
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(noteList)
        binding.notesRecyclerView.apply{
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteAdapter
        }
    }

    private fun fetchNotes(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                noteList.clear()
                for (document in documents) {
                    val note = document.toObject(Note::class.java)
                    noteList.add(note)
                }
                noteAdapter.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch notes: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
    }
}