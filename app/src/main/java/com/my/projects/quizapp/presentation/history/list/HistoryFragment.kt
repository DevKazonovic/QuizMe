package com.my.projects.quizapp.presentation.history.list

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.my.projects.quizapp.QuizApplication
import com.my.projects.quizapp.R
import com.my.projects.quizapp.data.local.model.relations.QuizWithQuestionsAndAnswers
import com.my.projects.quizapp.databinding.FragmentHistoryBinding
import com.my.projects.quizapp.domain.enums.SortBy
import com.my.projects.quizapp.presentation.ViewModelProviderFactory
import com.my.projects.quizapp.presentation.history.list.adpter.HistoryAdapter
import com.my.projects.quizapp.util.Const.Companion.KEY_QUIZ_ID
import com.my.projects.quizapp.util.extensions.hide
import com.my.projects.quizapp.util.extensions.show
import timber.log.Timber
import javax.inject.Inject


class HistoryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private val viewModel: HistoryViewModel by navGraphViewModels(R.id.graph_history_list) {
        viewModelFactory
    }
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: HistoryAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as QuizApplication).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(
        savedInstanceState: Bundle?
    ) {
        super.onActivityCreated(savedInstanceState)

        /* binding.swipeRefresh.setOnRefreshListener {
             refresh()
             binding.swipeRefresh.isRefreshing = false
         }*/
        observeData()
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {
        when (item.itemId) {
            R.id.action_clear -> {
                showDeleteAlertDialog()
                return true
            }
            R.id.action_sort -> {
                showSortByDialog()
                return true
            }
            R.id.action_filter -> {
                showFilterDialog()
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        menu.clear()
        inflater.inflate(R.menu.menu_history, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.onSubmitSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onInstantSearch(newText)
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun observeData() {
        viewModel.quizzesMediatorLiveData.observe(viewLifecycleOwner, {
            onDisplayData(it)
        })
    }

    private fun onDisplayData(list: List<QuizWithQuestionsAndAnswers>) {
        if (list.isNullOrEmpty()) {
            onDisplayDataSatat(getString(R.string.all_empty_history))
        } else {
            binding.layoutErrors.hide()
            binding.layoutData.show()
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = HistoryAdapter(list, object : HistoryAdapter.ItemClickListener {
                override fun onItemClick(data: QuizWithQuestionsAndAnswers) {
                    navigateToDetailPage(data.quizEntity.id)
                }
            })
            binding.recyclerView.adapter = adapter
        }
    }

    private fun onDisplayDataSatat(message: String) {
        binding.layoutData.hide()
        binding.layoutErrors.show()
        binding.textViewError.text = message
    }

    private fun onRefresh() {
        viewModel.onRefresh()
    }

    private fun navigateToDetailPage(quizID: Long) {
        findNavController().navigate(
            R.id.action_history_to_quizDetail,
            bundleOf(KEY_QUIZ_ID to quizID)
        )
    }


    private fun showDeleteAlertDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.dialog_title_delete))
        }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Delete") { _, _ ->
                viewModel.onDeleteAllQuizzes()
            }
            .show()
    }

    private fun showSortByDialog() {
        val sortByItems = arrayOf(SortBy.LATEST, SortBy.OLDEST, SortBy.TITLE)
        val itemsForDialog = sortByItems.map { item -> item.name }.toTypedArray()
        var checkedItem = sortByItems.indexOf(viewModel.currentSortBy())

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.dialog_title_sort))
            .setNeutralButton(resources.getString(R.string.dialog_action_title_cancle)) { dialog, which ->
                Timber.d("Cancle")
            }
            .setPositiveButton(resources.getString(R.string.dialog_action_title_ok)) { dialog, which ->
                Timber.d("OK: $which")
                viewModel.onSortBy(sortByItems[checkedItem])
            }
            .setSingleChoiceItems(itemsForDialog, checkedItem) { dialog, which ->
                checkedItem = which
            }
            .show()
    }

    private fun showFilterDialog() {
        findNavController().navigate(R.id.action_history_to_dialogHistoryFilter)
    }

}