package com.example.matchyourmunch.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.matchyourmunch.viewmodel.ListViewModel
import com.example.matchyourmunch.model.ListRepository
import com.example.matchyourmunch.helper.ListViewModelFactory
import com.example.matchyourmunch.helper.ListViewModelInterface

/**
 * Einstiegspunkt der App
 */
class MainActivity : ComponentActivity() {
    /**
     * initialisiert das Repository, das ViewModel und setzt die Hauptoberfläche
     * @param savedInstanceState enthält gespeicherte Daten aus vorherigen Sitzungen
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //das Repository
        val repository = ListRepository(applicationContext)
        //die Factory, die das ViewModel mit dem Repository erzeugt
        val factory = ListViewModelFactory(repository)
        //Viewmodel wird mit Factory und Interface erzeugt (Testbarkeit und Entkopplung)
        val viewModel: ListViewModelInterface = ViewModelProvider(this, factory)[ListViewModel::class.java]

        //Compose-Oberfläche starten
        setContent {
            val navController = rememberNavController()
            //App-Navigation mit ViewModel starten
            AppNavigation(navController = navController, listViewModel = viewModel, listIdOverride = -1)        }
    }
}