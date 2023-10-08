package com.nibavlifts.firebaserealtimedatabase.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Note {
    private var id: String? = null
    private var noteName: String? = null

    constructor()
    constructor(id: String?, noteName: String?) {
        this.noteName = noteName
        this.id = id
    }

    fun getId(): String? {
        return id
    }

    fun getNoteName(): String? {
        return noteName
    }
}