package com.example.comicreader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserUploadedComicActivity : AppCompatActivity() {

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_uploaded_comic)
    }
}