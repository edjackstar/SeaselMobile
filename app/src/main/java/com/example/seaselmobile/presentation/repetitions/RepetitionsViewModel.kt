package com.example.seaselmobile.presentation.repetitions

import android.os.storage.StorageManager
import androidx.lifecycle.ViewModel
import com.example.seaselmobile.RetrofitServices
import com.example.seaselmobile.StorageController
import com.example.seaselmobile.model.api.CompositionRepresentationApiModel
import com.example.seaselmobile.utils.RefreshToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


@HiltViewModel
class RepetitionsViewModel @Inject constructor(
    private val refreshToken: RefreshToken,
    private val storageController: StorageController,
    private val api: RetrofitServices,
) : ViewModel() {
    val events: Flow<Event>
        get() = _events
    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 5)

    val musicList: Flow<List<CompositionRepresentationApiModel>>
        get() = _musicList
    private val _musicList: MutableStateFlow<List<CompositionRepresentationApiModel>> =
        MutableStateFlow(listOf())

    val favorites: Flow<List<Int>>
        get() = _favorites
    private val _favorites: MutableStateFlow<List<Int>> =
        MutableStateFlow(listOf())

    private val scope = MainScope()

    init {
        _favorites.tryEmit(storageController.getFavorites())
        getMusicList()
    }

    private fun getMusicList() = scope.launch {
        doRefresh()

        try {
            _musicList.tryEmit(api.getUserMusicList("Bearer ${storageController.getAccessToken()}"))
        } catch (e: HttpException) {
            doRefresh()
            e.printStackTrace()
        } catch (e: Exception) {
            _events.tryEmit(Event.ShowLoadingError)
            e.printStackTrace()
        }
    }

    fun addFavorite(id: Int) {
        _favorites.tryEmit(_favorites.value + id)
        storageController.setFavorites(_favorites.value)
    }

    fun removeFavorite(id: Int) {
        _favorites.tryEmit(_favorites.value.filterNot { it == id })
        storageController.setFavorites(_favorites.value)
    }

    fun goToFeedback(id: Int) {
        _events.tryEmit(Event.ToFeedback(id))
    }

    fun goToRepetition(id: Int) {

        _events.tryEmit(Event.ToRepetition(_musicList.value.first { it.composition_id == id }))
    }

    private suspend fun doRefresh() {
        try {
            refreshToken()
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                storageController.setAccessToken(null)
                storageController.setRefreshToken(null)
                _events.tryEmit(Event.ToLogin)
            }
        }
    }

    sealed class Event {
        object ShowLoadingError : Event()
        object ToLogin : Event()
        class ToRepetition(val repetition: CompositionRepresentationApiModel) : Event()
        class ToFeedback(val id: Int) : Event()
    }
}