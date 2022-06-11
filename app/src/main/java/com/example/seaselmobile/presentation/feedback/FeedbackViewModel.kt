package com.example.seaselmobile.presentation.feedback

import androidx.lifecycle.ViewModel
import com.example.seaselmobile.RetrofitServices
import com.example.seaselmobile.StorageController
import com.example.seaselmobile.model.request.FeedbackRequestBody
import com.example.seaselmobile.presentation.all_songs.AllSongsViewModel
import com.example.seaselmobile.utils.RefreshToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val refreshToken: RefreshToken,
    private val storageController: StorageController,
    private val api: RetrofitServices,
) : ViewModel() {

    val events: Flow<Event>
        get() = _events
    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 5)

    fun onFeedbackClick(compositionId: Int, comment: String, mark: Float) {
        leaveFeedback(
            FeedbackRequestBody(compositionId, mark, comment)
        )
    }

    private fun leaveFeedback(feedback: FeedbackRequestBody) = MainScope().launch {
        try {
            api.leaveFeedback(
                "Bearer ${storageController.getAccessToken()}",
                feedback
            )

        } catch (e: HttpException) {
            doRefresh()
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
            _events.tryEmit(Event.FeedbackError)
        }
    }

    fun goBack() {
        _events.tryEmit(Event.ToBase)
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
        object ToBase : Event()
        object ToLogin : Event()
        object FeedbackError : Event()
    }
}