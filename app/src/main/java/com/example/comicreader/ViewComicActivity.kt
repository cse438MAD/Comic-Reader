package com.example.comicreader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.comicreader.Adapter.MyViewPagerAdapter
import com.example.comicreader.Common.Common
import com.example.comicreader.Model.Chapter
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer
import kotlinx.android.synthetic.main.activity_view_comic.*

class ViewComicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_comic)

        back.setOnClickListener{
            if (Common.chapter_index == 0){

                //If user in first chapter but press back
                Toast.makeText(this@ViewComicActivity, "You are reading first chapter", Toast.LENGTH_SHORT).show()
            }else{
                Common.chapter_index--
                fetchLinks(Common.chapterList[Common.chapter_index])
            }
        }
        next.setOnClickListener{
            if (Common.chapter_index == Common.chapterList.size - 1){

                //If user in first chapter but press back
                Toast.makeText(this@ViewComicActivity, "You are reading last chapter", Toast.LENGTH_SHORT).show()
            }else{
                Common.chapter_index++
                fetchLinks(Common.chapterList[Common.chapter_index])
            }
        }

        fetchLinks(Common.selected_chapter!!)
    }

    private fun fetchLinks(chapter: Chapter) {

        if (chapter.Links != null){
            if (chapter.Links!!.size > 0){

                val adapter = MyViewPagerAdapter(baseContext, chapter.Links!!)
                view_pager.adapter = adapter
                txt_chapter_name_view_comic.text = Common.formatString(Common.selected_chapter!!.Name!!)

                //Create book flip anim
                val bookFlipPageTransformer = BookFlipPageTransformer()
                bookFlipPageTransformer.scaleAmountPercent = 10f
                view_pager.setPageTransformer(true, bookFlipPageTransformer)

            }else{
                Toast.makeText(this@ViewComicActivity, "No image here", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this@ViewComicActivity, "This is latest chapter from author", Toast.LENGTH_SHORT).show()
        }

    }
}
