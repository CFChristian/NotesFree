package com.project.notesfree

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.notesfree.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var deleteIcon: Drawable
    private lateinit var background: ColorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        binding.txtGreet.text = "Welcome, ${currentUser?.displayName}"
        setupRecyclerView(currentUser?.uid ?: "")

        binding.addNoteBtn.setOnClickListener {
            val intent = Intent(this, Note_Activity::class.java)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            noteAdapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerView)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Close the app
    }

    private fun setupRecyclerView(userId: String) {
        val query = firestore.collection("users")
            .document(userId)
            .collection("notes")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<NoteData>()
            .setQuery(query, NoteData::class.java)
            .build()

        noteAdapter = NoteAdapter(options)
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteAdapter
        }
    }
    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val docId = noteAdapter.snapshots.getSnapshot(position).id
            firestore.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("notes")
                .document(docId)
                .delete()
                .addOnCompleteListener{
                    Log.d("MainActivity", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w("MainActivity", "Error deleting document", e)
                }
        }
    }
}