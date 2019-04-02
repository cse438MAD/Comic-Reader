package com.example.comicreader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.example.comicreader.Adapter.MyChapterAdapter
import com.example.comicreader.Common.Common
import com.example.comicreader.Model.Comic
import kotlinx.android.synthetic.main.activity_chapter.*
import kotlinx.android.synthetic.main.chapter_item.*
import java.lang.StringBuilder

class ChapterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)

        toolbar.title = Common.selected_comic!!.Name
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp)
        toolbar.setNavigationOnClickListener{
            finish() //Go back
        }

        recycler_chapter.setHasFixedSize(true)

        val layoutMAnager = LinearLayoutManager(this@ChapterActivity)
        recycler_chapter.layoutManager = layoutMAnager
        recycler_chapter.addItemDecoration(DividerItemDecoration(this,layoutMAnager.orientation))

        fetchChapter(Common.selected_comic!!)
    }

    private fun fetchChapter(comic: Comic) {
        Common.chapterList = comic.Chapters!!
        txt_chapter_name.text = StringBuilder("CHAPTER (")
            .append(comic.Chapters!!.size)
            .append(")")

        recycler_chapter.adapter = MyChapterAdapter(this,Common.chapterList)

    }
}
