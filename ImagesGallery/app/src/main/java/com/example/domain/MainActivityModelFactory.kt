package com.example.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivityModelFactory(private val showError: (textMessage: String) -> Unit) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel { textMessage -> showError(textMessage) } as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
