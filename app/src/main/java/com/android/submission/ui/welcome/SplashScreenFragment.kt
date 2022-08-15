package com.android.submission.ui.welcome

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.submission.MainViewModel
import com.android.submission.R
import com.android.submission.core.utils.ViewModelUserFactory
import com.android.submission.session.SessionRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private lateinit var viewModel: MainViewModel
    private var isLogin: Boolean? = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        viewModel = ViewModelProvider(
            this,
            ViewModelUserFactory(SessionRepository.getInstance(requireContext().dataStore))
        )[MainViewModel::class.java]

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getUser().collect {
                    isLogin = it.isLogin

                }
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (isLogin!!) {
                findNavController().navigate(
                    R.id.action_splashScreenFragment_to_homeActivity
                )
            } else {
                findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
            }
        }, 3000)
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }


}