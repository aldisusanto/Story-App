package com.android.submission.ui.story

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.android.submission.R
import com.android.submission.core.source.remote.network.State
import com.android.submission.databinding.FragmentAddStoryBinding
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.toMultipartBody
import com.inyongtisto.myhelper.extension.toastInfo
import com.inyongtisto.myhelper.extension.toastSuccess
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddStoryFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: AddStoryViewModel by viewModel()
    private var fileImage: File? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var token = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddStoryBinding.inflate(layoutInflater)
        root = binding?.root
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainButton()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        token = arguments?.getString("token")!!
    }


    private fun mainButton() {
        binding?.ivBtnToCamera?.setOnClickListener(this)
        binding?.btnPost?.setOnClickListener(this)
        binding?.ivBtnCurrentLocation?.setOnClickListener(this)
        binding?.ivClose?.setOnClickListener(this)
    }

    private fun fetchLocation() {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101
            )
            return
        }

        task.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
                binding?.apply {
                    cvLocation.visibility = View.VISIBLE
                    tvInformation.text = resources.getString(R.string.enable_location)

                }
            }

        }
    }

    private fun pickImage() {
        ImagePicker.with(requireActivity())
            .crop()
            .maxResultSize(1080, 1080, true)
            .createIntentFromDialog { launcher.launch(it) }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                fileImage = File(uri.path!!)
                Picasso.get()
                    .load(fileImage!!)
                    .into(binding?.ivPost)
            }
        }

    private fun post(token: String) {

        if (fileImage == null) {
            toastInfo("Please, unggah dokumen!")
        } else {
            val file = fileImage.toMultipartBody("photo")
            val description = binding?.tvDesc?.text.toString()
            viewModel.addStory(
                "Bearer $token",
                file!!,
                description.toRequestBody("text/plain".toMediaType()),
                currentLocation?.latitude,
                currentLocation?.longitude

            )
                .observe(viewLifecycleOwner) {
                    when (it.state) {
                        State.SUCCESS -> {
                            progress.dismiss()
                            val dataArray = it.data
                            toastSuccess(dataArray!!.message)
                            requireActivity().onBackPressed()
                        }
                        State.LOADING -> {
                            progress.show()
                        }
                        State.ERROR -> {
                            progress.dismiss()
                            toastInfo(it.message ?: "Info")
                        }
                    }
                }
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_btn_to_camera -> {
                pickImage()
            }
            R.id.btn_post -> {
                post(token)

            }
            R.id.iv_btn_current_location -> {
                fetchLocation()
            }
            R.id.iv_close -> {
                binding?.cvLocation?.visibility = View.GONE
                currentLocation = null
            }
        }
    }

}