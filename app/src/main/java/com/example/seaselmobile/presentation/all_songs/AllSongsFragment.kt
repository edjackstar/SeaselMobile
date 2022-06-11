package com.example.seaselmobile.presentation.all_songs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seaselmobile.R
import com.example.seaselmobile.presentation.repetition.RepetitionActivity
import com.example.seaselmobile.presentation.CompositionsAdapter
import com.example.seaselmobile.presentation.favorite.FavoriteFragment
import com.example.seaselmobile.presentation.repetitions.RepetitionsFragment
import com.example.seaselmobile.utils.findGlobalNavController
import com.example.seaselmobile.utils.replace
import com.example.seaselmobile.utils.setRoot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AllSongsFragment : Fragment(R.layout.fragment_all_songs) {

    private val viewModel: AllSongsViewModel by viewModels()
    private var adapter: CompositionsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val musicListRecycler = view.findViewById<RecyclerView>(R.id.allSongsRecycler)

        val newAdapter = CompositionsAdapter(listOf())

        newAdapter.onItemClick = {
            viewModel.goToRepetition(it)
        }

        newAdapter.onFeedbackClick = {
            viewModel.goToFeedback(it)
        }

        newAdapter.onAddFavoriteClick = {
            viewModel.addFavorite(it)
        }

        newAdapter.onRemoveFavoriteClick = {
            viewModel.removeFavorite(it)
        }

        musicListRecycler.layoutManager = LinearLayoutManager(requireContext())
        musicListRecycler.adapter = newAdapter

        val navigation = findGlobalNavController()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                when (it) {
                    AllSongsViewModel.Event.ShowLoadingError -> {
                        Toast.makeText(requireContext(), "can't load compositions", Toast.LENGTH_SHORT).show()
                    }
                    AllSongsViewModel.Event.ToLogin -> {
                        navigation.setRoot(R.id.loginFragment)
                    }
                    is AllSongsViewModel.Event.ToRepetition -> {
                        startActivity(
                            Intent(requireContext(), RepetitionActivity::class.java)
                                .putExtra(
                                    REPETITION_ID,
                                    it.repetition.composition_representation_id
                                )
                                .putExtra(
                                    COMPOSITION_ID,
                                    it.repetition.composition_id
                                )
                                .putExtra(REPETITION_AUTHOR, it.repetition.author)
                                .putExtra(REPETITION_NAME, it.repetition.name)
                        )
                    }
                    is AllSongsViewModel.Event.ToFeedback -> {
                        navigation.backQueue
                        val bundle = Bundle().apply { putInt(RepetitionsFragment.COMPOSITION_ID, it.id) }
                        navigation.navigate(R.id.action_baseFragment_to_feedbackFragment, bundle)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.musicList.flowWithLifecycle(lifecycle).collectLatest {
                newAdapter.compositions = it
                newAdapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch {
            viewModel.favorites.flowWithLifecycle(lifecycle).collectLatest {
                newAdapter.favorites = it
                newAdapter.notifyDataSetChanged()
            }
        }

        adapter = newAdapter
    }

    companion object {
        const val COMPOSITION_ID = "COMPOSITION_ID"
        const val REPETITION_ID = "REPETITION_ID"
        const val REPETITION_AUTHOR = "REPETITION_AUTHOR"
        const val REPETITION_NAME = "REPETITION_NAME"
    }
}