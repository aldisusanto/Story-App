package com.android.submission.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.submission.R
import com.android.submission.core.source.remote.network.State
import com.android.submission.core.source.remote.response.item.ResultItem
import com.android.submission.databinding.FragmentLoginBinding
import com.android.submission.session.SessionRepository
import com.android.submission.ui.HomeActivity
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : BaseFragment(), View.OnClickListener {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: LoginViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        root = binding?.root
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainButton()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            activity?.finishAndRemoveTask()
        }

    }


    private fun mainButton() {
        binding?.apply {
            btnLogin.setOnClickListener(this@LoginFragment)
            btnToRegister.setOnClickListener(this@LoginFragment)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_login -> {
                val email = binding?.edtEmail?.text
                val password = binding?.edtPassword?.text
                viewModel.login(email.toString(), password.toString()).observe(viewLifecycleOwner) {
                    when (it.state) {
                        State.SUCCESS -> {
                            val dataArray = it.data
                            val user = ResultItem(
                                dataArray?.result?.name.toString(),
                                dataArray?.result?.userId.toString(),
                                dataArray?.result?.token.toString(),
                                true

                            )
                            activity?.dismisLoading()
                            toastSuccess(resources.getString(R.string.welcome))

                            val intent = Intent(requireActivity(), HomeActivity::class.java)
                            intent.putExtra(EXTRA_USER, user)
                            startActivity(intent)
                            requireActivity().finish()

                            val sessionRepository =
                                SessionRepository.getInstance(requireContext().dataStore)
                            lifecycleScope.launchWhenStarted {
                                sessionRepository.saveUser(user)
                            }
                        }
                        State.LOADING -> {
                            activity?.showLoading()
                        }
                        State.ERROR -> {
                            activity?.dismisLoading()
                            when (it.message) {
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
            R.id.btn_to_register -> {
                val mBundle = Bundle()
                if (arguments != null) {
                    val profile = requireArguments().getString("fromProfile")
                    val register = requireArguments().getString("fromRegisterNav")

                    if (profile == FROM_PROFILE || register == FROM_REGISTER_NAV) {
                        mBundle.putString("fromLoginNav", "FROM_LOGIN_NAV")
                        findNavController().navigate(
                            R.id.action_loginFragment2_to_registerFragment2,
                            mBundle
                        )
                    }
                } else {
                    findNavController().navigate(
                        R.id.action_loginFragment_to_registerFragment,
                        mBundle
                    )
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_USER = "user"
        const val FROM_PROFILE = "FROM_PROFILE"
        const val FROM_REGISTER_NAV = "FROM_REGISTER_NAV"
    }


}