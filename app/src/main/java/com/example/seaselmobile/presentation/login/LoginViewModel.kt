package com.example.seaselmobile.presentation.login

import androidx.lifecycle.ViewModel
import com.example.seaselmobile.RetrofitServices
import com.example.seaselmobile.StorageController
import com.example.seaselmobile.model.request.AuthRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val storageController: StorageController,
    private val api: RetrofitServices
) : ViewModel() {

    val events: Flow<Event>
        get() = _events
    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 5)

    val state: Flow<State>
        get() = _state
    private val _state: MutableStateFlow<State> = MutableStateFlow(State.LoginIdle)

    private val scope = MainScope()

    fun login(email: String, password: String) = scope.launch {
        try {
            _state.tryEmit(State.LoginLoading)
            val tokens = api.login(AuthRequestBody(email, password))
            storageController.setAccessToken(tokens.access)
            storageController.setRefreshToken(tokens.refresh)
            _events.tryEmit(Event.ToBaseScreen)
        } catch (e: HttpException) {
            e.printStackTrace()
            _events.tryEmit(Event.LoginError)
        } catch (e: Exception) {
            e.printStackTrace()
            _events.tryEmit(Event.Error)
        } finally {
            _state.tryEmit(State.LoginIdle)
        }
    }

    fun onFragmentStarted(){
        if (storageController.getRefreshToken() != null)
            _events.tryEmit(Event.ToBaseScreen)
    }

    sealed class Event {
        object ToBaseScreen : Event()
        object LoginError : Event()
        object Error : Event()
    }

    sealed class State {
        object LoginLoading : State()
        object LoginIdle : State()
    }
}