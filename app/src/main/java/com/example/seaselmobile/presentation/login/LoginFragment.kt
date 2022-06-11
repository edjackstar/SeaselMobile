package com.example.seaselmobile.presentation.login

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.seaselmobile.R
import com.example.seaselmobile.utils.findGlobalNavController
import com.example.seaselmobile.utils.setRoot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginEnterButton = view.findViewById<TextView>(R.id.loginEnterButton)
        val loginEmail = view.findViewById<EditText>(R.id.loginEmail)
        val loginPassword = view.findViewById<EditText>(R.id.loginPassword)
        val navigation = findGlobalNavController()

        loginEnterButton.setOnClickListener {
            viewModel.login(loginEmail.text.toString(), loginPassword.text.toString())
        }
        try {
            lifecycleScope.launch {
                try {
                    viewModel.events.collectLatest {
                        when (it) {
                            LoginViewModel.Event.Error -> {
                                Toast.makeText(requireContext(), "can't login", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            LoginViewModel.Event.LoginError -> {
                                Toast.makeText(
                                    requireContext(),
                                    "wrong login or password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            LoginViewModel.Event.ToBaseScreen -> {
                                navigation.setRoot(R.id.baseFragment)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onFragmentStarted()
    }
}