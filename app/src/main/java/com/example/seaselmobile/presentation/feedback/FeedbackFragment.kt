package com.example.seaselmobile.presentation.feedback

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.seaselmobile.R
import com.example.seaselmobile.utils.findGlobalNavController
import com.example.seaselmobile.utils.setRoot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FeedbackFragment : Fragment(R.layout.fragment_feedback) {

    private val viewModel: FeedbackViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val comment = view.findViewById<EditText>(R.id.feedbackComment)
        val rating = view.findViewById<RatingBar>(R.id.feedbackRating)
        val button = view.findViewById<TextView>(R.id.feedbackSendButton)
        val backButton = view.findViewById<ImageView>(R.id.feedbackBackButton)
        val compositionId = arguments?.getInt(COMPOSITION_ID, 0) ?: 0

        button.setOnClickListener {
            viewModel.onFeedbackClick(compositionId, comment.text.toString(), rating.rating)
        }

        backButton.setOnClickListener {
            viewModel.goBack()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    viewModel.goBack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        lifecycleScope.launch {
            viewModel.events.flowWithLifecycle(lifecycle).collectLatest {
                when (it) {
                    FeedbackViewModel.Event.FeedbackError -> {
                        Toast.makeText(requireContext(), "Can't send feedback", Toast.LENGTH_SHORT)
                            .show()
                    }
                    FeedbackViewModel.Event.ToBase -> findGlobalNavController().popBackStack()
                    FeedbackViewModel.Event.ToLogin -> {
                        findGlobalNavController().setRoot(R.id.loginFragment)
                    }
                }
            }
        }
    }

    companion object {
        const val COMPOSITION_ID = "COMPOSITION_ID"
    }
}