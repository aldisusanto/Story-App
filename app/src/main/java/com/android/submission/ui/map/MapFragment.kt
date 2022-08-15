package com.android.submission.ui.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.submission.MainViewModel
import com.android.submission.R
import com.android.submission.core.source.remote.network.State
import com.android.submission.core.source.remote.response.item.ResultItem
import com.android.submission.core.utils.ViewModelUserFactory
import com.android.submission.databinding.FragmentMapBinding
import com.android.submission.session.SessionRepository
import com.android.submission.ui.login.LoginFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.toastInfo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapFragment : BaseFragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: MapViewModel by viewModel()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var mainViewModel: MainViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private var token: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        getToken()
    }

    private fun getToken() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelUserFactory(SessionRepository.getInstance(requireContext().dataStore))
        )[MainViewModel::class.java]
        token =
            requireActivity().intent.getParcelableExtra<ResultItem>(LoginFragment.EXTRA_USER)?.token
        if (token == null) {
            lifecycleScope.launchWhenCreated {
                launch {
                    mainViewModel.getUser().collect {
                        token = it.token
                    }
                }
            }
        }

    }

    private fun getLocation(token: String) {
        val query = 1
        viewModel.getAllStoryWithLocation("Bearer $token", query).observe(viewLifecycleOwner) {
            when (it.state) {
                State.SUCCESS -> {
                    progress.dismiss()
                    val dataArray = it.data?.listStory
                    dataArray?.forEach { story ->
                        val location = LatLng(story.lat, story.lon)
                        val title = story.name
                        val desc = story.description
                        mMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(title)
                                .snippet(desc)
                        )
                    }
                }
                State.LOADING -> {
                    progress.show()
                }
                State.ERROR -> {
                    progress.dismiss()
                    toastInfo(it.message ?: "Error")
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocation(token!!)

        val jakarta = LatLng(-6.23, 106.76)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 2f))
    }
}