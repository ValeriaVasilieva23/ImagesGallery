package com.example.imagesgallery

import androidx.lifecycle.LifecycleObserver

import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*


class MainActivityViewModel constructor(
    private val showError: (textError: String) -> Unit
) : ViewModel(), LifecycleObserver {
    private val getPhotosQuery = "${IMAGES_SERVER_URL}/napi/photos?per_page=30&page="
    private var imagesFromJson: ArrayList<ImageFromInternet>? = null

    private var pageNum: Int = 1

    suspend fun getImagesFromJson(): ArrayList<ImageFromInternet>? {
        if (imagesFromJson == null) {
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
        }
        return imagesFromJson
    }

    companion object {
        private const val IMAGES_SERVER_URL = "https://unsplash.com"
    }
}