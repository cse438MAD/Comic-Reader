package com.example.comicreader

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_chapter.*
import kotlinx.android.synthetic.main.image_list_item.view.*
import java.io.IOException
import java.util.*


class AddChapterActivity : AppCompatActivity() {


    //Firebase
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var _title: String? = null
    private var _chapterTitle: String? = null
    private var imageList: MutableList<Uri> = ArrayList()
    private var adapter = ImageListAdapter()


    private val imageRequest = 71

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_add_chapter)

        _title = intent!!.extras!!.getString("Title")
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        fab.setOnClickListener { chooseImage() }
        button.setOnClickListener {
            for (image in imageList) {
                uploadImage(image)
            }
        }

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                val sourcePosition = p1.adapterPosition
                val targetPosition = p2.adapterPosition
                Collections.swap(imageList, sourcePosition, targetPosition)
                adapter.notifyItemMoved(sourcePosition, targetPosition)
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                // DO NOTHING
            }
        })
        touchHelper.attachToRecyclerView(image_list)
    }

    override fun onStart() {
        super.onStart()
        image_list.layoutManager = LinearLayoutManager(this)
        image_list.adapter = adapter
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), imageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequest && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            imageList.add(data.data!!)
            adapter.notifyDataSetChanged()
        }
    }

    private fun uploadImage(filePath: Uri) {

        validateForm()

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val ref = storageReference!!.child("$_title/$_chapterTitle/" + imageList.indexOf(filePath))
        ref.putFile(filePath)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
            }


    }

    private fun validateForm(): Boolean {
        var valid = true

        _chapterTitle = chapter_title.text.toString()
        if (TextUtils.isEmpty(title)) {
            chapter_title.error = "Required."
            valid = false
        } else {
            chapter_title.error = null
        }



        return valid
    }


    inner class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ImageListViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.image_list_item, p0, false)
            return ImageListViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ImageListViewHolder, p1: Int) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageList[p1])
                p0.pageImg.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun getItemCount(): Int {
            return imageList.size
        }

        inner class ImageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var pageImg: ImageView = itemView.page_img
        }
    }

}
