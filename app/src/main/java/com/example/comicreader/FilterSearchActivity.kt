package com.example.comicreader

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.comicreader.Adapter.MyComicAdapter
import com.example.comicreader.Common.Common
import com.example.comicreader.Model.Comic
import kotlinx.android.synthetic.main.activity_filter_search.*
import kotlinx.android.synthetic.main.dialog_filter.*
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class FilterSearchActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_search)

        bottom_app_bar.inflateMenu(R.menu.main_menu)
        bottom_app_bar.setOnNavigationItemSelectedListener {menuItem ->

            when(menuItem.itemId){

                R.id.action_filter -> showOptionsDialog()
                R.id.action_search -> showSearchDialog()
            }
            true

        }

        recycler_filter_search.setHasFixedSize(true)
        recycler_filter_search.layoutManager = GridLayoutManager(this,2)

    }

    private fun showSearchDialog() {
        val alertDialog = AlertDialog.Builder(this@FilterSearchActivity)
        alertDialog.setTitle("Select Category")

        val inflater = this.layoutInflater
        val search_layout = inflater.inflate(R.layout.dialog_search,null)

        val edt_search = search_layout.findViewById<View>(R.id.edt_search) as EditText

        alertDialog.setView(search_layout)
        alertDialog.setNegativeButton("CANCEL",{dialogInterface, i -> dialogInterface.dismiss()})
        alertDialog.setPositiveButton("SEARCH",{dialogInterface, i ->
            fetchSearchComic(edt_search.text.toString())

        } )

        alertDialog.show()


    }

    private fun fetchSearchComic(search: String) {

        val comic_searched = ArrayList<Comic>()
        for (comic in Common.comicList){
            if (comic.Name != null){
                if (comic.Name!!.contains(search))
                    comic_searched.add(comic)

            }
        }
        if (comic_searched.size > 0){
            recycler_filter_search.adapter = MyComicAdapter(this@FilterSearchActivity,comic_searched)
        }else{
            Toast.makeText(this@FilterSearchActivity, "No result", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showOptionsDialog() {

        val alertDialog = AlertDialog.Builder(this@FilterSearchActivity)
        alertDialog.setTitle("Select Category")

        val inflater = this.layoutInflater
        val filter_layout = inflater.inflate(R.layout.dialog_filter,null)

        val chipGroup = filter_layout.findViewById<View>(R.id.chipGroup) as ChipGroup

        val autoCompleteTextView = filter_layout.findViewById<View>(R.id.edt_category) as AutoCompleteTextView
        autoCompleteTextView.threshold = 3
        autoCompleteTextView.setAdapter(ArrayAdapter(this, android.R.layout.select_dialog_item, Common.categories))

        autoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i ,l ->

            //Clear text
            autoCompleteTextView.setText("")

            //Add chip
            val chip = inflater.inflate(R.layout.chip_item, null, false) as Chip
            chip.text = (view as TextView).text
            chip.setOnCloseIconClickListener{ view -> chipGroup.removeView(view) }
            chipGroup.addView(chip)

        }

        alertDialog.setView(filter_layout)
        alertDialog.setNegativeButton("CANCEL",{dialogInterface, i -> dialogInterface.dismiss()})
        alertDialog.setPositiveButton("FILTER",{dialogInterface, i ->

            val filter_key = ArrayList<String>()
            val filter_query = StringBuilder("")
            for(j in 0 until chipGroup.childCount){
                val chip = chipGroup.getChildAt(j) as Chip
                filter_key.add(chip.text.toString())
            }
            //After get all category key, just sort it
            Collections.sort(filter_key)

            //Convert to string
            for(key in filter_key)
                filter_query.append(key).append(",")

            //Remove last ","
            filter_query.setLength(filter_query.length-1)

            //Get all comic with category search
            FetchFilterCategory(filter_query.toString())
        })

        alertDialog.show()
    }

    private fun FetchFilterCategory(query: String) {

        val comic_filtered = ArrayList<Comic>()
        for(comic in Common.comicList){
            if (comic.Category != null){
                if (comic.Category!!.contains(query)){
                    comic_filtered.add(comic)
                }
            }
        }
        if (comic_filtered.size > 0){
            recycler_filter_search.adapter = MyComicAdapter(this@FilterSearchActivity,comic_filtered)
        }else{
            Toast.makeText(this@FilterSearchActivity, "No result", Toast.LENGTH_SHORT).show()
        }

    }
}
