package com.pronaycoding.toletapp.data

import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

object FirebaseDatabaseProvider {
    val database: FirebaseDatabase by lazy {
        val url = FirebaseApp.getInstance().options.databaseUrl
        if (!url.isNullOrBlank()) {
            FirebaseDatabase.getInstance(url)
        } else {
            FirebaseDatabase.getInstance("https://toletapp-6eb8e-default-rtdb.firebaseio.com")
        }
    }
}
