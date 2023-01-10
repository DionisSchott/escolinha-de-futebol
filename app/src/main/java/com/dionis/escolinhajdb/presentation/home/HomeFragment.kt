package com.dionis.escolinhajdb.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.databinding.FragmentHomeBinding
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.util.Extensions.navTo


class HomeFragment : Fragment() {

    private val viewModel: ViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dados: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setUp()
    }

    private fun setUp() {

        binding.tvPlayers.setOnClickListener {
            navTo(R.id.action_homeFragment_to_registerPlayerFragment)
        }
    }

//    private fun readData() {
//
//
//        binding.ler.setOnClickListener {
//
//            dados = binding.nome.text.toString()
//            viewModel.read(dados)
//
//        }
//    }


}