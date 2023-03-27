package com.example.imagesgallery

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val getPhotosQuery = "$IMAGES_SERVER_URL/napi/photos?per_page=30&page="
    private lateinit var imagesFromJson: ArrayList<ImageFromInternet>
    private var imagesGalleryAdapter: ImagesGalleryAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var pageNum: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRetry: Button = findViewById(R.id.buttonRetry)
        val tvError: TextView = findViewById(R.id.tvError)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        gridLayoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)

        getImagesFromServer(this, recyclerView)

        btnRetry.setOnClickListener {
            tvError.visibility = View.GONE
            btnRetry.visibility = View.GONE
            getImagesFromServer(this, recyclerView)
        }

        supportActionBar?.hide()
    }

    private fun getImagesFromServer(context: Context, recyclerView: RecyclerView) {
        GlobalScope.launch(Dispatchers.IO) {
            val client = HttpClient()
            val data: String

            try {
                data = client.get(getPhotosQuery + pageNum++)
            } catch (e: Exception) {
                showError("Сервер недоступен", recyclerView)
                return@launch
            }
            val mapper = jacksonObjectMapper()

            try {
                imagesFromJson = mapper.readValue(data)
            } catch (e: Exception) {
                showError("Ответ от сервера не распознан", recyclerView)
                return@launch
            }

            if (imagesFromJson.size < 1) {
                return@launch //Долистали до конца
            }

            CoroutineScope(Dispatchers.Main).launch {
                recyclerView.visibility = View.VISIBLE
                if (imagesGalleryAdapter == null) {
                    imagesGalleryAdapter = ImagesGalleryAdapter(
                        context,
                        imagesFromJson,
                        onScrolledToEnd = { getImagesFromServer(context, recyclerView) })
                    recyclerView.adapter = imagesGalleryAdapter
                } else {
                    imagesGalleryAdapter!!.addImages(imagesFromJson)
                }
            }
        }
    }

    private fun showError(errorText: String, recyclerView: RecyclerView) {
        CoroutineScope(Dispatchers.Main).launch {
            val tvError: TextView = findViewById(R.id.tvError)
            val btnRetry: Button = findViewById(R.id.buttonRetry)
            tvError.setText(errorText)
            tvError.visibility = View.VISIBLE
            btnRetry.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    companion object {
        private const val IMAGES_SERVER_URL = "https://unsplash.com"
        private const val SPAN_COUNT: Int = 2
    }
}


