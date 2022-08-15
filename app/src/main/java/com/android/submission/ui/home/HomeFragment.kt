package com.android.submission.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.submission.MainViewModel
import com.android.submission.R
import com.android.submission.core.source.remote.response.item.ResultItem
import com.android.submission.core.source.remote.response.item.StoryItem
import com.android.submission.core.utils.ViewModelUserFactory
import com.android.submission.databinding.FragmentHomeBinding
import com.android.submission.session.SessionRepository
import com.android.submission.ui.detail.DetailActivity
import com.android.submission.ui.login.LoginFragment
import com.google.gson.Gson
import com.inyongtisto.myhelper.base.BaseFragment
import com.inyongtisto.myhelper.extension.toastSuccess
import io.reactivex.disposables.Disposables
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess


class HomeFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private var root: View? = null
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var mainViewModel: MainViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private lateinit var storyAdapter: StoryAdapter
    private var token: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // handle back event
            activity?.finishAndRemoveTask()
            exitProcess(0)
        }

        mainButton()

        initSwipeToRefresh()

        getToken()
        setList(token!!)


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

    private fun initSwipeToRefresh() {
        binding?.swipeRefresh?.setOnRefreshListener { storyAdapter.refresh() }
    }

    private fun mainButton() {
        binding?.apply {
            btnAddStory.setOnClickListener(this@HomeFragment)
            btnLanguage.setOnClickListener(this@HomeFragment)
            btnLogout.setOnClickListener(this@HomeFragment)
        }
    }

    private fun setList(token: String) {
        storyAdapter = StoryAdapter(requireContext())
        with(binding?.rvStory) {
            this?.layoutManager = LinearLayoutManager(requireActivity())
            this?.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
        viewLifecycleOwner.lifecycleScope.launch {
            storyAdapter.loadStateFlow.collectLatest { loadStates ->
                binding?.viewError?.root?.isVisible = loadStates.refresh is LoadState.Error
            }
            if (storyAdapter.itemCount < 1) binding?.viewError?.root?.visibility =
                View.VISIBLE
            else binding?.viewError?.root?.visibility = View.GONE
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            storyAdapter.loadStateFlow.collect {
                binding?.swipeRefresh?.isRefreshing =
                    it.mediator?.refresh is LoadState.Loading
            }
        }
        viewModel.getAllStory("Bearer $token").observe(viewLifecycleOwner) { story ->
            storyAdapter.submitData(lifecycle, story)
        }


        storyAdapter.setOnItemClickCallback((object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(storyItem: StoryItem) {
                val json = Gson().toJson(storyItem, StoryItem::class.java)
                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra("story", json)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity)
                        .toBundle()
                )

            }
        }))
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_add_story -> {
                val bundle = Bundle()
                bundle.putString("token", token)
                findNavController().navigate(
                    R.id.action_homeFragment_to_addStoryFragment,
                    bundle
                )
            }

            R.id.btn_logout -> {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    mainViewModel.logout()
                    toastSuccess("Logout Success")
                    val mBundle = Bundle()
                    mBundle.putString("fromProfile", "FROM_PROFILE")
                    findNavController().navigate(
                        R.id.action_navigation_list_to_loginFragment2,
                        mBundle
                    )
                }

                builder.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
                builder.setTitle(resources.getString(R.string.title_logout))
                builder.setMessage(resources.getString(R.string.logout_info))
                builder.create().show()
            }
            R.id.btn_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        root = null
        Disposables.disposed()
    }

}