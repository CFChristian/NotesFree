package com.project.notesfree

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteData (
    var title: String = "",
    var content: String = "",
    var date: String = "",
    var time: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable