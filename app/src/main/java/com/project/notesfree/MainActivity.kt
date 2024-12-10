package com.project.notesfree
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
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
    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        setAppTheme(isDarkMode)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        binding.searchView.clearFocus()

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

        setupSearchView()
        setupPopupMenu()
    }

    // Function to setup the search view
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!TextUtils.isEmpty(newText)) {
                    searchNotes(newText!!)
                } else {
                    resetRecyclerView()
                }
                return true
            }
        })
    }

    // Function to setup the popup menu
    private fun setupPopupMenu() {
        binding.dropdownMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

            val darkModeMenuItem = popupMenu.menu.findItem(R.id.dark_mode)
            darkModeMenuItem.title = if (isDarkMode) "Light Mode" else "Dark Mode"

            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.action_logout -> {
                        auth.signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.dark_mode -> {
                        isDarkMode = !isDarkMode
                        setAppTheme(isDarkMode)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    // Function to set the app theme
    private fun setAppTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        with(sharedPreferences.edit()) {
            putBoolean("isDarkMode", isDarkMode)
            apply()
        }
    }

    // Function to search notes
    private fun searchNotes(query: String) {
        val searchQuery = query.toLowerCase()
        Log.d("MainActivity", "Search query: $searchQuery")
        val firestoreQuery = firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("notes")
            .orderBy("title_lowercase")
            .startAt(searchQuery)
            .endAt(searchQuery + "\uf8ff")

        val options = FirestoreRecyclerOptions.Builder<NoteData>()
            .setQuery(firestoreQuery, NoteData::class.java)
            .build()

        noteAdapter.updateOptions(options)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Close the app
    }

    // Function to setup the recycler view
    private fun setupRecyclerView(userId: String) {
        val query = firestore.collection("users")
            .document(userId)
            .collection("notes")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<NoteData>()
            .setQuery(query, NoteData::class.java)
            .build()

        noteAdapter = object : NoteAdapter(options) {
            override fun onDataChanged() {
                super.onDataChanged()
                Log.d("MainActivity", "onDataChanged called. Item count: $itemCount")
                if (itemCount == 0) {
                    binding.noNotesText.visibility = android.view.View.VISIBLE
                } else {
                    binding.noNotesText.visibility = android.view.View.GONE
                }
            }
        }

        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = noteAdapter
        }
    }

    // Function to start and stop listening to the adapter
    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }


    // Function to reset the recycler view
    private fun resetRecyclerView() {
        val userId = auth.currentUser?.uid ?: return
        val query = firestore.collection("users")
            .document(userId)
            .collection("notes")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<NoteData>()
            .setQuery(query, NoteData::class.java)
            .build()

        noteAdapter.updateOptions(options)
        noteAdapter.notifyDataSetChanged()
    }



    // Function to delete a note
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