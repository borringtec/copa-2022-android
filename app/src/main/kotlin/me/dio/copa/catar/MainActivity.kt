package me.dio.copa.catar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import me.dio.copa.catar.extensions.observe
import me.dio.copa.catar.features.MainScreen
import me.dio.copa.catar.features.MainUiAction
import me.dio.copa.catar.features.MainViewModel
import me.dio.copa.catar.notification.scheduler.extensions.NotificationMatcherWorker
import me.dio.copa.catar.ui.theme.Copa2022Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeActions()
        setContent {
            Copa2022Theme {
                val state by viewModel.state.collectAsState()
                MainScreen(matches = state.matches, viewModel::toggleNotification)
            }
        }
    }

    private fun observeActions() {
        viewModel.action.observe(this) { action ->
            when (action) {
                is MainUiAction.MatchesNotFound -> {
                    Toast.makeText(this, action.message, Toast.LENGTH_SHORT).show()
                }

                MainUiAction.Unexpected -> {
                    Log.e("MainUiAction", "Ocorreu um erro inesperado")
                    Toast.makeText(
                        this,
                        "Algo deu errado, tente novamente mais tarde.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MainUiAction.DisableNotification -> {
                    NotificationMatcherWorker.cancel(applicationContext, action.match)
                }

                is MainUiAction.EnableNotification -> {
                    NotificationMatcherWorker.start(applicationContext, action.match)
                }
            }
        }
    }

}
