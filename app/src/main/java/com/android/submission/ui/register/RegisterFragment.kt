package com.android.submission.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.submission.R
import com.android.submission.core.source.remote.network.State
import com.android.submission.databinding.FragmentRegisterBinding
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentRegisterBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainButton()

    }

    private fun mainButton() {
        binding?.apply {
            btnRegister.setOnClickListener(this@RegisterFragment)
            btnToLogin.setOnClickListener(this@RegisterFragment)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_register -> {

                val name = binding?.edtName?.text.toString()
                val email = binding?.edtEmail?.text.toString()
                val password = binding?.edtPassword?.text.toString()
                viewModel.register(name, email, password).observe(viewLifecycleOwner) {
                    when (it.state) {
                        State.SUCCESS -> {
                            val dataArray = it.data
                            activity?.dismisLoading()
                            toastError(dataArray!!.message)
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

                        }
                        State.LOADING -> {
                            activity?.showLoading()
                        }
                        State.ERROR -> {
                            activity?.dismisLoading()
                            when (it.message) {
                                "\"name\" is not allowed to be empty" -> {
                                    toastInfo(resources.getString(R.string.empty_name))
                                }
                                "\"email\" is not allowed to be empty" -> {
                                    toastInfo(resources.getString(R.string.empty_email))
                                }
                                "\"password\" is not allowed to be empty" -> {
                                    toastInfo(resources.getString(R.string.empty_password))
                                }
                                else -> {
                                    toastInfo(it.message ?: "Pesan")
                                }
                            }
                        }
                    }
                }
            }
            R.id.btn_to_login -> {
                if (arguments != null) {
                    val login = requireArguments().getString("fromRegisNav")
                    val mBundle = Bundle()
                    if (login == FROM_LOGIN_NAV) {
                        mBundle.putString("fromRegisterNav", "FROM_REGISTER_NAV")
                        findNavController().navigate(
                            R.id.action_registerFragment2_to_loginFragment2,
                            mBundle
                        )
                    }
                } else {
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
        }
    }

    companion object {
        const val FROM_LOGIN_NAV = "FROM_LOGIN_NAV"
    }

}