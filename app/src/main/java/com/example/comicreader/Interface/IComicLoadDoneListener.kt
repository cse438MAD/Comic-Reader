package com.example.comicreader.Interface

import com.example.comicreader.Model.Comic

interface IComicLoadDoneListener {
    fun onComicLoadDoneListener(comicList: List<Comic>)
}