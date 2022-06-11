package com.example.seaselmobile.presentation.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.seaselmobile.R
import com.example.seaselmobile.utils.findMenuNavController
import com.example.seaselmobile.utils.setRoot
import com.example.seaselmobile.view.NavigationButtonView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BaseFragment : Fragment(R.layout.fragment_base) {

    private val viewModel: BaseViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var allSongsButton: NavigationButtonView
    private lateinit var favoriteButton: NavigationButtonView
    private lateinit var repetitionsButton: NavigationButtonView

    private val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        when (destination.id) {
            R.id.allSongsFragment -> {
                viewModel.setScreenCurrentScreen(BaseViewModel.CurrentScreen.AllSongsScreen)
            }
            R.id.repetitionsFragment -> {
                viewModel.setScreenCurrentScreen(BaseViewModel.CurrentScreen.RepetitionsScreen)
            }
            R.id.favoriteFragment -> {
                viewModel.setScreenCurrentScreen(BaseViewModel.CurrentScreen.FavoriteScreen)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findMenuNavController()

        allSongsButton = view.findViewById(R.id.baseAllSongsButton)
        favoriteButton = view.findViewById(R.id.baseFavoriteButton)
        repetitionsButton = view.findViewById(R.id.baseRepetitionsButton)

        allSongsButton.setOnClickListener {
            viewModel.toAllSongsScreen()
        }

        favoriteButton.setOnClickListener {
            viewModel.toFavoriteScreen()
        }

        repetitionsButton.setOnClickListener {
            viewModel.toRepetitionsScreen()
        }

        lifecycleScope.launch {
            viewModel.events.flowWithLifecycle(lifecycle).collectLatest {
                when (it) {
                    BaseViewModel.Event.ToAllSongsScreen -> {
                        navController.setRoot(R.id.allSongsFragment)
                    }
                    BaseViewModel.Event.ToRepetitionsScreen -> {
                        navController.setRoot(R.id.repetitionsFragment)
                    }
                    BaseViewModel.Event.ToFavoriteScreen -> {
                        navController.setRoot(R.id.favoriteFragment)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currentScreen.flowWithLifecycle(lifecycle).collectLatest {
                when (it) {
                    BaseViewModel.CurrentScreen.FavoriteScreen -> selectFavorite()
                    BaseViewModel.CurrentScreen.AllSongsScreen -> selectAllSongs()
                    BaseViewModel.CurrentScreen.RepetitionsScreen -> selectRepetitions()
                }
            }
        }
    }

    private fun selectAllSongs() {
        unselectAll()
        allSongsButton.buttonImage =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_all_songs_selected)
        allSongsButton.buttonTextColor = ContextCompat.getColor(requireContext(), R.color.main_blue)
    }

    private fun selectFavorite() {
        unselectAll()
        favoriteButton.buttonImage =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_favorite_selected)
        favoriteButton.buttonTextColor = ContextCompat.getColor(requireContext(), R.color.main_blue)
    }

    private fun selectRepetitions() {
        unselectAll()
        repetitionsButton.buttonImage =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_repetitions_selected)
        repetitionsButton.buttonTextColor = ContextCompat.getColor(requireContext(), R.color.main_blue)
    }

    private fun unselectAll() {
        allSongsButton.buttonImage =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_all_songs)
        repetitionsButton.buttonImage =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_repetitions)
        favoriteButton.buttonImage =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_favorite)
        allSongsButton.buttonTextColor = ContextCompat.getColor(requireContext(), R.color.black)
        favoriteButton.buttonTextColor = ContextCompat.getColor(requireContext(), R.color.black)
        repetitionsButton.buttonTextColor = ContextCompat.getColor(requireContext(), R.color.black)
    }

    override fun onResume() {
        navController.addOnDestinationChangedListener(listener)
        super.onResume()
    }

    override fun onPause() {
        navController.removeOnDestinationChangedListener(listener)
        super.onPause()
    }
}