package com.example.domain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class DetailedImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val imageUrl = intent.getStringExtra("imageUrl")
        val fullscreenView = findViewById<PhotoView>(R.id.fullscreenView)
        Glide.with(fullscreenView).load(imageUrl).placeholder(R.drawable.loading)
            .error(R.drawable.error)
            .into(fullscreenView)
    }
}