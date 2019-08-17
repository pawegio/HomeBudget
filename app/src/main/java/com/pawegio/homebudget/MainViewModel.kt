package com.pawegio.homebudget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@FlowPreview
class MainViewModel(
    sheetsService: GoogleSheetsService
) : ViewModel(), CoroutineScope by MainScope() {

    val appState: LiveData<AppState> get() = _appState

    val mainEvents = Channel<MainAction>()

    private val _appState = MutableLiveData<AppState>()

    init {
        launch {
            MainFlow(
                mainEvents.consumeAsFlow(),
                _appState,
                sheetsService
            )
        }
    }
}
