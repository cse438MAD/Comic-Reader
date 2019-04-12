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
import kotlinx.android.synthetic.main.activity_upload_comic.*
import kotlinx.android.synthetic.main.button_list_item.view.*
import java.util.*

class UploadComicActivity : AppCompatActivity() {

    private val requestCode: Int = 0
    private var chapterList: MutableList<String> = ArrayList()
    private var adapter = ButtonListAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_comic)
        cover.setOnClickListener {
            val intent = Intent(this@UploadComicActivity, AddChapterActivity::class.java)
            intent.putExtra("Cover", title_field.text.toString())
            this@UploadComicActivity.startActivityForResult(intent, requestCode) }

    }

    private fun addNewChapterButton() {
        chapterList.add(getString(R.string.new_chapter))
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        button_list.layoutManager = LinearLayoutManager(this)
        button_list.adapter = adapter
        if(chapterList.size == 0){
            addNewChapterButton()
        }
    }

    public override fun onActivityResult(request_code: Int, resultCode: Int, data: Intent?) {
        if (request_code == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if(data!!.extras!!.containsKey("CHAPTER")){
                    val chapterTitle = data!!.extras!!.getString("CHAPTER") as String
                    Log.e("Upload Comic", chapterTitle)
                    chapterList[chapterList.size - 1] = chapterTitle
                    addNewChapterButton()
                } else{
                    cover.text = "Cover Added"
                }

            }
            Log.e("list", chapterList.toString())


        }

    }

    private fun validateForm(): Boolean {
        var valid = true

        val title = title_field.text.toString()
        if (TextUtils.isEmpty(title)) {
            title_field.error = "Required."
            valid = false
        } else {
            title_field.error = null
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
                        intent.putExtra("Title", title_field.text.toString())
                        this@UploadComicActivity.startActivityForResult(intent, requestCode)
                    }
                }
            } else {
                p0.button.text = chapterList[p1]
                p0.button.setOnClickListener {
                    val intent = Intent(this@UploadComicActivity, AddChapterActivity::class.java)
                    intent.putExtra("Title", title_field.text.toString())
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