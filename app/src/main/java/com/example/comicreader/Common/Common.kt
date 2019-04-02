package com.example.comicreader.Common

import com.example.comicreader.Model.Chapter
import com.example.comicreader.Model.Comic
import java.lang.StringBuilder

object Common {
    fun formatString(name: String): String {
        val finalResult = StringBuilder(if (name.length > 15)name.substring(0,15)+"..." else name)
        return finalResult.toString()
    }

    var categories = arrayOf("Action", "Adult", "Adventure", "Comedy", "Completed", "Cooking", "Doujinshi", "Drama", "Drop", "Ecchi", "Fantasy", "Gender bender", "Harem", "Historical", "Horror", "Jose", "Latest", "Manhua", "Manhwa", "Material arts", "Mature", "Mecha", "Medical", "Mystery", "Newest", "One shot", "Ongoing", "Psychological", "Romance", "School life", "Sci fi", "Seinen", "Shoujo", "Shoujo a", "Shounen", "Shounen ai", "Slice of life", "Smut", "Sports", "Superhero", "Supernatural", "Top Read", "Tragedy", "Webtoons", "Yaoi", "Yuri")

    var comicList: List<Comic> = ArrayList<Comic>()
    var selected_comic: Comic? = null
    lateinit var chapterList: List<Chapter>
    lateinit var selected_chapter: Chapter
    var chapter_index: Int = -1
}