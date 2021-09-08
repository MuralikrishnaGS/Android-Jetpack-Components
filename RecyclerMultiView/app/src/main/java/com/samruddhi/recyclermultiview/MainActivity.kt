package com.samruddhi.recyclermultiview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.Toolbar)
        toolbar.title = getString(R.string.app_name)

        setUpGridRecyclerView()
    }

    private fun setUpGridRecyclerView() {
        val galleryItemsList = ArrayList<GalleryItem>()
        galleryItemsList.add(GalleryItem(R.drawable.p1, "big"))
        galleryItemsList.add(GalleryItem(R.drawable.p2, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p3, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p4, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p4, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p1, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p2, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p2, "big"))
        galleryItemsList.add(GalleryItem(R.drawable.p3, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p4, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p4, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p1, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p2, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p2, "small"))
        galleryItemsList.add(GalleryItem(R.drawable.p3, "big"))

        val galleryAdapter = GalleryAdapter(galleryItemsList, this)
        val galleryLayoutManager = GridLayoutManager(this, 2)
        galleryLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (galleryItemsList[position].size == "big") {
                    2
                } else {
                    1
                }
            }
        }

        val recyclerViewGallery = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerViewGallery.layoutManager = galleryLayoutManager
        recyclerViewGallery.adapter = galleryAdapter
    }
}