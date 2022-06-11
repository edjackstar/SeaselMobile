package com.example.seaselmobile.presentation.base

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class BaseViewModel @Inject constructor() : ViewModel() {

    val events: Flow<Event>
        get() = _events
    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 5)

    val currentScreen: Flow<CurrentScreen>
        get() = _currentScreen
    private val _currentScreen: MutableStateFlow<CurrentScreen> =
        MutableStateFlow(CurrentScreen.AllSongsScreen)

    fun toFavoriteScreen() {
        if (_currentScreen.value !is CurrentScreen.FavoriteScreen)
            _events.tryEmit(Event.ToFavoriteScreen)
    }

    fun toAllSongsScreen() {
        if (_currentScreen.value !is CurrentScreen.AllSongsScreen)
            _events.tryEmit(Event.ToAllSongsScreen)
    }

    fun toRepetitionsScreen() {
        if (_currentScreen.value !is CurrentScreen.RepetitionsScreen)
            _events.tryEmit(Event.ToRepetitionsScreen)
    }

    fun setScreenCurrentScreen(screen: CurrentScreen) {
        _currentScreen.tryEmit(screen)
    }

    sealed class Event {
        object ToFavoriteScreen : Event()
        object ToAllSongsScreen : Event()
        object ToRepetitionsScreen : Event()
    }

    sealed class CurrentScreen {
        object FavoriteScreen : CurrentScreen()
        object AllSongsScreen : CurrentScreen()
        object RepetitionsScreen : CurrentScreen()
    }
    override fun onCleared() {
        super.onCleared()
//        cancelAll()
    }
}