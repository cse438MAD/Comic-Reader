package com.example.comicreader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.comicreader.Model.Chapter
import com.example.comicreader.Model.Comic
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_upload_comic.*
import kotlinx.android.synthetic.main.button_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList


class UploadComicActivity : AppCompatActivity() {

    private val requestCode: Int = 0
    private var chapterList: MutableList<String> = ArrayList()
    private var adapter = ButtonListAdapter()
    private var _comic = Comic()
    lateinit var comicRef: DatabaseReference
    lateinit var countRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_comic)

        comicRef = FirebaseDatabase.getInstance().getReference("Comic")
        countRef = FirebaseDatabase.getInstance().getReference("Count")

        cover.setOnClickListener {
            val intent = Intent(this@UploadComicActivity, AddChapterActivity::class.java)
            intent.putExtra("COVER", title_field.text.toString())
            this@UploadComicActivity.startActivityForResult(intent, requestCode)
        }
        button_done.setOnClickListener {
            writeNewComic()
            startActivity(Intent(this, FilterSearchActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()

        button_list.layoutManager = LinearLayoutManager(this)
        button_list.adapter = adapter
        if (chapterList.size == 0) {
            addNewChapterButton()
        }
    }

    private fun addNewChapterButton() {
        chapterList.add(getString(R.string.new_chapter))
        adapter.notifyDataSetChanged()
    }


    private fun writeNewComic() {
        countRef.addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@UploadComicActivity, "" + p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                var count = p0.value as Long
                ++count
                comicRef.child("$count").setValue(_comic)
                countRef.setValue(count)
            }
        })
    }

    public override fun onActivityResult(request_code: Int, resultCode: Int, data: Intent?) {
        if (request_code == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.extras!!.containsKey("CHAPTER")) {
                    val chapter = data.extras!!.getSerializable("CHAPTER") as Chapter
                    chapterList[chapterList.size - 1] = chapter.Name as String
                    Log.e("Upload1", _comic.Chapters.toString())
                    if (_comic.Chapters == null) {
                        _comic.Chapters = ArrayList()
                    }
                    _comic.Chapters!!.add(chapter)
                    addNewChapterButton()
                } else {
                    cover.text = getString(R.string.cover_added)
                    val chapter = data.extras!!.getSerializable("COVER") as Chapter
                    _comic.Image = chapter.Links?.get(0)
                }

            }
            Log.e("list", chapterList.toString())


        }

    }

    private fun validateForm(): Boolean {
        var valid = true

        val title = title_field.text.toString()
        val category = category_field.text.toString()
        if (TextUtils.isEmpty(title)) {
            title_field.error = "Required."
            valid = false
        } else {
            title_field.error = null
        }

        _comic.Name = title
        if (!TextUtils.isEmpty(category)) {
            _comic.Category = category
        }

        return valid
    }

    inner class ButtonListAdapter : RecyclerView.Adapter<ButtonListAdapter.ButtonListViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ButtonListViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.button_list_item, p0, false)
            return ButtonListViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ButtonListViewHolder, p1: Int) {
            if (chapterList[p1] == getString(R.string.new_chapter)) {
                p0.button.text = getString(R.string.new_chapter)
                p0.button.setOnClickListener {
                    if (validateForm()) {
                        val intent = Intent(this@UploadComicActivity, AddChapterActivity::class.java)
                        intent.putExtra("TITLE", title_field.text.toString())
                        this@UploadComicActivity.startActivityForResult(intent, requestCode)
                    }
                }
            } else {
                p0.button.text = chapterList[p1]
                p0.button.setOnClickListener {
                    val intent = Intent(this@UploadComicActivity, AddChapterActivity::class.java)
                    intent.putExtra("CHAPTER", _comic.Chapters!![p1])
                    intent.putExtra("TITLE", title_field.text.toString())
                    this@UploadComicActivity.startActivityForResult(intent, requestCode)
                }
            }
        }

        override fun getItemCount(): Int {
            return chapterList.size
        }

        inner class ButtonListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var button: Button = itemView.button
        }
    }

}
