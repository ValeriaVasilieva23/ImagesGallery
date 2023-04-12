package com.example.imagesgallery

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var gridLayoutManager: GridLayoutManager? = null
    private var imagesGalleryAdapter: ImagesGalleryAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory =
            MainActivityModelFactory { textError -> showError(textError = textError) }
        val model: MainActivityViewModel =
            ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)

        val btnRetry: Button = findViewById(R.id.buttonRetry)
        val tvError: TextView = findViewById(R.id.tvError)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        gridLayoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)

        getImagesFromServer(model, this, recyclerView)

        btnRetry.setOnClickListener {
            tvError.visibility = View.GONE
            btnRetry.visibility = View.GONE
            getImagesFromServer(model, this, recyclerView)
        }

        supportActionBar?.hide()
    }

    private fun showError(textError: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val tvError: TextView = findViewById(R.id.tvError)
            val btnRetry: Button = findViewById(R.id.buttonRetry)
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            tvError.text = textError
            tvError.visibility = View.VISIBLE
            btnRetry.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    private fun getImagesFromServer(
        model: MainActivityViewModel,
        context: Context,
        recyclerView: RecyclerView
    ) {
        var data: ArrayList<ImageFromInternet>?
        GlobalScope.launch(Dispatchers.IO) {
            data = model.getImagesFromJson()

            if (data != null) {
                if (data!!.size < 1) {
                    return@launch//Долистали до конца
                }
                GlobalScope.launch(Dispatchers.Main) {
                    recyclerView.visibility = View.VISIBLE
                }
                if (imagesGalleryAdapter == null) {
                    imagesGalleryAdapter = ImagesGalleryAdapter(
                        context,
                        data!!,
                        onScrolledToEnd = { getImagesFromServer(model, context, recyclerView) })
                    GlobalScope.launch(Dispatchers.Main) {
                        recyclerView.adapter = imagesGalleryAdapter
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        imagesGalleryAdapter!!.addImages(data)
                    }
                }
            }
        }
    }

    companion object {
        private const val SPAN_COUNT: Int = 2
    }
}


