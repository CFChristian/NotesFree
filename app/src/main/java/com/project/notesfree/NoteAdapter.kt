package com.project.notesfree

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val noteList: List<Note>) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.tv_title)
        val content : TextView = itemView.findViewById(R.id.tv_description)
        val date :TextView = itemView.findViewById(R.id.tv_date)
        val time : TextView = itemView.findViewById(R.id.tv_time)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_row, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount() = noteList.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.title.text = noteList[position].title
        holder.content.text = noteList[position].content
        holder.date.text = noteList[position].date
        holder.time.text = noteList[position].time
    }

}