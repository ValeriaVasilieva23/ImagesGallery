package com.example.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageFromInternet(
    val urls: ImageUrls
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageUrls(
    val full: String,
    val thumb: String
)