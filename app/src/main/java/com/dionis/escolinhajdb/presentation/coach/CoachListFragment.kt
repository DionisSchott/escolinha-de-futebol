package com.dionis.escolinhajdb.presentation.coach

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.databinding.FragmentCoachListBinding
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.presentation.coach.DialogCoachDetailFragment.Companion.COACH
import com.dionis.escolinhajdb.util.Extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoachListFragment : Fragment() {

    private lateinit var binding: FragmentCoachListBinding
    private lateinit var coachListAdapter: CoachAdapter
    private val coachViewModel: ViewModel by viewModels()
    var coachList: List<Coach> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentCoachListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coachViewModel.getCoach()
        setup()
    }

    override fun onResume() {
        super.onResume()

        binding.coachFindEdt.setText("")

    }

    private fun setup() {
        setCoachAdapter()
        setCoachObservers()
        searchCoach()
    }


    private fun setCoachObservers() {

        coachViewModel.coach.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                    toast("falha")
                }
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    coachListAdapter.updateList(it.data)
                    coachList = it.data
                }
            }
        }
    }

    private fun setCoachAdapter() {
        coachListAdapter = CoachAdapter()

        coachListAdapter.onItemClicked = {

            val args = Bundle().apply { putParcelable(COACH, it) }

            val dialog = DialogCoachDetailFragment()
            dialog.show(childFragmentManager, dialog.tag)
            dialog.arguments = args
        }

        coachListAdapter.onDeleteClicked = {
            toast("deletedo")
        }

        binding.recyclerViewCoachList.adapter = coachListAdapter
    }

    private fun searchCoach() {

        binding.coachFindEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                val query = binding.coachFindEdt.text.toString()
                search(query)
            }
        })
        binding.coachFindLayout.setEndIconOnClickListener {
            // Chame o método de pesquisa quando o usuário pressionar o botão de pesquisa
            val query = binding.coachFindEdt.text.toString()
            search(query)
        }
    }

    fun search(query: String) {
        val filteredList = coachList.filter { document ->
            document.name.contains(query, ignoreCase = true)
        }
        coachListAdapter.updateList(filteredList)
    }


}