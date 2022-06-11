package com.example.seaselmobile.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.seaselmobile.R

fun Fragment.findGlobalNavController(): NavController {
    return requireActivity().findViewById<View>(R.id.mainNavHost)
        .findNavController()
}

fun Fragment.findMenuNavController(): NavController {
    return (childFragmentManager.findFragmentById(R.id.baseNavHost) as NavHostFragment)
        .findNavController()
}