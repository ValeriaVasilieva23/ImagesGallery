package com.example.imagesgallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivityModelFactory(private val showError: () -> Unit) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel { showError() } as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
