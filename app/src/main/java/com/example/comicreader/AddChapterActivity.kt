package com.example.comicreader

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.comicreader.Model.Chapter
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_chapter.*
import kotlinx.android.synthetic.main.image_list_item.view.*
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class AddChapterActivity : AppCompatActivity(), View.OnClickListener {


    //Firebase
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.reference
    private var _title: String? = null
    private var _chapterTitle: String? = null
    private var imageList: MutableList<Uri> = ArrayList()
    private var adapter = ImageListAdapter()
    private var upload = false
    private var cover = false
    private lateinit var _chapter: Chapter


    private val imageRequest = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        setContentView(R.layout.activity_add_chapter)

        _chapter = Chapter()
        Log.e("ADD", "OnCreate")
        when {
            intent!!.extras!!.containsKey("CHAPTER") -> {
                _title = intent!!.extras!!.getString("TITLE")

                _chapter = intent!!.extras!!.getSerializable("CHAPTER") as Chapter
                for (string in _chapter.Links!!) {
                    imageList.add(Uri.parse(string))
                    adapter.notifyDataSetChanged()
                }
                Log.e("ImageList", imageList[0].toString())

            }
            intent!!.extras!!.containsKey("COVER") -> {
                _title = intent!!.extras!!.getString("COVER")
                chapter_title.visibility = View.INVISIBLE
                cover = true
            }
            else -> {
                _title = intent!!.extras!!.getString("TITLE")
            }
        }



        fab.setOnClickListener(this)
        button_done.setOnClickListener(this)
        button_upload.setOnClickListener(this)

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                val sourcePosition = p1.adapterPosition
                val targetPosition = p2.adapterPosition
                Collections.swap(imageList, sourcePosition, targetPosition)
                adapter.notifyItemMoved(sourcePosition, targetPosition)
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                imageList.removeAt(p0.adapterPosition)
                adapter.notifyItemRemoved(p0.adapterPosition)
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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), imageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequest && resultCode == Activity.RESULT_OK
        ) {

            if(data!!.clipData != null) {
                val clipData = data.clipData!!
                for (i in 0 until clipData.itemCount) {
                    imageList.add(clipData.getItemAt(i).uri)
                    adapter.notifyDataSetChanged()
                }
            } else{
                imageList.add(data.data!!)
                adapter.notifyDataSetChanged()

            }
        }
    }


    private fun uploadImage(filePath: Uri) {

        validateForm()
        upload = true

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val ref = storageReference.child("$_title/$_chapterTitle/" + imageList.indexOf(filePath))

        val uploadTask: StorageTask<UploadTask.TaskSnapshot> = ref.putFile(filePath)
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

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                if(_chapter.Links == null){
                    _chapter.Links = ArrayList()
                }
                _chapter.Links!!.add(downloadUri.toString())
            } else {
                // Handle failures
                // ...
            }
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_done -> {
                val intent = Intent(this, UploadComicActivity::class.java)
                if (upload) {
                    if (cover) {
                        intent.putExtra("COVER", _chapter)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    _chapter.Name = chapter_title.text.toString()
                    intent.putExtra("CHAPTER", _chapter)
                    Log.e("Add Chapter", _chapterTitle)
                    setResult(RESULT_OK, intent)
                    finish()
                }

                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
            R.id.fab -> chooseImage()
            R.id.button_upload -> {
                for (image in imageList) {
                    uploadImage(image)
                }
            }
        }
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
                p0.pageImg.setImageBitmap(getImageBitmap(imageList[p1].toString()))
                Log.e("Catch", "Whoops")
            }
        }

        override fun getItemCount(): Int {
            return imageList.size
        }

        inner class ImageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var pageImg: ImageView = itemView.page_img
        }
    }

    private fun getImageBitmap(url: String): Bitmap {
        var bm: Bitmap? = null
        try {
            val aURL = URL(url)
            val conn = aURL.openConnection()
            conn.connect()
            val inputStr = conn.getInputStream()
            val bufInputStr = BufferedInputStream(inputStr)
            bm = BitmapFactory.decodeStream(bufInputStr)
            bufInputStr.close()
            inputStr.close()
        } catch (e: IOException) {
            Log.e("GET BM", "Error getting bitmap", e)
        }
        return bm as Bitmap
    }

}
