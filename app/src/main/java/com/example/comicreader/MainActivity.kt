package com.example.comicreader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import android.widget.Toast
import com.example.comicreader.Adapter.MyComicAdapter
import com.example.comicreader.Adapter.MySliderAdapter
import com.example.comicreader.Common.Common
import com.example.comicreader.Interface.IBannerLoadDoneListener
import com.example.comicreader.Interface.IComicLoadDoneListener
import com.example.comicreader.Model.Comic
import com.example.comicreader.Service.PicassoImageLoadingService
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*
import ss.com.bannerslider.Slider

class MainActivity : AppCompatActivity(), IBannerLoadDoneListener, IComicLoadDoneListener {

    override fun onComicLoadDoneListener(comicList: List<Comic>) {
        alertDialog.dismiss()

        Common.comicList = comicList;
        recycler_comic.adapter = MyComicAdapter(baseContext,comicList)
        txt_comic.text = StringBuilder("NEW COMIC (")
            .append(comicList.size)
            .append(")")

        if(swipe_to_refresh.isRefreshing)
            swipe_to_refresh.isRefreshing = false



    }

    override fun onBannerLoadDoneListener(banners: List<String>) {

        slider.setAdapter(MySliderAdapter(banners))

    }

    //Database
    lateinit var banners_ref : DatabaseReference
    lateinit var comic_ref : DatabaseReference

    //Listener
    lateinit var iBannerLoadDoneListener: IBannerLoadDoneListener
    lateinit var iComicLoadDoneListener: IComicLoadDoneListener

    lateinit var alertDialog: android.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init listener
        iBannerLoadDoneListener = this
        iComicLoadDoneListener = this


        //Init dialog
        alertDialog = SpotsDialog.Builder().setContext(this@MainActivity)
            .setCancelable(false)
            .setMessage("Please wait...")
            .build()

        FirebaseApp.initializeApp(this)
        //Init DB
        banners_ref = FirebaseDatabase.getInstance().getReference("Banners")
        comic_ref = FirebaseDatabase.getInstance().getReference("Comic")

        //First, load banner and comic
        swipe_to_refresh.setColorSchemeResources(R.color.colorPrimary,R.color.colorPrimaryDark)
        swipe_to_refresh.setOnRefreshListener {
            loadBanners()
            loadComic()
        }
        swipe_to_refresh.post{
            loadBanners()
            loadComic()
        }
        Slider.init(PicassoImageLoadingService())

        recycler_comic.setHasFixedSize(true)
        recycler_comic.layoutManager = GridLayoutManager(this@MainActivity,2)

        btn_show_filter_search.setOnClickListener {
            startActivity(Intent(this@MainActivity,FilterSearchActivity::class.java))
        }


    }

    private fun loadComic() {

        alertDialog.show()

            comic_ref.addListenerForSingleValueEvent(object :ValueEventListener{

                var comic_load : MutableList<Comic> = ArrayList<Comic>()

                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@MainActivity, ""+p0.message, Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for(comicSnapShot in p0.children){
                        val comic = comicSnapShot.getValue(Comic::class.java)
                        comic_load.add(comic!!)
                    }
                    iComicLoadDoneListener.onComicLoadDoneListener(comic_load)
                }


            })
    }

    private fun loadBanners() {
        banners_ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity, ""+p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val banner_list = ArrayList<String>()
                for(banner in p0.children){
                    val image = banner.getValue(String::class.java)
                    banner_list.add(image!!)
                }
                iBannerLoadDoneListener.onBannerLoadDoneListener(banner_list)
            }

        })
    }
}
