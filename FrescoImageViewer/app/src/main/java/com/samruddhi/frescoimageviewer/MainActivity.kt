package com.samruddhi.frescoimageviewer

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.view.SimpleDraweeView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val draweeView = findViewById<SimpleDraweeView>(R.id.img_fresco_full_image)
        draweeView.setImageURI(Uri.parse("https://cdn.pixabay.com/photo/2018/03/29/11/59/snow-3272072_960_720.jpg"))
    }
}