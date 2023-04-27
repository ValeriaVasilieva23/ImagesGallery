package com.example.domain

import android.content.Context
import androidx.lifecycle.LifecycleObserver

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivityViewModel constructor(
    private val showError: (textError: String) -> Unit
) : ViewModel(), LifecycleObserver {
    private val getPhotosQuery = "${IMAGES_SERVER_URL}/napi/photos?per_page=30&page="

    //private var imagesFromJson: ArrayList<ImageFromInternet>? = null
    private var imagesGalleryAdapter: ImagesGalleryAdapter? = null

    private var pageNum: Int = 1

    suspend fun getImagesFromJson(): ArrayList<ImageFromInternet>? {
        var imagesFromJson: ArrayList<ImageFromInternet>?
        //if (imagesFromJson == null) {
        val client = HttpClient()
        val text: String
        try {
            text = client.get(getPhotosQuery + pageNum++)
        } catch (e: Exception) {
            showError("Сервер недоступен")
            return null
        }
        val mapper = jacksonObjectMapper()
        try {
            imagesFromJson = mapper.readValue(text)
        } catch (e: Exception) {
            showError("Ответ от сервера не распознан")
            return null
        }
        //}
        return imagesFromJson
    }

    fun getAdapter(
        context: Context,
        data: ArrayList<ImageFromInternet>?,
        recyclerView: RecyclerView,
        onScrollToEnd1: Boolean,
        onScrollToEnd: () -> Unit,
    ) {
        if (imagesGalleryAdapter == null) {
            imagesGalleryAdapter = ImagesGalleryAdapter(
                context,
                data!!
            ) { onScrollToEnd() }
            GlobalScope.launch(Dispatchers.Main) {
                recyclerView.adapter = imagesGalleryAdapter
            }
        } else {
            if (onScrollToEnd1) {
                GlobalScope.launch(Dispatchers.Main) {
                    imagesGalleryAdapter!!.addImages(data)
                }

            } else {
                imagesGalleryAdapter!!.setContext1(context)
                GlobalScope.launch(Dispatchers.Main) {
                    recyclerView.adapter = imagesGalleryAdapter
                }
            }
        }
    }

    companion object {
        private const val IMAGES_SERVER_URL = "https://unsplash.com"
    }
}