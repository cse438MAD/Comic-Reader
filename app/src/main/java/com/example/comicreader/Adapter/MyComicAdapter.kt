package com.example.comicreader.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.comicreader.ChapterActivity
import com.example.comicreader.Common.Common
import com.example.comicreader.Interface.IRecyclerClick
import com.example.comicreader.Model.Comic
import com.example.comicreader.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comic_item.view.*
import java.security.AccessControlContext

class MyComicAdapter(internal var context: Context, internal var comicList: List<Comic>):RecyclerView.Adapter<MyComicAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.comic_item,p0,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return comicList.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        Picasso.get().load(comicList[p1].Image).into(p0.imageView)
        p0.textView.text = comicList[p1].Name

        p0.setClick(object :IRecyclerClick{
            override fun onClick(view: View, position: Int) {
                val intent = Intent(context,ChapterActivity::class.java )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Common.selected_comic = comicList[position]
            }

        })
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(p0: View?) {

            iRecyclerClick.onClick(p0!!,adapterPosition)

        }

        var imageView:ImageView
        var textView:TextView
        lateinit var iRecyclerClick: IRecyclerClick

        fun setClick(iRecyclerClick: IRecyclerClick){

            this.iRecyclerClick = iRecyclerClick
        }

        init{
            imageView = itemView.findViewById(R.id.comic_image) as ImageView
            textView = itemView.findViewById(R.id.comic_name) as TextView

            itemView.setOnClickListener(this)

        }
    }
}