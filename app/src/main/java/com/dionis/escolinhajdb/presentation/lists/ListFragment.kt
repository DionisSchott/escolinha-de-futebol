package com.dionis.escolinhajdb.presentation.lists

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.databinding.FragmentListBinding
import com.dionis.escolinhajdb.databinding.FragmentRegisterBinding


class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLists()
        setUp()
    }

    private fun setUp() {
        setObservers()
    }

    private fun setObservers() {
        viewModel.lists.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {

                }
                is UiState.Success -> {

                }
            }


        }
    }


}