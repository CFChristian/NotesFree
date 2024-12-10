package com.project.notesfree
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

open class NoteAdapter(options: FirestoreRecyclerOptions<NoteData>) : FirestoreRecyclerAdapter<NoteData, NoteAdapter.NoteViewHolder>(options) {
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val content: TextView = itemView.findViewById(R.id.tv_description)
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val time: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_row, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: NoteData) {
        holder.title.text = model.title
        holder.content.text = model.content
        holder.date.text = model.date
        holder.time.text = model.time

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, Note_Activity::class.java)
            intent.putExtra("title", model.title)
            intent.putExtra("description", model.content)
            val docId = snapshots.getSnapshot(position).id
            intent.putExtra("docId", docId)
            context.startActivity(intent)
        }
    }

    // Function to synchronize data changes in the recylcer view
    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
    }
}