package com.example.matchyourmunch.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchyourmunch.model.ListRepository
import com.example.matchyourmunch.viewmodel.ListViewModel

/**
 * Factory zur Erzeugung eines ListViewModels
 * @param repository das Repository für den Zugriff auf die Listen
 */
class ListViewModelFactory(private val repository: ListRepository) : ViewModelProvider.Factory {
    /**
     * erstellt ein neues ListViewModel
     * @param modelClass die Klasse des ViewModels, das erzeugt werden soll
     * @return das neue ViewModel
     * @throws IllegalArgumentException wenn die ViewModel-Klasse nicht unterstützt wird
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}