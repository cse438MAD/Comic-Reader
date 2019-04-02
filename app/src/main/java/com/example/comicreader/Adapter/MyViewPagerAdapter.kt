package com.example.comicreader.Adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.comicreader.R
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_comic.view.*

class MyViewPagerAdapter(internal var context: Context,
                         internal var linkList: List<String>):PagerAdapter() {

    internal var inflater:LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }


    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return linkList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView( `object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image_layout = inflater.inflate(R.layout.view_pager_item, container, false)

        val page_image = image_layout.findViewById(R.id.page_image) as PhotoView

        Picasso.get().load(linkList[position]).into(page_image)

        container.addView(image_layout)
        return image_layout
    }
}