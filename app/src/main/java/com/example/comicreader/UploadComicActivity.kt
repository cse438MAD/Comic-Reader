package com.example.comicreader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_upload_comic.*

class UploadComicActivity : AppCompatActivity() {

    private val requestCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_comic)
        b_add_chapter.setOnClickListener {
            if(validateForm()){
                val intent = Intent(this, AddChapterActivity::class.java)
                intent.putExtra("Title", title_field.text.toString())
                this.startActivityForResult(intent, requestCode)
            }
        }
    }

    public override fun onActivityResult(request_code: Int, resultCode: Int, data: Intent?) {
        if (request_code == requestCode) {
            if (resultCode == Activity.RESULT_OK) {

            }
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

}